package com.example.livesportsnow.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.example.livesportsnow.service.LiveMatchService;
import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MatchStreamController {
    private final LiveMatchService liveMatchService;

    @GetMapping(path = "/api/stream/matches/{matchId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamMatch(@PathVariable String matchId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        liveMatchService.addEmitter(matchId, emitter);
        
        try {
            // 초기 연결 확인 이벤트 전송
            emitter.send(SseEmitter.event()
                .name("connect")
                .data("Connected to match stream")
                .id("0"));
        } catch (IOException e) {
            log.error("Error sending initial event", e);
        }
        
        return emitter;
    }
} 