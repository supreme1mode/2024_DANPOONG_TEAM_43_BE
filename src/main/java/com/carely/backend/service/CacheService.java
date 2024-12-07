package com.carely.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CacheService {
    private final RedisTemplate<String, Object> redisTemplate;

    // 데이터 저장 (TTL: 1시간)
    public <T> void save(String key, T value) {
        redisTemplate.opsForValue().set(key, value, 1, TimeUnit.HOURS);
    }

    // 데이터 조회
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) redisTemplate.opsForValue().get(key);
    }

    // 캐시 삭제
    public void evict(String key) {
        redisTemplate.delete(key);
    }
}