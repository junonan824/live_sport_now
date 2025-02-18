package com.example.livesportsnow.controller;

import com.example.livesportsnow.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.context.event.EventListener;
import com.example.livesportsnow.event.ScoreUpdateEvent;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ScoreStreamController {

    private final RedisService redisService;
    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @EventListener
    public void handleScoreUpdate(ScoreUpdateEvent event) {
        sendScoreUpdate(event.getScores());
    }

    @GetMapping(path = "/stream/scores", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamScores(HttpServletResponse response) {
        log.info("New SSE connection requested");
        
        // CORS 헤더 직접 설정
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");
        
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        
        try {
            // CORS 헤더 추가
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("connected")
                    .id("0"));
                
            log.info("Sending initial scores");
            Map<String, Double> scores = redisService.getTopNScores("scoreboard", 10);
            log.info("Initial scores: {}", scores);
            
            emitter.send(SseEmitter.event()
                    .name("scores")
                    .data(scores)
                    .id("init")
                    .reconnectTime(3000));
                
            log.info("Initial scores sent successfully");
            emitters.add(emitter);
            log.info("New emitter added. Total emitters: {}", emitters.size());
        } catch (IOException e) {
            log.error("Failed to send initial scores", e);
            emitter.completeWithError(e);
            return emitter;
        }
        
        emitter.onCompletion(() -> {
            emitters.remove(emitter);
            log.info("SSE connection completed. Remaining emitters: {}", emitters.size());
        });
        emitter.onTimeout(() -> {
            emitters.remove(emitter);
            log.info("SSE connection timeout. Remaining emitters: {}", emitters.size());
        });
        emitter.onError(e -> {
            emitters.remove(emitter);
            log.error("SSE connection error", e);
            log.info("Remaining emitters: {}", emitters.size());
        });
        
        return emitter;
    }

    public void sendScoreUpdate(Map<String, Double> scores) {
        List<SseEmitter> deadEmitters = new ArrayList<>();
        
        emitters.forEach(emitter -> {
            try {
                log.info("Sending score update to emitter: {}", scores);
                emitter.send(SseEmitter.event()
                        .name("scores")
                        .data(scores)
                        .id(String.valueOf(System.currentTimeMillis())));
            } catch (IOException e) {
                log.error("Failed to send score update", e);
                deadEmitters.add(emitter);
            }
        });
        
        if (!deadEmitters.isEmpty()) {
            log.info("Removing {} dead emitters", deadEmitters.size());
            emitters.removeAll(deadEmitters);
        }
    }
} 