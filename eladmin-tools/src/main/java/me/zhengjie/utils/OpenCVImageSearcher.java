package me.zhengjie.utils;

import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.opencv_java;
import org.opencv.core.*;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.Feature2D;
import org.opencv.features2d.ORB;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

@Slf4j
@Component
public class OpenCVImageSearcher {

    @Value("${file.mac.imagePath}")
    private String macFilePath;
    @Value("${file.linux.imagePath}")
    private String linuxFilePath;
    @Value("${file.windows.imagePath}")
    private String windowsFilePath;


    static {
        Loader.load(opencv_java.class);
    }

    private final List<ImageData> imageDatabase = new ArrayList<>();
    private final Feature2D featureDetector;
    private final DescriptorMatcher matcher;
    public OpenCVImageSearcher() {
        // 使用ORB特征检测器（适合中小规模图像库）
        this.featureDetector = ORB.create();
        // 使用汉明距离匹配器（适合ORB特征）
        this.matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
    }

    /**
     * 构建图像特征数据库
     */
    @PostConstruct
    public void buildImageDatabase() {
        File directory = new File(getFilePath(macFilePath, linuxFilePath, windowsFilePath));
        if (directory.isDirectory()) {
            processDirectory(directory);
        }
        log.info("图像数据库构建完成，共{}张有效图片", imageDatabase.size());
    }

    /**
     * 搜索并加载目录中compress文件夹内的图片
     */
    private void processDirectory(File directory) {
        File[] subDirectories = directory.listFiles();
        if (subDirectories != null) {
            for (File subDirectory : subDirectories) {
                if (subDirectory.isDirectory() && subDirectory.getName().matches("compress")) {
                    processSubDirectory(subDirectory);
                } else {
                    processDirectory(subDirectory);
                }
            }
        }
    }

    /**
     * 搜索并加载子目录中compress文件夹内的图片
     */
    private void processSubDirectory(File subDirectory) {
        File[] imageFiles = subDirectory.listFiles((dir, name) -> name.toLowerCase().matches(".*\\.(jpg|jpeg|png|gif|bmp)"));
        if (imageFiles == null) {
            return;
        }
        for (File file : imageFiles) {
            try {
                Mat image = Imgcodecs.imread(file.getAbsolutePath(), Imgcodecs.IMREAD_GRAYSCALE);
                if (image.empty()) {
                    System.err.println("无法读取图片: " + file.getName());
                    continue;
                }

                // 检测特征点和计算描述符
                MatOfKeyPoint keyPoints = new MatOfKeyPoint();
                Mat descriptors = new Mat();
                featureDetector.detectAndCompute(image, new Mat(), keyPoints, descriptors);

                if (!descriptors.empty()) {
                    imageDatabase.add(new ImageData(file.getAbsolutePath(), descriptors));
                } else {
                    log.error("无法提取特征: {}", file.getName());
                }
            } catch (Exception e) {
                log.error("处理图片出错:{}", file.getName(), e);
            }
        }
    }

    public static String getFilePath(String macFilePath, String linuxFilePath, String windowsFilePath) {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("mac")) {
            log.info("os.name:{}, filePath:{}", os, macFilePath);
            return macFilePath;
        } else if (os.contains("win")) {
            log.info("os.name:{}, filePath:{}", os, windowsFilePath);
            return windowsFilePath;
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            log.info("os.name:{}, filePath:{}", os, linuxFilePath);
            return linuxFilePath;
        } else {
            log.info("os.name:{}, filePath:{}", os, linuxFilePath);
            return linuxFilePath;
        }
    }

    /**
     * 搜索相似图片
     *
     * @param topN  返回最相似的N张图片
     * @param bytes 图片文件字节数组
     * @return 包含图片信息和相似度的列表
     */
    public List<SearchResult> searchSimilarImages(int topN, byte[] bytes) throws IOException {
        List<SearchResult> results = new ArrayList<>();
        // 读取查询图片
        Mat queryImage = Imgcodecs.imdecode(new MatOfByte(bytes), Imgcodecs.IMREAD_GRAYSCALE);
        if (queryImage.empty()) {
            return results;
        }

        // 提取查询图片特征
        MatOfKeyPoint queryKeyPoints = new MatOfKeyPoint();
        Mat queryDescriptors = new Mat();
        featureDetector.detectAndCompute(queryImage, new Mat(), queryKeyPoints, queryDescriptors);

        if (queryDescriptors.empty()) {
            return results;
        }

        // 与数据库中的图片进行匹配
        PriorityQueue<SearchResult> resultQueue = new PriorityQueue<>(topN, Comparator.comparingDouble(a -> a.similarity));
        for (ImageData imageData : imageDatabase) {
            try {
                MatOfDMatch matches = new MatOfDMatch();
                matcher.match(queryDescriptors, imageData.descriptors, matches);

                // 计算平均相似度（距离越小越相似）
                double totalDistance = 0;
                for (DMatch match : matches.toArray()) {
                    totalDistance += match.distance;
                }
                double avgDistance = totalDistance / matches.rows();

                // 将距离转换为相似度（0-1之间，1表示最相似）
                double similarity = 1 - normalize(avgDistance, 0, 100);

                resultQueue.offer(new SearchResult(
                        imageData.filePath,
                        imageData.fileName,
                        similarity
                ));

                // 保持队列中只有topN个结果
                while (resultQueue.size() > topN) {
                    resultQueue.poll();
                }
            } catch (Exception e) {
                System.err.println("匹配出错: " + imageData.fileName);
            }
        }

        // 将结果转为列表
        while (!resultQueue.isEmpty()) {
            results.add(0, resultQueue.poll()); // 按相似度从高到低排序
        }

        return results;
    }

    // 归一化函数
    private double normalize(double value, double min, double max) {
        if (value < min) return 0;
        if (value > max) return 1;
        return (value - min) / (max - min);
    }

    // 存储图像特征的数据结构
    private static class ImageData {
        String filePath;
        String fileName;
        Mat descriptors;

        ImageData(String filePath, Mat descriptors) {
            this.filePath = filePath;
            this.fileName = new File(filePath).getName();
            this.descriptors = descriptors;
        }
    }

    /**
     * 搜索结果数据结构
     */
    public static class SearchResult {
        public final String filePath;  // 图片完整路径
        public final String fileName;  // 图片文件名
        public final double similarity; // 相似度（0-1）

        public SearchResult(String filePath, String fileName, double similarity) {
            this.filePath = filePath;
            this.fileName = fileName;
            this.similarity = similarity;
        }

        @Override
        public String toString() {
            return String.format("[相似度: %.2f] %s", similarity, fileName);
        }
    }

}