package me.zhengjie.modules.biz.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class VectorBackfillResultDto implements Serializable {
    private Long startId;
    private Long endId;
    private Integer batchSize;
    private Integer totalScanned = 0;
    private Integer successCount = 0;
    private Integer skippedCount = 0;
    private Integer failedCount = 0;
    private List<String> failedSamples = new ArrayList<>();
}

