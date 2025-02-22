package com.example.livesportsnow.simulation;

import com.example.livesportsnow.model.*;
import com.example.livesportsnow.service.LiveMatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchSimulator {
    private final Map<String, SimulatedMatch> activeMatches = new ConcurrentHashMap<>();
    private final LiveMatchService liveMatchService;
    private final Random random = new Random();

    // 팀별 선수 데이터
    private final Map<String, List<Player>> teamPlayers = Map.of(
        "Tottenham", Arrays.asList(
            new Player("손흥민", "FW", 7, "Tottenham"),
            new Player("케인", "FW", 10, "Tottenham"),
            new Player("쿨루셰브스키", "MF", 21, "Tottenham")
        ),
        "Manchester City", Arrays.asList(
            new Player("홀란드", "FW", 9, "Manchester City"),
            new Player("데브라이너", "MF", 17, "Manchester City"),
            new Player("포든", "MF", 47, "Manchester City")
        )
    );

    // 팀의 선수 목록 조회
    private List<Player> getTeamPlayers(String team) {
        return teamPlayers.getOrDefault(team, Collections.emptyList());
    }

    // 특정 매치 조회
    public SimulatedMatch getMatch(String matchId) {
        return activeMatches.get(matchId);
    }

    // 새로운 매치 시작
    public String startNewMatch(String homeTeam, String awayTeam) {
        String matchId = UUID.randomUUID().toString();
        SimulatedMatch match = new SimulatedMatch(
            matchId,
            homeTeam,
            awayTeam,
            LocalDateTime.now(),
            getTeamPlayers(homeTeam),  // 실제 선수 데이터 사용
            getTeamPlayers(awayTeam)
        );
        activeMatches.put(matchId, match);
        log.info("Started new simulated match: {} vs {}", homeTeam, awayTeam);
        return matchId;
    }

    // 30초마다 이벤트 생성
    @Scheduled(fixedRate = 30000)
    public void simulateMatchEvents() {
        activeMatches.values().forEach(match -> {
            if (match.getCurrentMinute() < 90) {
                generateRandomEvent(match);
                match.incrementMinute();
                log.info("Match {} minute {}: {} vs {} ({}:{})",
                    match.getMatchId(),
                    match.getCurrentMinute(),
                    match.getHomeTeam(),
                    match.getAwayTeam(),
                    match.getHomeScore(),
                    match.getAwayScore()
                );
            } else {
                finishMatch(match.getMatchId());
            }
        });
    }

    private void generateRandomEvent(SimulatedMatch match) {
        if (random.nextDouble() < 0.3) { // 30% 확률로 이벤트 발생
            EventType eventType = getRandomEventType();
            String team = random.nextBoolean() ? match.getHomeTeam() : match.getAwayTeam();
            boolean isHomeTeam = team.equals(match.getHomeTeam());
            
            // 선수 선택
            List<Player> teamPlayers = isHomeTeam ? match.getHomePlayers() : match.getAwayPlayers();
            Player selectedPlayer = teamPlayers.get(random.nextInt(teamPlayers.size()));
            Player assistPlayer = null;

            // 통계 업데이트
            MatchStats stats = match.getStats();
            switch(eventType) {
                case SHOT:
                    if (isHomeTeam) {
                        stats.setShotsHome(stats.getShotsHome() + 1);
                        if (random.nextDouble() < 0.4) {
                            stats.setShotsOnTargetHome(stats.getShotsOnTargetHome() + 1);
                        }
                    } else {
                        stats.setShotsAway(stats.getShotsAway() + 1);
                        if (random.nextDouble() < 0.4) {
                            stats.setShotsOnTargetAway(stats.getShotsOnTargetAway() + 1);
                        }
                    }
                    break;
                case GOAL:
                    if (isHomeTeam) {
                        match.incrementHomeScore();
                        stats.setShotsHome(stats.getShotsHome() + 1);
                        stats.setShotsOnTargetHome(stats.getShotsOnTargetHome() + 1);
                        if (random.nextDouble() < 0.7) { // 70% 확률로 어시스트
                            assistPlayer = teamPlayers.get(random.nextInt(teamPlayers.size()));
                        }
                    } else {
                        match.incrementAwayScore();
                        stats.setShotsAway(stats.getShotsAway() + 1);
                        stats.setShotsOnTargetAway(stats.getShotsOnTargetAway() + 1);
                        if (random.nextDouble() < 0.7) {
                            assistPlayer = teamPlayers.get(random.nextInt(teamPlayers.size()));
                        }
                    }
                    break;
                case CORNER:
                    if (isHomeTeam) {
                        stats.setCornersHome(stats.getCornersHome() + 1);
                    } else {
                        stats.setCornersAway(stats.getCornersAway() + 1);
                    }
                    break;
            }

            // 볼 점유율 업데이트
            updatePossession(stats);

            // 이벤트 생성 및 전송
            MatchEvent event = MatchEvent.builder()
                .matchId(match.getMatchId())
                .type(eventType)
                .minute(match.getCurrentMinute())
                .team(team)
                .player(selectedPlayer.getName())
                .assistPlayer(assistPlayer != null ? assistPlayer.getName() : null)
                .homeScore(match.getHomeScore())
                .awayScore(match.getAwayScore())
                .build();

            // 이벤트를 LiveMatchService를 통해 처리
            liveMatchService.handleMatchEvent(event);
            log.info("Generated event: {} at minute {} - {} ({})", 
                eventType, match.getCurrentMinute(), selectedPlayer.getName(),
                team.equals(match.getHomeTeam()) ? "Home" : "Away");
        }
    }

    private void updatePossession(MatchStats stats) {
        // 현재 점유율에서 -5%~+5% 변동
        int variation = random.nextInt(11) - 5;
        int homePossession = stats.getBallPossessionHome() + variation;
        homePossession = Math.max(30, Math.min(70, homePossession)); // 30~70% 사이로 제한
        stats.setBallPossessionHome(homePossession);
        stats.setBallPossessionAway(100 - homePossession);
    }

    private EventType getRandomEventType() {
        double rand = random.nextDouble();
        if (rand < 0.1) return EventType.GOAL;
        if (rand < 0.3) return EventType.SHOT;
        if (rand < 0.5) return EventType.FOUL;
        if (rand < 0.6) return EventType.YELLOW_CARD;
        if (rand < 0.65) return EventType.RED_CARD;
        return EventType.CORNER;
    }

    private void finishMatch(String matchId) {
        activeMatches.remove(matchId);
        log.info("Match finished: {}", matchId);
    }

    private Player getRandomPlayer(SimulatedMatch match, EventType type) {
        List<Player> players = type == EventType.GOAL 
            ? match.getHomePlayers() 
            : match.getAwayPlayers();
        return players.get(random.nextInt(players.size()));
    }
} 