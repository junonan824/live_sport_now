package com.example.livesportsnow.kafka.consumer;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Collections;

import com.example.livesportsnow.service.RedisService;
import com.example.livesportsnow.service.EsService;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventConsumer {

    private final Gson gson;
    private final RedisService redisService;
    private final EsService esService;

    @KafkaListener(
            topics = "${spring.kafka.topic.match-events}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(@Payload String message) {
        try {
            log.info("Received message: {}", message);
            
            // JSON 문자열을 Map으로 변환
            Map<String, Object> eventData = gson.fromJson(message, Map.class);
            
            // 이벤트 타입에 따른 처리
            String eventType = (String) eventData.get("eventType");
            log.info("Processing event type: {}", eventType);
            
            // TODO: Redis 저장
            saveToRedis(eventData);
            
            // TODO: Elasticsearch 저장
            saveToElasticsearch(eventData);
            
        } catch (Exception e) {
            log.error("Error processing message: {}", message, e);
        }
    }
    
    // Redis 저장을 위한 메서드 시그니처
    private void saveToRedis(Map<String, Object> eventData) {
        try {
            String eventType = (String) eventData.get("eventType");
            if ("GOAL".equals(eventType)) {
                String teamId = (String) eventData.get("team");
                Double newScore = redisService.incrementScore("scoreboard", teamId);
                log.info("Updated score for team {}: {}", teamId, newScore);
            }
        } catch (Exception e) {
            log.error("Error saving to Redis: {}", eventData, e);
        }
    }
    
    // Elasticsearch 저장을 위한 메서드 시그니처
    private void saveToElasticsearch(Map<String, Object> eventData) {
        try {
            esService.bulkInsertEvents(Collections.singletonList(eventData));
        } catch (Exception e) {
            log.error("Error saving to Elasticsearch: {}", eventData, e);
        }
    }
} 