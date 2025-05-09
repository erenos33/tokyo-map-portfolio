import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axiosInstance from '../api/axiosInstance';

const UserPage = () => {
    const [message, setMessage] = useState('');
    const [role, setRole] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        const fetchUserInfo = async () => {
            try {
                const response = await axiosInstance.get('/auth/test');
                setMessage(response.data.data); // 인증된 사용자 메시지
                const userRole = localStorage.getItem('userRole');
                setRole(userRole);
            } catch (error) {
                setMessage('❌ 접근 실패 - 유효한 사용자 아님');
            }
        };

        fetchUserInfo();
    }, []);

    const handleLogout = () => {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('userRole');
        navigate('/');
    };

    return (
        <div className="p-6 max-w-2xl mx-auto">
            <h2 className="text-2xl font-bold mb-4">👦 사용자 전용 페이지</h2>
            <p className="mb-6">{message}</p>

            <div className="flex flex-col gap-3">
                <button className="btn" onClick={() => navigate('/restaurant/my')}>📋 내가 등록한 맛집</button>
                <button className="btn" onClick={() => navigate('/review/create')}>✍️ 리뷰 작성하기</button>
                <button className="btn" onClick={() => navigate('/review/comments')}>💬 댓글 작성하기</button>
                <button className="btn" onClick={() => navigate('/favorites')}>⭐ 즐겨찾기 관리</button>
                {role === 'ADMIN' && (
                    <button className="btn" onClick={() => navigate('/admin')}>🛠 관리자 페이지</button>
                )}
                <button className="btn bg-red-500 hover:bg-red-600 text-white" onClick={handleLogout}>📕 로그아웃</button>
                <button className="btn" onClick={() => navigate('/')}>⬅️ 메인페이지로</button>
            </div>
        </div>
    );
};

export default UserPage;
