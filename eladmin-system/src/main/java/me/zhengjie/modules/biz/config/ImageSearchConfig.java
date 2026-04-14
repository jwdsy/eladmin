package me.zhengjie.modules.biz.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImageSearchConfig {

    @Bean
    @ConfigurationProperties(prefix = "image-search.vector")
    public ImageSearchVectorProperties imageSearchVectorProperties() {
        return new ImageSearchVectorProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "image-search.embedding")
    public ImageSearchEmbeddingProperties imageSearchEmbeddingProperties() {
        return new ImageSearchEmbeddingProperties();
    }
}

