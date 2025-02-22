package com.example.livesportsnow.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document(indexName = "matches")
public class Match {
    private String id;
    private String homeTeam;
    private String awayTeam;
    private int homeScore;
    private int awayScore;
    private String league;
    private LocalDateTime startTime;
    private MatchStatus status;
    private List<MatchEvent> events = new ArrayList<>();
    private MatchStats stats = new MatchStats();

    public void setHomeScore(int score) {
        this.homeScore = score;
    }

    public void setAwayScore(int score) {
        this.awayScore = score;
    }
} 