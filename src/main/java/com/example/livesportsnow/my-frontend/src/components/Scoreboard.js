import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './Scoreboard.css';

function Scoreboard() {
    const [scores, setScores] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchScores = async () => {
            try {
                const response = await axios.get('/api/scoreboard/top/10');
                setScores(Object.entries(response.data));
                setLoading(false);
            } catch (err) {
                setError('Failed to fetch scores');
                setLoading(false);
            }
        };

        fetchScores();
        // 5초마다 스코어보드 갱신
        const interval = setInterval(fetchScores, 5000);
        return () => clearInterval(interval);
    }, []);

    if (loading) return <div>Loading...</div>;
    if (error) return <div className="error">{error}</div>;

    return (
        <div className="scoreboard">
            <h2>Live Scoreboard</h2>
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