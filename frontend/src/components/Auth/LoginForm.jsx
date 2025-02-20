import axios from 'axios';
import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';

export default function LoginForm() {
  const [form, setForm] = useState({
    email: '',
    password: ''
  });
  const navigate = useNavigate();
  const { login } = useAuth();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const { data: token } = await axios.post('/api/auth/login', form);
      login(token);
      navigate('/scoreboard');
    } catch (error) {
      alert('로그인 실패');
    }
  };

  return (
    <div className="auth-container">
      <h2>Live Sports Now</h2>
      <form onSubmit={handleSubmit} className="auth-form">
        <input
          type="email"
          placeholder="이메일"
          value={form.email}
          onChange={e => setForm({...form, email: e.target.value})}
        />
        <input
          type="password"
          placeholder="비밀번호"
          value={form.password}
          onChange={e => setForm({...form, password: e.target.value})}
        />
        <button type="submit">로그인</button>
        <Link to="/signup" className="signup-link">회원가입</Link>
      </form>
    </div>
  );
} 