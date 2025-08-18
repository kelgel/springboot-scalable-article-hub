package com.euni.articlehub.batch;

import com.euni.articlehub.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisKeyCommands;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class ViewsFlushScheduler {

    private final StringRedisTemplate redis;
    private final PostRepository postRepository;

    // 매 60초마다 실행 (원하면 조절)
    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void flushDeltas() {
        String pattern = "post:views:delta:*";

        // SCAN으로 키 탐색 (운영에서 KEYS 사용 금지)
        List<String> keys = new ArrayList<>();

        redis.execute((RedisCallback<Object>) connection -> {
            try (Cursor<byte[]> cursor = ((RedisKeyCommands) connection)
                    .scan(ScanOptions.scanOptions().match(pattern).count(500).build())) {

                cursor.forEachRemaining(item -> keys.add(new String(item, StandardCharsets.UTF_8)));
            }
            return null;
        });


        if (keys.isEmpty()) return;

        for (String key : keys) {
            try {
                // postId 추출
                String idStr = key.substring("post:views:delta:".length());
                Long postId = Long.valueOf(idStr);

                // 증분값을 원자적으로 읽고 0으로 초기화
                String deltaStr = redis.opsForValue().getAndSet(key, "0");
                if (deltaStr == null) continue;

                int delta = Integer.parseInt(deltaStr);
                if (delta <= 0) continue;

                postRepository.incrementViews(postId, delta);
            } catch (Exception e) {
                log.warn("flush failed for key={} : {}", key, e.getMessage());
            }
        }
        log.debug("Flushed {} delta keys", keys.size());
    }
}
