// src/pages/EmailSend.jsx
import React from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';

export default function EmailSend() {
    const [searchParams] = useSearchParams();
    const email = searchParams.get('email');
    const navigate = useNavigate();

    const handleSend = async () => {
        try {
            const res = await fetch(`http://localhost:8080/api/email/send?email=${email}`, {
                method: 'POST'
            });

            if (res.ok) {
                navigate('/email/verify', { state: { email } });
            } else {
                alert('❌ 이메일 발송 실패');
            }
        } catch (err) {
            alert('❌ 서버 오류 발생');
        }
    };

    return (
        <div className="bg-gray-100 min-h-screen py-10 px-4">
            <div className="max-w-md mx-auto bg-white p-6 rounded-xl shadow text-center">
                <h2 className="text-2xl font-bold mb-4">📨 이메일 인증 발송</h2>
                <p className="mb-6">가입하신 이메일 <span className="font-semibold text-blue-600">{email}</span>로 인증코드를 전송합니다.</p>
                <button
                    onClick={handleSend}
                    className="w-full bg-blue-500 hover:bg-blue-600 text-white font-semibold py-2 px-4 rounded"
                >
                    ✉️ 인증 메일 보내기
                </button>
            </div>
        </div>
    );
}
