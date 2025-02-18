package com.example.livesportsnow.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    /**
     * 특정 키의 멤버 스코어를 증가시킵니다.
     *
     * @param key 스코어보드 키 (예: "scoreboard")
     * @param member 팀 ID
     * @return 증가 후의 스코어
     */
    public Double incrementScore(String key, String member) {
        try {
            Double newScore = redisTemplate.opsForZSet().incrementScore(key, member, 1.0);
            log.info("Score incremented - key: {}, member: {}, new score: {}", key, member, newScore);
            return newScore;
        } catch (Exception e) {
            log.error("Failed to increment score - key: {}, member: {}", key, member, e);
            throw new RuntimeException("Redis operation failed", e);
        }
    }

    /**
     * 특정 키의 상위 N개 스코어를 조회합니다.
     *
     * @param key 스코어보드 키 (예: "scoreboard")
     * @param n 조회할 상위 개수
     * @return 멤버와 스코어를 포함한 Map (정렬된 상태)
     */
    public Map<String, Double> getTopNScores(String key, int n) {
        try {
            Set<ZSetOperations.TypedTuple<String>> rangeWithScores = 
                redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, n - 1);

            Map<String, Double> result = new LinkedHashMap<>();
            if (rangeWithScores != null) {
                rangeWithScores.forEach(tuple -> {
                    if (tuple.getValue() != null && tuple.getScore() != null) {
                        result.put(tuple.getValue(), tuple.getScore());
                    }
                });
            }

            log.info("Retrieved top {} scores for key: {}", n, key);
            return result;
        } catch (Exception e) {
            log.error("Failed to get top scores - key: {}, n: {}", key, n, e);
            throw new RuntimeException("Redis operation failed", e);
        }
    }
} 