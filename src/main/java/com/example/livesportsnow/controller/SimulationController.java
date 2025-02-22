package com.example.livesportsnow.controller;

import com.example.livesportsnow.simulation.SimulatedMatch;
import com.example.livesportsnow.simulation.MatchSimulator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/simulation")
@RequiredArgsConstructor
public class SimulationController {
    private final MatchSimulator matchSimulator;

    @PostMapping("/matches")
    public Map<String, String> startMatch(@RequestBody Map<String, String> request) {
        String homeTeam = request.get("homeTeam");
        String awayTeam = request.get("awayTeam");
        
        String matchId = matchSimulator.startNewMatch(homeTeam, awayTeam);
        
        return Map.of(
            "matchId", matchId,
            "message", String.format("Started simulation: %s vs %s", homeTeam, awayTeam)
        );
    }

    @GetMapping("/matches/{matchId}/status")
    public Map<String, Object> getMatchStatus(@PathVariable String matchId) {
        SimulatedMatch match = matchSimulator.getMatch(matchId);
        if (match == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Match not found");
        }
        
        return Map.of(
            "matchId", match.getMatchId(),
            "homeTeam", match.getHomeTeam(),
            "awayTeam", match.getAwayTeam(),
            "minute", match.getCurrentMinute(),
            "score", String.format("%d - %d", match.getHomeScore(), match.getAwayScore()),
            "status", match.getCurrentMinute() < 90 ? "IN_PROGRESS" : "FINISHED"
        );
    }
} 