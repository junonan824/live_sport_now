import React from 'react';
import './App.css';
import Scoreboard from './components/Scoreboard';

function App() {
  return (
    <div className="App">
      <header className="App-header">
        <h1>Live Sports Now</h1>
      </header>
      <main>
        <Scoreboard />
      </main>
    </div>
  );
}

export default App;
