package com.example.livesportsnow.controller;

import com.example.livesportsnow.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/scoreboard")
@RequiredArgsConstructor
public class ScoreboardController {

    private final RedisService redisService;

    @PostMapping("/teams/{teamId}/increment")
    public Double incrementTeamScore(@PathVariable String teamId) {
        return redisService.incrementScore("scoreboard", teamId);
    }

    @GetMapping("/top/{n}")
    public Map<String, Double> getTopScores(@PathVariable int n) {
        return redisService.getTopNScores("scoreboard", n);
    }
} 