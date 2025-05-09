// src/pages/Login.jsx
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

export default function Login() {
    const [email, setEmail]       = useState('');
    const [password, setPassword] = useState('');
    const [message, setMessage]   = useState('');
    const navigate                = useNavigate();

    const handleLogin = async () => {
        try {
            const res = await fetch('http://localhost:8080/api/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, password }),
            });

            if (!res.ok) {
                const errorData = await res.json();

                // 이메일 인증이 안 된 경우 → 메시지 보여주고 2초 뒤 리다이렉트
                if (errorData.errorCode === 'EMAIL_NOT_VERIFIED') {
                    setMessage('🔔 이메일 인증이 필요합니다. 인증 메일페이지로 이동합니다.');
                    localStorage.setItem('pendingEmail', email);
                    setTimeout(() => {
                        navigate(`/email/send?email=${encodeURIComponent(email)}`);
                    }, 2000);
                    return;
                }

                // 그 외 로그인 실패
                setMessage('❌ 로그인 실패');
                return;
            }

            // 로그인 성공
            const result = await res.json();
            const { accessToken, expiresAt, role } = result.data;

            localStorage.setItem('accessToken', accessToken);
            localStorage.setItem('expiresAt', expiresAt);
            localStorage.setItem('userRole', role);
            setMessage('✅ 로그인 성공! 메인페이지로 이동 중...');
            setTimeout(() => navigate('/'), 1000);
        } catch (e) {
            console.error('로그인 에러:', e);
            setMessage('❌ 서버 오류');
        }
    };

    return (
        <div className="bg-gray-100 min-h-screen py-10 px-4">
            <div className="max-w-md mx-auto bg-white p-6 rounded-xl shadow">
                <h2 className="text-2xl font-bold mb-6 text-center">🔑 로그인</h2>

                <div className="mb-4">
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                        이메일
                    </label>
                    <input
                        type="email"
                        placeholder="이메일"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring focus:border-blue-400"
                    />
                </div>

                <div className="mb-6">
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                        비밀번호
                    </label>
                    <input
                        type="password"
                        placeholder="비밀번호"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring focus:border-blue-400"
                    />
                </div>

                <button
                    onClick={handleLogin}
                    className="w-full bg-blue-500 hover:bg-blue-600 text-white font-semibold py-2 px-4 rounded"
                >
                    로그인
                </button>

                {message && (
                    <p className="mt-4 text-center text-sm font-medium">{message}</p>
                )}
            </div>
        </div>
    );
}
