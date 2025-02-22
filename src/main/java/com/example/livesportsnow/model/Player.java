package com.example.livesportsnow.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    private String name;
    private String position;
    private int number;
    private String team;

    public void setTeam(String team) {
        this.team = team;
    }

    public String getTeam() {
        return team;
    }
} 