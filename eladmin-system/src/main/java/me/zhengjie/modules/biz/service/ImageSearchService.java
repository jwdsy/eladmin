package me.zhengjie.modules.biz.service;

import me.zhengjie.modules.biz.service.dto.ImageSearchResultDto;
import me.zhengjie.modules.biz.service.dto.VectorBackfillResultDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface ImageSearchService {

    /**
     * 以图搜图，返回相似图片及商品属性信息
     */
    List<ImageSearchResultDto> search(MultipartFile file, Integer topN) throws IOException;

    /**
     * 写入单张图片向量
     */
    void upsertVector(Long imageId, MultipartFile file) throws IOException;

    /**
     * 批量回填向量
     */
    VectorBackfillResultDto backfillVectors(Long startId, Long endId, Integer batchSize);

    /**
     * 按指定ID列表重试回填
     */
    VectorBackfillResultDto retryVectors(Set<Long> imageIds);
}

