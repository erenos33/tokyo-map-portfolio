// EmailVerify.jsx
import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate, useLocation } from 'react-router-dom';

export default function EmailVerify() {
    const navigate = useNavigate();
    const location = useLocation();

    const [email] = useState(
        location.state?.email ||
        localStorage.getItem('pendingEmail') ||
        ''
    );
    const [code, setCode] = useState('');
    const [message, setMessage] = useState('');
    const [sending, setSending] = useState(false);

    const handleResend = async () => {
        if (!email) return;
        setSending(true);
        try {
            await axios.post(
                'http://localhost:8080/api/email/send',
                null,
                { params: { email } }
            );
            setMessage('✅ 인증 코드 다시 발송 완료! 이메일을 확인하세요.');
            localStorage.setItem('pendingEmail', email);
        } catch (err) {
            console.error('❌ 재발송 실패', err);
            setMessage('❌ 인증 코드를 다시 발송하는 데 실패했습니다.');
        } finally {
            setSending(false);
        }
    };

    const handleVerify = async () => {
        const trimmed = code.trim();
        if (!email || !trimmed) {
            setMessage('❌ 이메일과 인증 코드를 모두 입력해주세요.');
            return;
        }

        try {
            console.log('📦 최종 요청 payload', { email, code: trimmed });

            // HTTP 200 이면 무조건 성공 처리
            await axios.post(
                'http://localhost:8080/api/email/verify',
                { email, code: trimmed },
                { headers: { 'Content-Type': 'application/json' } }
            );

            localStorage.removeItem('pendingEmail');
            setMessage('✅ 인증 성공! 메인페이지로 이동 중...');
            setTimeout(() => navigate('/'), 1000);
        } catch (err) {
            const errorMsg = err.response?.data?.message || err.message;
            console.error('❌ 인증 실패 상세:', err.response?.data || err);
            setMessage(`❌ 인증 실패: ${errorMsg}`);
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

                <button
                    onClick={handleResend}
                    disabled={sending}
                    className="w-full mt-2 bg-gray-200 hover:bg-gray-300 text-gray-700 font-medium py-2 px-4 rounded"
                >
                    {sending ? '⏳ 재발송 중...' : '🔄 인증 코드 재발송'}
                </button>

                {message && (
                    <p className="mt-4 text-sm font-medium">
                        {message}
                    </p>
                )}
            </div>
        </div>
    );
}
