package com.euni.articlehub.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Map;

/*
* 스프링의 @Cacheable 캐시 기능을 켜고, Redis에 JSON 형태로 저장하며,
* 캐시 만료시간(TTL) 을 캐시 이름별로 다르게 지정해 주는 중앙 설정 파일
*/
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public RedisSerializer<Object> redisValueSerializer(ObjectMapper objectMapper) {
        ObjectMapper om = objectMapper.copy();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        return new GenericJackson2JsonRedisSerializer(om);
    }

    @Bean
    public RedisCacheConfiguration defaultCacheConfig(RedisSerializer<Object> valueSerializer) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer))
                .disableCachingNullValues();
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory,
                                     RedisCacheConfiguration defaultConfig) {
        Map<String, RedisCacheConfiguration> perCache = Map.of(
                "search:es", defaultConfig.entryTtl(Duration.ofMinutes(1)),
                "search:mysql", defaultConfig.entryTtl(Duration.ofMinutes(2))
        );
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(perCache)
                .build();
    }
}
