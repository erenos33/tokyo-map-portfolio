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
        <div style={{ padding: 30 }}>
            <h2>📨 이메일 인증 발송</h2>
            <p>가입하신 이메일 <strong>{email}</strong>로 인증코드를 전송합니다.</p>
            <button onClick={handleSend}>✉️ 인증 메일 보내기</button>
        </div>
    );
}
