package me.zhengjie.modules.business.service.picture;

import lombok.extern.slf4j.Slf4j;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.business.rest.request.picture.FormatPicSuffixRequest;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @Description ：description
 * @Author ：wangpengfei
 * @Date ：created in 2025/4/19
 */
@Slf4j
@Service
public class FormatPicSuffixService {
    private static final String DEFAULT_PIC_SUFFIX = "JPG";

    public Integer formatPicSuffix(FormatPicSuffixRequest request) throws IOException {
        String sourcePath = request.getSourcePath();
        String targetPath = request.getTargetPath();
        File file = new File(sourcePath);

        // 1 校验
        if(!file.exists()){
            throw new BadRequestException("文件夹路径不存在：" + sourcePath);
        }

        if(!file.isDirectory()){
            throw new BadRequestException("不是一个文件夹路径：" + sourcePath);
        }

        Path target = Paths.get(targetPath);
        if(!Files.exists(target)){
            Files.createDirectory(target);
        }

        File[] files = file.listFiles();
        if(null == files){
            throw new BadRequestException("文件夹下没有文件：" + sourcePath);
        }

        Integer count = 0;
        for(File sourceFile : files){
            if(sourceFile.isDirectory()){
                continue;
            }

            String[] fileNameArray = sourceFile.getName().split("\\.");
            if(fileNameArray.length != 2){
                continue;
            }
            String fileName = fileNameArray[0];
            String fileSuffix = fileNameArray[1];
            // 只能是指定格式 才能被识别
            if(!".jpeg".equalsIgnoreCase(fileSuffix)
                    && !"jpg".equalsIgnoreCase(fileSuffix)){
                continue;
            }

        }


        return count;

    }
}
