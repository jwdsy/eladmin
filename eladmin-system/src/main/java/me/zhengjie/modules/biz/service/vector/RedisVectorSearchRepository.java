package me.zhengjie.modules.biz.service.vector;

import me.zhengjie.modules.biz.config.ImageSearchVectorProperties;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Repository
public class RedisVectorSearchRepository {

    private final RedisTemplate<Object, Object> redisTemplate;
    private final ImageSearchVectorProperties properties;

    public RedisVectorSearchRepository(RedisTemplate<Object, Object> redisTemplate,
                                       ImageSearchVectorProperties properties) {
        this.redisTemplate = redisTemplate;
        this.properties = properties;
    }

    public boolean isEnabled() {
        return properties.isEnabled();
    }

    public void ensureIndex() {
        if (!isEnabled()) {
            return;
        }
        redisTemplate.execute((RedisCallback<Void>) connection -> {
            // Try "FT.INFO" first; if fails, create index.
            try {
                connection.execute("FT.INFO", properties.getIndex().getBytes(StandardCharsets.UTF_8));
                return null;
            } catch (Exception ignored) {
                // fall-through create
            }
            connection.execute("FT.CREATE",
                    properties.getIndex().getBytes(StandardCharsets.UTF_8),
                    "ON".getBytes(StandardCharsets.UTF_8), "HASH".getBytes(StandardCharsets.UTF_8),
                    "PREFIX".getBytes(StandardCharsets.UTF_8), "1".getBytes(StandardCharsets.UTF_8),
                    properties.getKeyPrefix().getBytes(StandardCharsets.UTF_8),
                    "SCHEMA".getBytes(StandardCharsets.UTF_8),
                    "imageId".getBytes(StandardCharsets.UTF_8), "NUMERIC".getBytes(StandardCharsets.UTF_8), "SORTABLE".getBytes(StandardCharsets.UTF_8),
                    "itemNo".getBytes(StandardCharsets.UTF_8), "TEXT".getBytes(StandardCharsets.UTF_8),
                    properties.getVectorField().getBytes(StandardCharsets.UTF_8), "VECTOR".getBytes(StandardCharsets.UTF_8),
                    properties.getAlgorithm().getBytes(StandardCharsets.UTF_8),
                    "10".getBytes(StandardCharsets.UTF_8),
                    "TYPE".getBytes(StandardCharsets.UTF_8), "FLOAT32".getBytes(StandardCharsets.UTF_8),
                    "DIM".getBytes(StandardCharsets.UTF_8), String.valueOf(properties.getDim()).getBytes(StandardCharsets.UTF_8),
                    "DISTANCE_METRIC".getBytes(StandardCharsets.UTF_8), properties.getDistanceMetric().getBytes(StandardCharsets.UTF_8),
                    "M".getBytes(StandardCharsets.UTF_8), String.valueOf(properties.getHnswM()).getBytes(StandardCharsets.UTF_8),
                    "EF_CONSTRUCTION".getBytes(StandardCharsets.UTF_8), String.valueOf(properties.getHnswEfConstruction()).getBytes(StandardCharsets.UTF_8)
            );
            return null;
        });
    }

