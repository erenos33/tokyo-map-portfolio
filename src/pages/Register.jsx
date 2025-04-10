// src/pages/Register.jsx
import React, { useState } from 'react';
import { registerUser } from '../api/userApi';

export default function Register() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [nickname, setNickname] = useState('');
    const [message, setMessage] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        const success = await registerUser({ email, password, nickname });
        if (success) {
            setMessage('✅ 회원가입 성공!');
            setEmail('');
            setPassword('');
            setNickname('');
        } else {
            setMessage('❌ 회원가입 실패! 입력값 확인 또는 중복 이메일 확인');
        }
    };

    return (
        <div style={{ padding: 30 }}>
            <h2>회원가입</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>이메일:</label><br />
                    <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
                </div>
                <div>
                    <label>비밀번호:</label><br />
                    <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
                </div>
                <div>
                    <label>닉네임:</label><br />
                    <input type="text" value={nickname} onChange={(e) => setNickname(e.target.value)} required />
                </div>
                <br />
                <button type="submit">회원가입</button>
            </form>
            <p>{message}</p>
        </div>
    );
}
