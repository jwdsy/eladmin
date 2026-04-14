package me.zhengjie.modules.biz.service.impl;

import lombok.RequiredArgsConstructor;
import me.zhengjie.modules.biz.repository.BizItemBaseRecordRepository;
import me.zhengjie.modules.biz.repository.domain.BizItemBaseRecord;
import me.zhengjie.modules.biz.service.ImageSearchService;
import me.zhengjie.modules.biz.service.dto.ImageSearchResultDto;
import me.zhengjie.modules.biz.service.dto.VectorBackfillResultDto;
import me.zhengjie.modules.biz.service.mapstruct.BizItemBaseRecordStructMapper;
import me.zhengjie.modules.biz.service.embedding.EmbeddingService;
import me.zhengjie.modules.biz.service.vector.RedisVectorSearchRepository;
import me.zhengjie.modules.biz.service.vector.RedisVectorSearchRepository.SearchHit;
import me.zhengjie.utils.OpenCVImageSearcher;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ImageSearchServiceImpl implements ImageSearchService {

    private static final int DEFAULT_TOP_N = 20;
    private static final int MAX_TOP_N = 100;
    private static final int ACTIVE_FLAG = 0;

    private final OpenCVImageSearcher openCVImageSearcher;
    private final BizItemBaseRecordRepository bizItemBaseRecordRepository;
    private final BizItemBaseRecordStructMapper bizItemBaseRecordStructMapper;
    private final RedisVectorSearchRepository redisVectorSearchRepository;
    private final EmbeddingService embeddingService;
    @Value("${file.mac.imagePath:}")
    private String macImagePath;
    @Value("${file.linux.imagePath:}")
    private String linuxImagePath;
    @Value("${file.windows.imagePath:}")
    private String windowsImagePath;

    @Override
    public List<ImageSearchResultDto> search(MultipartFile file, Integer topN) throws IOException {
        int realTopN = normalizeTopN(topN);
        if (redisVectorSearchRepository.isEnabled() && embeddingService.isEnabled()) {
            return searchByVector(file, realTopN);
        }
        return searchByOpenCV(file, realTopN);
    }

    @Override
    public void upsertVector(Long imageId, MultipartFile file) throws IOException {
        if (!redisVectorSearchRepository.isEnabled() || !embeddingService.isEnabled()) {
            throw new UnsupportedOperationException("Vector pipeline is disabled.");
        }
        BizItemBaseRecord record = bizItemBaseRecordRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("item not found, imageId=" + imageId));
        float[] vector = embeddingService.embed(file);
        redisVectorSearchRepository.upsert(imageId, record.getItemNo(), vector);
    }

    @Override
    public VectorBackfillResultDto backfillVectors(Long startId, Long endId, Integer batchSize) {
        checkVectorPipeline();
        VectorBackfillResultDto result = new VectorBackfillResultDto();
        int realBatchSize = normalizeBatchSize(batchSize);
        long realStartId = startId == null || startId <= 0 ? 1L : startId;
        long realEndId = getEndId(endId);
        result.setStartId(realStartId);
        result.setEndId(realEndId);
        result.setBatchSize(realBatchSize);

        if (realEndId < realStartId) {
            return result;
        }
        int pageNo = 0;
        while (true) {
            Page<BizItemBaseRecord> page = bizItemBaseRecordRepository
                    .findByIdGreaterThanEqualAndIdLessThanEqualOrderByIdAsc(realStartId, realEndId, PageRequest.of(pageNo, realBatchSize));
            if (!page.hasContent()) {
                break;
            }
            for (BizItemBaseRecord record : page.getContent()) {
                processRecord(record, result);
            }
            if (!page.hasNext()) {
                break;
            }
            pageNo++;
        }
        return result;
    }

    @Override
    public VectorBackfillResultDto retryVectors(Set<Long> imageIds) {
        checkVectorPipeline();
        VectorBackfillResultDto result = new VectorBackfillResultDto();
        result.setBatchSize(imageIds == null ? 0 : imageIds.size());
        if (imageIds == null || imageIds.isEmpty()) {
            return result;
        }
        long minId = Long.MAX_VALUE;
        long maxId = Long.MIN_VALUE;
        for (Long id : imageIds) {
            if (id == null || id <= 0) {
                continue;
            }
            minId = Math.min(minId, id);
            maxId = Math.max(maxId, id);
            BizItemBaseRecord record = bizItemBaseRecordRepository.findById(id).orElse(null);
            if (record == null) {
                result.setTotalScanned(result.getTotalScanned() + 1);
                result.setSkippedCount(result.getSkippedCount() + 1);
                appendFailSample(result, "skip id=" + id + " item not found");
                continue;
            }
            processRecord(record, result);
        }
        if (minId != Long.MAX_VALUE) {
            result.setStartId(minId);
            result.setEndId(maxId);
        }
        return result;
    }

    private List<ImageSearchResultDto> searchByVector(MultipartFile file, int topN) throws IOException {
        float[] query = embeddingService.embed(file);
        List<SearchHit> hits = redisVectorSearchRepository.knn(query, topN);
        if (hits.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Long, Double> idToDistance = new HashMap<>();
        List<Long> ids = new ArrayList<>(hits.size());
        for (SearchHit hit : hits) {
            ids.add(hit.getImageId());
            idToDistance.put(hit.getImageId(), hit.getDistance());
        }

        Map<Long, BizItemBaseRecord> recordMap = new HashMap<>();
        bizItemBaseRecordRepository.findAllById(ids).forEach(r -> recordMap.put(r.getId(), r));

        List<ImageSearchResultDto> response = new ArrayList<>(hits.size());
        for (Long id : ids) {
            ImageSearchResultDto dto = new ImageSearchResultDto();
            Double dist = idToDistance.get(id);
            if (dist != null) {
                dto.setSimilarity(1d - dist); // COSINE distance => similarity approx (1 - dist)
            }
            BizItemBaseRecord record = recordMap.get(id);
            if (record != null) {
                dto.setItem(bizItemBaseRecordStructMapper.toDto(record));
                dto.setSourceImagePath(record.getItemPic());
                dto.setSourceImageName(record.getItemNo());
            }
            response.add(dto);
        }
        return response;
    }

    private List<ImageSearchResultDto> searchByOpenCV(MultipartFile file, int topN) throws IOException {
        List<OpenCVImageSearcher.SearchResult> searchResults = openCVImageSearcher.searchSimilarImages(topN, file.getBytes());
        List<ImageSearchResultDto> response = new ArrayList<>(searchResults.size());
        for (OpenCVImageSearcher.SearchResult searchResult : searchResults) {
            ImageSearchResultDto item = new ImageSearchResultDto();
            item.setSimilarity(searchResult.similarity);
            item.setSourceImagePath(searchResult.filePath);
            item.setSourceImageName(searchResult.fileName);

            BizItemBaseRecord record = bizItemBaseRecordRepository
                    .findTop1ByItemPicContainingAndDelFlagOrderByIdDesc(searchResult.fileName, ACTIVE_FLAG);
            if (record == null && StringUtils.isNotBlank(searchResult.filePath)) {
                record = bizItemBaseRecordRepository.findTop1ByItemPicContainingOrderByIdDesc(searchResult.filePath);
            }
            if (record != null) {
                item.setItem(bizItemBaseRecordStructMapper.toDto(record));
            }
            response.add(item);
        }
        return response;
    }

    private int normalizeTopN(Integer topN) {
        if (topN == null || topN <= 0) {
            return DEFAULT_TOP_N;
        }
        return Math.min(topN, MAX_TOP_N);
    }

    private int normalizeBatchSize(Integer batchSize) {
        if (batchSize == null || batchSize <= 0) {
            return 100;
        }
        return Math.min(batchSize, 500);
    }

    private long getEndId(Long endId) {
        if (endId != null && endId > 0) {
            return endId;
        }
        BizItemBaseRecord max = bizItemBaseRecordRepository.findTop1ByOrderByIdDesc();
        return max == null ? 0L : max.getId();
    }

    private void processRecord(BizItemBaseRecord record, VectorBackfillResultDto result) {
        result.setTotalScanned(result.getTotalScanned() + 1);
        try {
            Path path = resolveImagePath(record.getItemPic());
            if (path == null || !Files.exists(path)) {
                result.setSkippedCount(result.getSkippedCount() + 1);
                appendFailSample(result, "skip id=" + record.getId() + " path not found: " + record.getItemPic());
                return;
            }
            byte[] bytes = Files.readAllBytes(path);
            float[] vector = embeddingService.embed(bytes);
            redisVectorSearchRepository.upsert(record.getId(), record.getItemNo(), vector);
            result.setSuccessCount(result.getSuccessCount() + 1);
        } catch (Exception e) {
            result.setFailedCount(result.getFailedCount() + 1);
            appendFailSample(result, "fail id=" + record.getId() + " reason=" + e.getMessage());
        }
    }

    private void appendFailSample(VectorBackfillResultDto result, String message) {
        if (result.getFailedSamples().size() < 20) {
            result.getFailedSamples().add(message);
        }
    }

    private Path resolveImagePath(String itemPic) {
        if (StringUtils.isBlank(itemPic)) {
            return null;
        }
        String pathStr = itemPic.trim();
        if (pathStr.startsWith("http://") || pathStr.startsWith("https://")) {
            return null;
        }
        Path directPath = Paths.get(pathStr);
        if (directPath.isAbsolute()) {
            return directPath;
        }
        String imageRoot = OpenCVImageSearcher.getFilePath(macImagePath, linuxImagePath, windowsImagePath);
        if (StringUtils.isBlank(imageRoot)) {
            return directPath;
        }
        String normalized = pathStr.startsWith(File.separator) ? pathStr.substring(1) : pathStr;
        return Paths.get(imageRoot, normalized);
    }

    private void checkVectorPipeline() {
        if (!redisVectorSearchRepository.isEnabled() || !embeddingService.isEnabled()) {
            throw new UnsupportedOperationException("Vector pipeline is disabled.");
        }
    }
}

