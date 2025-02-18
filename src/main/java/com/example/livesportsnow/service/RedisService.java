package com.example.livesportsnow.service;

import com.example.livesportsnow.event.ScoreUpdateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.context.ApplicationEventPublisher;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;
    private final ApplicationEventPublisher eventPublisher;

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
            
            // 이벤트 발행 방식으로 변경
            Map<String, Double> topScores = getTopNScores(key, 10);
            eventPublisher.publishEvent(new ScoreUpdateEvent(topScores));
            
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
            log.info("Fetching top {} scores for key: {}", n, key);
            Set<ZSetOperations.TypedTuple<String>> tuples = 
                redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, n - 1);
            
            if (tuples == null || tuples.isEmpty()) {
                log.info("No scores found for key: {}", key);
                return new LinkedHashMap<>();
            }

            Map<String, Double> scores = new LinkedHashMap<>();
            tuples.forEach(tuple -> {
                scores.put(tuple.getValue(), tuple.getScore());
            });
            
            log.info("Found {} scores: {}", scores.size(), scores);
            return scores;
        } catch (Exception e) {
            log.error("Failed to get scores - key: {}, n: {}", key, n, e);
            throw new RuntimeException("Redis operation failed", e);
        }
    }
} 