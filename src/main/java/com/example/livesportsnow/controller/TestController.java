package com.example.livesportsnow.controller;

import com.example.livesportsnow.kafka.producer.EventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final EventProducer eventProducer;

    @PostMapping("/events")
    public String testEvent(@RequestBody Map<String, Object> eventData) {
        String eventId = UUID.randomUUID().toString();
        log.info("Sending test event with ID: {}", eventId);
        eventProducer.sendEvent(eventId, eventData);
        return eventId;
    }
} 