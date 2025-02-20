import axios from 'axios';
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

export default function SignupForm() {
  const [form, setForm] = useState({
    email: '',
    password: '',
    nickname: ''
  });
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await axios.post('/api/auth/signup', form);
      navigate('/login');
    } catch (error) {
      alert('회원가입 실패');
    }
  };

  return (
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
      <input
        type="text"
        placeholder="닉네임"
        value={form.nickname}
        onChange={e => setForm({...form, nickname: e.target.value})}
      />
      <button type="submit">회원가입</button>
    </form>
  );
} 