    public void upsert(long imageId, String itemNo, float[] vector) {
        if (!isEnabled()) {
            throw new IllegalStateException("Vector search is disabled.");
        }
        if (vector == null || vector.length != properties.getDim()) {
            throw new IllegalArgumentException("vector dim mismatch, expected " + properties.getDim());
        }
        String key = properties.getKeyPrefix() + imageId;
        byte[] vecBytes = floatArrayToLittleEndianBytes(vector);
        redisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.execute("HSET",
                    key.getBytes(StandardCharsets.UTF_8),
                    "imageId".getBytes(StandardCharsets.UTF_8), String.valueOf(imageId).getBytes(StandardCharsets.UTF_8),
                    "itemNo".getBytes(StandardCharsets.UTF_8), (itemNo == null ? "" : itemNo).getBytes(StandardCharsets.UTF_8),
                    properties.getVectorField().getBytes(StandardCharsets.UTF_8), vecBytes
            );
            return null;
        });
    }

    public List<SearchHit> knn(float[] queryVector, int topK) {
        if (!isEnabled()) {
            throw new IllegalStateException("Vector search is disabled.");
        }
        if (queryVector == null || queryVector.length != properties.getDim()) {
            throw new IllegalArgumentException("queryVector dim mismatch, expected " + properties.getDim());
        }
        int k = Math.max(1, Math.min(topK, 100));
        byte[] vecBytes = floatArrayToLittleEndianBytes(queryVector);

        Object raw = redisTemplate.execute((RedisCallback<Object>) connection -> ftSearchKnn(connection, vecBytes, k));
        return parseFtSearchResult(raw);
    }

    private Object ftSearchKnn(RedisConnection connection, byte[] vecBytes, int k) throws DataAccessException {
        // FT.SEARCH idx "*=>[KNN k @vec $BLOB AS score]" PARAMS 2 BLOB <bytes> SORTBY score DIALECT 2 RETURN 2 imageId score
        return connection.execute("FT.SEARCH",
                properties.getIndex().getBytes(StandardCharsets.UTF_8),
                ("*=>[KNN " + k + " @" + properties.getVectorField() + " $BLOB AS score]").getBytes(StandardCharsets.UTF_8),
                "PARAMS".getBytes(StandardCharsets.UTF_8), "2".getBytes(StandardCharsets.UTF_8),
                "BLOB".getBytes(StandardCharsets.UTF_8), vecBytes,
                "SORTBY".getBytes(StandardCharsets.UTF_8), "score".getBytes(StandardCharsets.UTF_8),
                "DIALECT".getBytes(StandardCharsets.UTF_8), "2".getBytes(StandardCharsets.UTF_8),
                "RETURN".getBytes(StandardCharsets.UTF_8), "2".getBytes(StandardCharsets.UTF_8),
                "imageId".getBytes(StandardCharsets.UTF_8),
                "score".getBytes(StandardCharsets.UTF_8)
        );
    }

    private List<SearchHit> parseFtSearchResult(Object raw) {
        if (!(raw instanceof List)) {
            return Collections.emptyList();
        }
        List<?> top = (List<?>) raw;
        if (top.size() < 2) {
            return Collections.emptyList();
        }
        List<SearchHit> hits = new ArrayList<>();
        // format: [total, docKey1, [field, value, field, value], docKey2, [..], ...]
        for (int i = 1; i < top.size(); i += 2) {
            Object fieldsObj = top.get(i + 1);
            Map<String, String> fields = listToStringMap(fieldsObj);
            String imageIdStr = fields.get("imageId");
            String scoreStr = fields.get("score");
            if (imageIdStr == null) {
                continue;
            }
            SearchHit hit = new SearchHit();
            hit.setImageId(Long.parseLong(imageIdStr));
            hit.setDistance(scoreStr == null ? null : Double.parseDouble(scoreStr));
            hits.add(hit);
        }
        return hits;
    }

    private Map<String, String> listToStringMap(Object fieldsObj) {
        if (!(fieldsObj instanceof List)) {
            return Collections.emptyMap();
        }
        List<?> list = (List<?>) fieldsObj;
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i + 1 < list.size(); i += 2) {
            String k = bytesOrStringToString(list.get(i));
            String v = bytesOrStringToString(list.get(i + 1));
            if (k != null) {
                map.put(k, v);
            }
        }
        return map;
    }

    private String bytesOrStringToString(Object obj) {
        if (obj == null) return null;
        if (obj instanceof byte[]) {
            return new String((byte[]) obj, StandardCharsets.UTF_8);
        }
        return String.valueOf(obj);
    }

    private byte[] floatArrayToLittleEndianBytes(float[] vector) {
        ByteBuffer buf = ByteBuffer.allocate(vector.length * 4).order(ByteOrder.LITTLE_ENDIAN);
        for (float v : vector) {
            buf.putFloat(v);
        }
        return buf.array();
    }

    public static class SearchHit {
        private long imageId;
        /**
         * Redis 返回的是“距离/相似度”字段名 score，这里先按 distance 存（COSINE 下越小越相似）
         */
        private Double distance;

        public long getImageId() {
            return imageId;
        }

        public void setImageId(long imageId) {
            this.imageId = imageId;
        }

        public Double getDistance() {
            return distance;
        }

        public void setDistance(Double distance) {
            this.distance = distance;
        }
    }
}

