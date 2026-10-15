package me.zhengjie.modules.business.service.picture;

import lombok.extern.slf4j.Slf4j;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.business.rest.request.picture.FormatPicSuffixRequest;
import me.zhengjie.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * @Description ：description
 * @Author ：wangpengfei
 * @Date ：created in 2025/4/19
 */
@Slf4j
@Service
public class FormatPicSuffixService {
    private static final String DEFAULT_PIC_SUFFIX = "JPG";

    /**
     * 把 sourcePath 下的 jpeg / jpg 统一成 .JPG，使用方式见 PictureUtilsController#formatPicSuffix 上的说明。
     */
    public Integer formatPicSuffix(FormatPicSuffixRequest request) throws IOException {
        String sourcePath = request.getSourcePath();
        // 目标路径没传，则与源路径保持一致（即原地重命名）
        String targetPath = StringUtils.isEmpty(request.getTargetPath()) ? sourcePath : request.getTargetPath();
        File sourceDirectory = new File(sourcePath);

        // 1 校验
        if(!sourceDirectory.exists()){
            throw new BadRequestException("文件夹路径不存在：" + sourcePath);
        }

        if(!sourceDirectory.isDirectory()){
            throw new BadRequestException("不是一个文件夹路径：" + sourcePath);
        }

        File targetDirectory = new File(targetPath);
        if(targetDirectory.exists() && !targetDirectory.isDirectory()){
            throw new BadRequestException("不是一个文件夹路径：" + targetPath);
        }
        // 目标文件路径要是不存在，则创建
        if(!targetDirectory.exists()){
            Files.createDirectories(targetDirectory.toPath());
        }

        // 源目录和目标目录是同一个，则原地重命名；否则输出到目标目录，源目录保留
        boolean sameDirectory = sourceDirectory.getCanonicalFile().equals(targetDirectory.getCanonicalFile());

        File[] files = sourceDirectory.listFiles();
        if(null == files){
            throw new BadRequestException("文件夹下没有文件：" + sourcePath);
        }

        Integer count = 0;
        for(File sourceFile : files){
            if(sourceFile.isDirectory()){
                continue;
            }

            String fileName = sourceFile.getName();
            int dotIndex = fileName.lastIndexOf('.');
            if(dotIndex <= 0){
                continue;
            }
            String fileSuffix = fileName.substring(dotIndex + 1);
            // 只能是指定格式 才能被识别
            boolean needFormatSuffix = "jpeg".equalsIgnoreCase(fileSuffix)
                    || "jpg".equalsIgnoreCase(fileSuffix);
            boolean alreadyTargetSuffix = DEFAULT_PIC_SUFFIX.equals(fileSuffix);
            if(!needFormatSuffix && !alreadyTargetSuffix){
                continue;
            }
            // 已经是目标后缀 且 原地处理，则无需处理
            if(sameDirectory && alreadyTargetSuffix){
                continue;
            }

            String newName = fileName.substring(0, dotIndex) + "." + DEFAULT_PIC_SUFFIX;
            Path source = sourceFile.toPath();
            Path target = targetDirectory.toPath().resolve(newName);
            if(Files.exists(target) && !Files.isSameFile(source, target)){
                log.warn("目标文件已存在，将被覆盖：{}", target);
            }

            if(sameDirectory){
                Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
            } else {
                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            }
            count++;
        }

        return count;

    }
}
