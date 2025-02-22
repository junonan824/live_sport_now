package com.example.livesportsnow.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class MatchStats {
    private TeamStats homeTeamStats = new TeamStats();
    private TeamStats awayTeamStats = new TeamStats();
    private int ballPossessionHome;  // 볼 점유율
    private int ballPossessionAway;
    private int shotsHome;           // 슈팅
    private int shotsAway;
    private int shotsOnTargetHome;   // 유효슈팅
    private int shotsOnTargetAway;
    private int cornersHome;         // 코너킥
    private int cornersAway;
    private int foulsHome;           // 파울
    private int foulsAway;
    private int yellowCardsHome;     // 옐로카드
    private int yellowCardsAway;
    private int redCardsHome;        // 레드카드
    private int redCardsAway;
    private int passesHome;          // 패스
    private int passesAway;
    private int passAccuracyHome;    // 패스 정확도
    private int passAccuracyAway;
}



