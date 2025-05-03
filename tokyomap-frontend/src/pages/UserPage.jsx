// 수정된 UserPage.jsx
import React, { useEffect, useState } from 'react';
import axiosInstance from '../api/axiosInstance';
import { useNavigate } from 'react-router-dom';

const UserPage = () => {
    const [message, setMessage] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        const fetchUserPage = async () => {
            try {
                const response = await axiosInstance.get('/auth/test');
                setMessage(response.data.data); // 수정된 부분
            } catch (error) {
                setMessage('❌ 접근 실패 - 유효한 사용자 아님');
            }
        };

        fetchUserPage();
    }, []);

    return (
        <div style={{ padding: '40px' }}>
            <h2>👦 사용자 전용 페이지</h2>
            <p>{message}</p>
            <button onClick={() => navigate('/')} style={{ marginTop: '30px' }}>
                ⬅️ 메인페이지로 돌아가기
            </button>
        </div>
    );
};

export default UserPage;