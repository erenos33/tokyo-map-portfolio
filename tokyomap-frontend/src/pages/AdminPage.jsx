import React, { useEffect, useState } from 'react';
import axiosInstance from '../api/axiosInstance';
import { useNavigate } from 'react-router-dom';

const AdminPage = () => {
    const [message, setMessage] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        const fetchAdminPage = async () => {
            try {
                const response = await axiosInstance.get('http://localhost:8080/api/admin/only');
                setMessage(response.data);
            } catch (error) {
                setMessage('❌ 접근 실패 - 관리자 권한 필요');
            }
        };

        fetchAdminPage();
    }, []);

    return (
        <div style={{ padding: '40px' }}>
            <h2>👑 관리자 전용 페이지</h2>
            <p>{message}</p>
            <button onClick={() => navigate('/')} style={{ marginTop: '30px' }}>
                ⬅️ 메인페이지로 돌아가기
            </button>
        </div>
    );
};

export default AdminPage;
