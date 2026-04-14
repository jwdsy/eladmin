package me.zhengjie.modules.biz.config;

import lombok.Data;

@Data
public class ImageSearchVectorProperties {
    private boolean enabled = false;
    private String index = "idx:img:vector";
    private String keyPrefix = "img:";
    private String vectorField = "vec";
    private int dim = 512;
    private String distanceMetric = "COSINE";
    private String algorithm = "HNSW";
    private int hnswM = 16;
    private int hnswEfConstruction = 200;
    private int hnswEfRuntime = 100;
}

