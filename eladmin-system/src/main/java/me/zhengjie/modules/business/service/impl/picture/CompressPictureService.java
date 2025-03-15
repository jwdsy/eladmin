package me.zhengjie.modules.business.service.impl.picture;

import lombok.extern.slf4j.Slf4j;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.business.rest.request.picture.CompressPictureRequest;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @Description ：description
 * @Author ：wangpengfei
 * @Date ：created in 2025/3/10
 */
@Slf4j
@Service
public class CompressPictureService {
    private static final Integer DEFAULT_SIDE_LENGTH = 800;
    private static final float DEFAULT_OUTPUT_QUALITY = 0.2f;


    public Integer compressPicture(CompressPictureRequest request) throws IOException {
        String sourcePath = request.getSourcePath();
        String targetPath = sourcePath + "/compress";
        Integer sideLength = request.getSideLength();
        if(null == sideLength || sideLength <= 0){
            sideLength = DEFAULT_SIDE_LENGTH;
        }
        Float outputQuality = request.getOutputQuality();
        if(null == outputQuality || outputQuality > 1.0f || outputQuality <= 0.0f){
            outputQuality = DEFAULT_OUTPUT_QUALITY;
        }

        File file = new File(sourcePath);

        // 1 校验
        if(!file.exists()){
            throw new BadRequestException("文件夹路径不存在：" + sourcePath);
        }

        if(!file.isDirectory()){
            throw new BadRequestException("不是一个文件夹路径：" + sourcePath);
        }

        // 目标文件路径要是不存在，则创建
        Path target = Paths.get(targetPath);
        if(!Files.exists(target)){
            Files.createDirectory(target);
        }

        File[] files = file.listFiles();
        if(null == files){
            throw new BadRequestException("文件夹下没有文件：" + sourcePath);
        }
        Integer count = 0;

        // 循环压缩每一张图片
        for(File sourceFile : files){
            if(sourceFile.isDirectory()){
                continue;
            }
            // 只能是.jpeg 才能被识别
            if(!sourceFile.getName().endsWith(".jpeg")){
                continue;
            }
            File outputFile = new File(targetPath, sourceFile.getName());
//            System.out.println(outputFile.getPath());
            // 计算压缩质量
//                float quality = calculateQuality(sourceFile.length(), picTargetSize);
//                float quality = 0.1f;
//                if (sourceFile.length() <= picTargetSize) {
//                    quality = 1.0f; // 如果文件已经小于目标大小，则不压缩
//                }

            Thumbnails.of(sourceFile) // 用于加载输入文件
                    .size(sideLength, sideLength) //设置缩略图的宽度和高度
//                .scale(1.0f) // 表示保持原始尺寸
                    .outputQuality(outputQuality) // 设置压缩质量（0.0f 到 1.0f）
                    .toFile(outputFile); // 将压缩后的图片保存到输出文件
            // 保存压缩后的图片
//            System.out.println("name:" + sourceFile.getName() + " size:" + sourceFile.length());

            count++;
        }
        return count;
    }
}
