package me.zhengjie.modules.biz.config;

import lombok.Data;

@Data
public class ImageSearchEmbeddingProperties {
    private boolean enabled = false;
    /**
     * baseline: use built-in Java grayscale embedding (no model file required)
     * djl: reserved for future DJL model-based embedding
     */
    private String mode = "baseline";
    private String modelPath;
    private int dim = 512;
}

