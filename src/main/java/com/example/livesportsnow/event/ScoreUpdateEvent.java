package com.example.livesportsnow.event;

import lombok.Getter;
import lombok.AllArgsConstructor;
import java.util.Map;

@Getter
@AllArgsConstructor
public class ScoreUpdateEvent {
    private final Map<String, Double> scores;
} 