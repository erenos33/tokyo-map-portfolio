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
        <div className="p-6 max-w-2xl mx-auto">
            <h1 className="text-2xl font-bold mb-6 text-center">🍜 도쿄 맛집 포트폴리오 메인페이지</h1>

            {token ? (
                <div className="space-y-3">
                    <p className="text-green-600 font-semibold">🔐 로그인된 사용자입니다</p>
                    <div className="flex flex-col gap-2">
                        <button className="btn" onClick={() => navigate('/user')}>👦 유저 전용 페이지</button>
                        {role === 'ADMIN' && (
                            <button className="btn" onClick={() => navigate('/admin')}>🛠 관리자 전용 페이지</button>
                        )}
                        <button className="btn" onClick={() => navigate('/restaurant')}>🍴 맛집 검색하기</button>
                        <button className="btn" onClick={() => navigate('/review/list')}>📖 리뷰 조회하기</button>
                        <button className="btn" onClick={() => navigate('/review/comments/view')}>📖 리뷰 댓글 조회</button>
                        <button className="btn" onClick={() => navigate('/locations')}>🌏 지역 목록 조회</button>
                        <button className="btn bg-red-500 hover:bg-red-600 text-white" onClick={handleLogout}>📕 로그아웃</button>
                    </div>
                </div>
            ) : (
                <div className="flex flex-col gap-3">
                    <div className="flex gap-2">
                        <button className="btn" onClick={() => navigate('/login')}>🔐 로그인 하러가기</button>
                        <button className="btn" onClick={() => navigate('/register')}>✍️ 회원가입</button>
                    </div>
                    <button className="btn" onClick={() => navigate('/restaurant')}>🍴 맛집 검색하기</button>
                    <button className="btn" onClick={() => navigate('/review/list')}>📖 리뷰 조회하기</button>
                    <button className="btn" onClick={() => navigate('/review/comments/view')}>📖 리뷰 댓글 조회</button>
                    <button className="btn" onClick={() => navigate('/locations')}>🌏 지역 목록 조회</button>
                </div>
            )}
        </div>
    );
}
