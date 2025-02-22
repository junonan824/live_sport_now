package com.example.livesportsnow.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class TeamStats {
    private int possession;
    private int shots;
    private int shotsOnTarget;
    private int corners;
    private int fouls;
    private int yellowCards;
    private int redCards;
} 