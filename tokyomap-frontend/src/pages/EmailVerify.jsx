// EmailVerify.jsx
import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate, useLocation } from 'react-router-dom';

export default function EmailVerify() {
    const navigate = useNavigate();
    const location = useLocation();

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

            const result = response.data;
            const { accessToken, expiresAt, role } = result.data;
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
        <div className="bg-gray-100 min-h-screen py-10 px-4">
            <div className="max-w-md mx-auto bg-white p-6 rounded-xl shadow text-center">
                <h2 className="text-2xl font-bold mb-4">🔐 이메일 인증 확인</h2>
                <p className="mb-6">
                    <span className="font-semibold text-blue-600">{email}</span>로 받은 인증 코드를 입력하세요.
                </p>
                <input
                    type="text"
                    value={code}
                    onChange={(e) => setCode(e.target.value)}
                    placeholder="인증 코드"
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring focus:border-blue-400 placeholder-gray-400"
                />
                <button
                    onClick={handleVerify}
                    className="w-full mt-4 bg-blue-500 hover:bg-blue-600 text-white font-semibold py-2 px-4 rounded"
                >
                    ✅ 인증 확인
                </button>
                {message && <p className="mt-4 text-sm font-medium">{message}</p>}
            </div>
        </div>
    );
}
