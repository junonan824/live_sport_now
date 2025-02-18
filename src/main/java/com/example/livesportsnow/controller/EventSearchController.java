package com.example.livesportsnow.controller;

import com.example.livesportsnow.service.EsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventSearchController {

    private final EsService esService;

    @GetMapping("/match/{matchId}/goals")
    public List<Map<String, Object>> searchGoalEvents(@PathVariable String matchId) throws IOException {
        log.info("Searching goal events for match: {}", matchId);
        List<Map<String, Object>> results = esService.searchGoalEvents(matchId);
        log.info("Found {} goal events for match {}", results.size(), matchId);
        return results;
    }
} 