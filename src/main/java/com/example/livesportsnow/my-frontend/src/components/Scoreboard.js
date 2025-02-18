import React, { useState, useEffect } from 'react';
import './Scoreboard.css';

function Scoreboard() {
    const [scores, setScores] = useState([]);
    const [error, setError] = useState(null);
    const [status, setStatus] = useState('connecting');

    useEffect(() => {
        console.log('Connecting to SSE...');
        const eventSource = new EventSource('http://localhost:8080/stream/scores', {
            withCredentials: true
        });
        
        console.log('Initial readyState:', eventSource.readyState);
        
        const checkConnection = setInterval(() => {
            console.log('Current readyState:', eventSource.readyState);
            if (eventSource.readyState === EventSource.OPEN) {
                console.log('Connection is open');
                clearInterval(checkConnection);
            }
        }, 1000);

        eventSource.onopen = () => {
            console.log('SSE Connection opened successfully');
            console.log('ReadyState after open:', eventSource.readyState);
            setStatus('connected');
            setError(null);
        };

        eventSource.addEventListener('connect', (event) => {
            console.log('Connect event received:', event);
        });

        eventSource.addEventListener('scores', (event) => {
            console.log('Score event received:', event);
            console.log('Event type:', event.type);
            console.log('Event data:', event.data);
            
            try {
                const data = JSON.parse(event.data);
                console.log('Parsed data:', data);
                if (Object.keys(data).length > 0) {
                    const entries = Object.entries(data);
                    console.log('Setting scores:', entries);
                    setScores(entries);
                } else {
                    console.log('Received empty scores data');
                }
            } catch (err) {
                console.error('Parse error:', err);
                setError('Failed to parse score data');
            }
        });

        eventSource.onerror = (error) => {
            console.error('SSE error:', error);
            if (eventSource.readyState === EventSource.CLOSED) {
                console.log('Connection was closed');
                setStatus('disconnected');
            } else {
                console.log('Connection error, attempting to reconnect...');
                setStatus('reconnecting');
            }
            setError('Connection lost. Retrying...');
        };

        return () => {
            clearInterval(checkConnection);
            console.log('Cleaning up SSE connection');
            eventSource.close();
        };
    }, []);

    return (
        <div className="scoreboard">
            <h2>Live Scoreboard</h2>
            {status !== 'connected' && (
                <div className="connection-status">{status}...</div>
            )}
            {error && <div className="error">{error}</div>}
            <table>
                <thead>
                    <tr>
                        <th>Team</th>
                        <th>Score</th>
                    </tr>
                </thead>
                <tbody>
                    {scores.map(([team, score]) => (
                        <tr key={team}>
                            <td>{team}</td>
                            <td>{score}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}

export default Scoreboard; 