package com.euni.articlehub.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CacheStatsService {

    private final StringRedisTemplate redis;

    private static final String HITS = "stats:cache:hits";
    private static final String MISSES = "stats:cache:misses";

    public void incHit() {
        redis.opsForValue().increment(HITS);
    }

    public void incMiss() {
        redis.opsForValue().increment(MISSES);
    }

    public Map<String, String> getStats() {
        String hits = redis.opsForValue().get(HITS);
        String misses = redis.opsForValue().get(MISSES);
        return Map.of("hits", hits != null ? hits : "0",
                "misses", misses != null ? misses : "0");
    }
}
