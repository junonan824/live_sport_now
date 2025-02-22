package com.example.livesportsnow.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SimulatedMatch {
    private String matchId;
    private String homeTeam;
    private String awayTeam;
    private LocalDateTime startTime;
    private List<Player> homeTeamPlayers;
    private List<Player> awayTeamPlayers;
    private int currentMinute = 0;

    public SimulatedMatch(String matchId, String homeTeam, String awayTeam, 
                         LocalDateTime startTime, List<Player> homeTeamPlayers, 
                         List<Player> awayTeamPlayers) {
        this.matchId = matchId;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.startTime = startTime;
        this.homeTeamPlayers = homeTeamPlayers;
        this.awayTeamPlayers = awayTeamPlayers;
    }

    public int getCurrentMinute() {
        return currentMinute;
    }

    public void incrementMinute() {
        currentMinute++;
    }
} 