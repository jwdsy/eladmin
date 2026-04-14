package me.zhengjie.modules.biz.service.embedding;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface EmbeddingService {

    boolean isEnabled();

    float[] embed(MultipartFile file) throws IOException;

    float[] embed(byte[] imageBytes) throws IOException;

    int dim();
}

