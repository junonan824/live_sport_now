import React, { useState } from 'react';
import LiveMatch from './components/LiveMatch';
import './App.css';

function App() {
    const [matchId, setMatchId] = useState(null);

    const startNewMatch = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/simulation/matches', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    homeTeam: "Tottenham",
                    awayTeam: "Manchester City"
                })
            });
            
            const data = await response.json();
            setMatchId(data.matchId);
        } catch (error) {
            console.error('Error starting match:', error);
        }
    };

    return (
        <div className="App">
            <header className="App-header">
                <h1>Live Sports Now</h1>
                {!matchId && (
                    <button onClick={startNewMatch}>Start New Match</button>
                )}
                {matchId && (
                    <LiveMatch matchId={matchId} />
                )}
            </header>
        </div>
    );
}

export default App;
