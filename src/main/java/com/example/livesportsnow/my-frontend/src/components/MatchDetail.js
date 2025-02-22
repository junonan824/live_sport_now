import React from 'react';
import './MatchDetail.css';

function MatchDetail({ match }) {
    return (
        <div className="match-detail">
            <div className="match-header">
                <h2>{match.homeTeam} vs {match.awayTeam}</h2>
                <div className="match-time">Minute: {match.currentMinute}'</div>
            </div>
            
            <div className="score-board">
                <div className="team home">
                    <h3>{match.homeTeam}</h3>
                    <div className="score">{match.homeScore}</div>
                </div>
                <div className="separator">-</div>
                <div className="team away">
                    <h3>{match.awayTeam}</h3>
                    <div className="score">{match.awayScore}</div>
                </div>
            </div>

            <div className="match-stats">
                <div className="possession-bar">
                    <div className="home" style={{width: `${match.stats.ballPossessionHome}%`}}>
                        {match.stats.ballPossessionHome}%
                    </div>
                    <div className="away" style={{width: `${match.stats.ballPossessionAway}%`}}>
                        {match.stats.ballPossessionAway}%
                    </div>
                </div>
                
                <div className="stats-row">
                    <div className="home">{match.stats.shotsHome}</div>
                    <div className="label">슈팅</div>
                    <div className="away">{match.stats.shotsAway}</div>
                </div>
                
                <div className="stats-row">
                    <div className="home">{match.stats.shotsOnTargetHome}</div>
                    <div className="label">유효슈팅</div>
                    <div className="away">{match.stats.shotsOnTargetAway}</div>
                </div>
            </div>

            <div className="events-timeline">
                {match.events.map((event, index) => (
                    <div key={index} className={`event ${event.type.toLowerCase()}`}>
                        <span className="minute">{event.minute}'</span>
                        <div className={`event-content ${event.team === match.homeTeam ? 'home' : 'away'}`}>
                            <span className="type-icon">{getEventIcon(event.type)}</span>
                            <span className="player">{event.player}</span>
                            {event.assistPlayer && (
                                <span className="assist">assist: {event.assistPlayer}</span>
                            )}
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}

export default MatchDetail; 