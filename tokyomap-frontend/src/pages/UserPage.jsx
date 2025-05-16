import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axiosInstance from '../api/axiosInstance';

const UserPage = () => {
    // 認証メッセージとユーザー権限の状態管理
    const [message, setMessage] = useState('');
    const [role, setRole] = useState('');
    const navigate = useNavigate();

    // 認証済みユーザー情報を取得
    useEffect(() => {
        const fetchUserInfo = async () => {
            try {
                const response = await axiosInstance.get('/auth/test');
                setMessage(response.data.data);
                const userRole = localStorage.getItem('userRole');
                setRole(userRole);
            } catch (error) {
                setMessage('アクセス失敗：認証されていないユーザーです');
            }
        };

        fetchUserInfo();
    }, []);

    // ログアウト処理
    const handleLogout = () => {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('userRole');
        navigate('/');
    };

    return (
        <div className="p-6 max-w-2xl mx-auto">
            <h2 className="text-2xl font-bold mb-4">ユーザーページ</h2>
            <p className="mb-6">{message}</p>

            <div className="flex flex-col gap-3">
                <button className="btn" onClick={() => navigate('/restaurant/my')}>登録したレストラン一覧</button>
                <button className="btn" onClick={() => navigate('/review/create')}>レビューを作成</button>
                <button className="btn" onClick={() => navigate('/review/comments')}>コメントを書く</button>
                <button className="btn" onClick={() => navigate('/favorites')}>お気に入り管理</button>
                {role === 'ADMIN' && (
                    <button className="btn" onClick={() => navigate('/admin')}>管理者ページ</button>
                )}
                <button className="btn bg-red-500 hover:bg-red-600 text-white" onClick={handleLogout}>ログアウト</button>
                <button className="btn" onClick={() => navigate('/')}>⬅メインページへ戻る</button>
            </div>
        </div>
    );
};

export default UserPage;
