package me.zhengjie.modules.biz.service.vector;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisVectorIndexInitializer implements ApplicationRunner {

    private final RedisVectorSearchRepository redisVectorSearchRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (!redisVectorSearchRepository.isEnabled()) {
            log.info("Redis vector search disabled, skip index init.");
            return;
        }
        try {
            redisVectorSearchRepository.ensureIndex();
            log.info("Redis vector index ensured.");
        } catch (Exception e) {
            log.error("Failed to ensure Redis vector index.", e);
        }
    }
}

