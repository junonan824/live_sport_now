package com.example.livesportsnow.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public class ScoreUpdateEvent {
    private final Map<String, Double> scores;
} 