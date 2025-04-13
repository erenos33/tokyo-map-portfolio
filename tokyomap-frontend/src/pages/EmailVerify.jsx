// src/pages/EmailVerify.jsx
import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate, useLocation } from 'react-router-dom';

export default function EmailVerify() {
    const navigate = useNavigate();
    const location = useLocation();

    // 이메일은 localStorage 또는 이전 페이지에서 받아오기
    const [email] = useState(location.state?.email || localStorage.getItem('pendingEmail') || '');
    const [code, setCode] = useState('');
    const [message, setMessage] = useState('');

    const handleVerify = async () => {
        try {
            const response = await axios.post(
                'http://localhost:8080/api/email/verify',
                {
                    email,
                    code: code.trim(),
                },
                {
                    headers: {
                        'Content-Type': 'application/json',
                    },
                }
            );

            // 인증 성공 시 accessToken, role 저장 후 메인으로
            const { accessToken, expiresAt, role } = response.data;
            localStorage.setItem('accessToken', accessToken);
            localStorage.setItem('expiresAt', expiresAt);
            localStorage.setItem('userRole', role);
            localStorage.removeItem('pendingEmail');

            setMessage('✅ 인증 성공! 메인페이지로 이동 중...');
            setTimeout(() => navigate('/'), 1000);
        } catch (err) {
            console.error('❌ 인증 실패:', err);
            setMessage('❌ 인증 실패: 유효하지 않거나 만료된 인증 코드입니다.');
        }
    };

    return (
        <div style={{ padding: 30 }}>
            <h2>🔐 이메일 인증 확인</h2>
            <p>
                <strong>{email}</strong>로 받은 인증 코드를 입력하세요.
            </p>
            <input
                type="text"
                value={code}
                onChange={(e) => setCode(e.target.value)}
                placeholder="인증 코드"
            />
            <br /><br />
            <button onClick={handleVerify}>✅ 인증 확인</button>
            <p>{message}</p>
        </div>
    );
}