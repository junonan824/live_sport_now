package com.example.livesportsnow.simulation;

import com.example.livesportsnow.model.Player;
import com.example.livesportsnow.model.MatchStats;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class SimulatedMatch {
    private String matchId;
    private String homeTeam;
    private String awayTeam;
    private LocalDateTime startTime;
    private List<Player> homeTeamPlayers;
    private List<Player> awayTeamPlayers;
    private int currentMinute = 0;
    private int homeScore = 0;
    private int awayScore = 0;
    private MatchStats stats = new MatchStats();

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

    public void incrementHomeScore() {
        this.homeScore++;
    }

    public void incrementAwayScore() {
        this.awayScore++;
    }

    public void incrementMinute() {
        currentMinute++;
    }

    public int getHomeScore() {
        return homeScore;
    }

    public int getAwayScore() {
        return awayScore;
    }

    public String getMatchId() {
        return matchId;
    }

    public List<Player> getHomePlayers() {
        return homeTeamPlayers;
    }

    public List<Player> getAwayPlayers() {
        return awayTeamPlayers;
    }

    public MatchStats getStats() {
        return stats;
    }
} 