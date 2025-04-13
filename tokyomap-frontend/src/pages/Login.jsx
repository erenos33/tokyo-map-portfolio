import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

export default function Login() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [message, setMessage] = useState('');
    const navigate = useNavigate();

    const handleLogin = async () => {
        try {
            const res = await fetch('http://localhost:8080/api/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, password }),
            });

            if (res.ok) {
                const data = await res.json();
                localStorage.setItem('accessToken', data.accessToken);
                localStorage.setItem('expiresAt', data.expiresAt);
                localStorage.setItem('userRole', data.role); // 🔥 권한도 저장
                setMessage('✅ 로그인 성공!');
                navigate("/"); // 로그인 후 메인 페이지로 이동
            } else {
                setMessage('❌ 로그인 실패');
            }
        } catch (e) {
            console.error('로그인 에러:', e);
            setMessage('❌ 서버 오류');
        }
    };

    return (
        <div style={{ padding: 30 }}>
            <h2>🔑 로그인</h2>
            <input
                type="email"
                placeholder="이메일"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
            /><br /><br />
            <input
                type="password"
                placeholder="비밀번호"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
            /><br /><br />
            <button onClick={handleLogin}>로그인</button>
            <p>{message}</p>
        </div>
    );
}
