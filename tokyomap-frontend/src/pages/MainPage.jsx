// src/pages/MainPage.jsx
import React from 'react';
import { useNavigate } from 'react-router-dom';

export default function MainPage() {
    const navigate = useNavigate();
    const token = localStorage.getItem('accessToken');
    const role = localStorage.getItem('userRole');

    const handleLogout = () => {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('userRole');
        navigate('/');
    };

    return (
        <div style={{ padding: 30 }}>
            <h1>🍜 도쿄 맛집 포트폴리오 메인페이지</h1>
            {token ? (
                <>
                    <p>🔐 로그인된 사용자입니다</p>
                    <button onClick={() => navigate('/user')}>👦 유저 전용 페이지</button>
                    {role === 'ADMIN' && (
                        <button onClick={() => navigate('/admin')}>🛠 관리자 전용 페이지</button>
                    )}
                    <br /><br />
                    <button onClick={() => navigate('/restaurant')}>🍴 맛집 검색하기</button>
                    <br /><br />
                    <button onClick={() => navigate('/review/create')}>✍️ 리뷰 작성하기</button> {/* ✅ 추가 */}
                    <br /><br />
                    <button onClick={() => navigate('/review/list')}>📖 리뷰 조회하기</button> {/* ✅ 추가 */}
                    <br /><br />
                    <br /><br />
                    <button onClick={handleLogout}>📕 로그아웃</button>
                </>
            ) : (
                <>
                    <button onClick={() => navigate('/login')}>🔐 로그인 하러가기</button>
                    <button onClick={() => navigate('/register')}>✍️ 회원가입</button>
                    <br /><br />
                    <button onClick={() => navigate('/restaurant')}>🍴 맛집 검색하기</button>
                    <br /><br />
                    <button onClick={() => navigate('/review/list')}>📖 리뷰 조회하기</button> {/* ✅ 추가 */}
                </>
            )}
        </div>
    );
}
