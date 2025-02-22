package com.example.livesportsnow.service;

import com.example.livesportsnow.model.*;  // 모든 모델 클래스 import
import static com.example.livesportsnow.model.EventType.*;  // 추가
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.ArrayList;

@Service
@Slf4j
@RequiredArgsConstructor
public class LiveMatchService {
    private final Map<String, List<SseEmitter>> matchEmitters = new ConcurrentHashMap<>();
    private final RedisTemplate<String, Match> redisTemplate;
    private final KafkaTemplate<String, MatchEvent> kafkaTemplate;
    private final ElasticsearchClient esClient;

    public void handleMatchEvent(MatchEvent event) {
        // Redis에 매치 상태 저장
        String matchKey = "match:" + event.getMatchId();
        Match match = redisTemplate.opsForValue().get(matchKey);
        if (match == null) {
            match = new Match();
            match.setId(event.getMatchId());
        }
        
        // 이벤트에 따른 매치 상태 업데이트
        updateMatchState(match, event);
        redisTemplate.opsForValue().set(matchKey, match);

        // SSE를 통해 클라이언트에게 이벤트 전송
        sendEventToClients(event.getMatchId(), event);

        kafkaTemplate.send("match-events", event);
        saveEventToEs(event);
    }

    private void updateMatchState(Match match, MatchEvent event) {
        match.getEvents().add(event);
        if (event.getType() == EventType.GOAL) {
            if (event.getTeam().equals(match.getHomeTeam())) {
                match.setHomeScore(event.getHomeScore());
            } else {
                match.setAwayScore(event.getAwayScore());
            }
        }
    }

    public void addEmitter(String matchId, SseEmitter emitter) {
        matchEmitters.computeIfAbsent(matchId, k -> new CopyOnWriteArrayList<>()).add(emitter);
        emitter.onCompletion(() -> removeEmitter(matchId, emitter));
        emitter.onTimeout(() -> removeEmitter(matchId, emitter));
    }

    private void sendEventToClients(String matchId, MatchEvent event) {
        List<SseEmitter> emitters = matchEmitters.get(matchId);
        if (emitters != null) {
            List<SseEmitter> deadEmitters = new ArrayList<>();
            emitters.forEach(emitter -> {
                try {
                    emitter.send(SseEmitter.event()
                        .name("matchUpdate")
                        .data(event)
                        .id(String.valueOf(System.currentTimeMillis())));
                } catch (IOException e) {
                    deadEmitters.add(emitter);
                }
            });
            emitters.removeAll(deadEmitters);
        }
    }

    private void saveEventToEs(MatchEvent event) {
        try {
            esClient.index(i -> i
                .index("match-events")
                .document(event)
            );
        } catch (Exception e) {
            log.error("Failed to save event to Elasticsearch", e);
        }
    }

    private void removeEmitter(String matchId, SseEmitter emitter) {
        List<SseEmitter> emitters = matchEmitters.get(matchId);
        if (emitters != null) {
            emitters.remove(emitter);
            if (emitters.isEmpty()) {
                matchEmitters.remove(matchId);
            }
        }
    }
} 