package com.example.livesportsnow.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchEvent {
    private String id;
    private String matchId;
    private EventType type;
    private String team;
    private String player;
    private String assistPlayer;
    private int minute;
    private String description;
    private int homeScore;
    private int awayScore;
} 