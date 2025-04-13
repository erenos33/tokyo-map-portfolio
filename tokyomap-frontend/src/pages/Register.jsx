// src/pages/Register.jsx
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

export default function Register() {
    const navigate = useNavigate();
    const [form, setForm] = useState({ email: '', password: '', nickname: '' });
    const [message, setMessage] = useState('');

    const handleChange = (e) => {
        const { name, value } = e.target;
        setForm({ ...form, [name]: value });
    };

    const handleRegister = async () => {
        try {
            const res = await fetch('http://localhost:8080/api/users/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(form)
            });

            if (res.ok) {
                setMessage('✅ 회원가입 성공! 인증 메일을 발송합니다.');
                navigate(`/email/send?email=${form.email}`); // 🔁 다음 페이지로 이동
            } else {
                const data = await res.json();
                setMessage(`❌ ${data.message || '회원가입 실패'}`);
            }
        } catch (e) {
            setMessage('❌ 서버 오류 발생');
        }
    };

    return (
        <div style={{ padding: 30 }}>
            <h2>✍️ 회원가입</h2>
            <input
                type="email"
                name="email"
                placeholder="이메일"
                value={form.email}
                onChange={handleChange}
            /><br /><br />
            <input
                type="password"
                name="password"
                placeholder="비밀번호"
                value={form.password}
                onChange={handleChange}
            /><br /><br />
            <input
                type="text"
                name="nickname"
                placeholder="닉네임"
                value={form.nickname}
                onChange={handleChange}
            /><br /><br />
            <button onClick={handleRegister}>회원가입</button>
            <p>{message}</p>
        </div>
    );
}
