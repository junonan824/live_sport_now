import React, { useState, useEffect } from 'react';
import MatchDetail from './MatchDetail';

function LiveMatch({ matchId }) {
    const [matchData, setMatchData] = useState(null);
    const [matchStatus, setMatchStatus] = useState(null);
    const [error, setError] = useState(null);

    useEffect(() => {
        const eventSource = new EventSource(
            `http://localhost:8080/api/stream/matches/${matchId}`,
            { withCredentials: true }
        );

        eventSource.onmessage = (event) => {
            try {
                const data = JSON.parse(event.data);
                setMatchData(data);
                console.log("Received match update:", data);
            } catch (err) {
                console.error("Error parsing event data:", err);
            }
        };

        eventSource.onerror = (error) => {
            console.error("SSE Error:", error);
            setError("Connection lost. Trying to reconnect...");
        };

        return () => {
            eventSource.close();
        };
    }, [matchId]);

    useEffect(() => {
        const checkStatus = async () => {
            try {
                const response = await fetch(`http://localhost:8080/api/simulation/matches/${matchId}/status`);
                const status = await response.json();
                setMatchStatus(status);
            } catch (err) {
                console.error("Error checking match status:", err);
            }
        };

        const interval = setInterval(checkStatus, 5000);
        checkStatus();

        return () => clearInterval(interval);
    }, [matchId]);

    if (error) return <div className="error-message">{error}</div>;
    if (!matchData) return <div className="loading">Loading match data...</div>;

    return (
        <div>
            {matchStatus && (
                <div className="match-status">
                    <h2>{matchStatus.homeTeam} vs {matchStatus.awayTeam}</h2>
                    <div className="minute">Minute: {matchStatus.minute}'</div>
                    <div className="score">Score: {matchStatus.score}</div>
                    <div className="status">Status: {matchStatus.status}</div>
                </div>
            )}
            {matchData && <MatchDetail match={matchData} />}
        </div>
    );
}

export default LiveMatch; 