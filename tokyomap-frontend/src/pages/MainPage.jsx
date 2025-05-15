import React from 'react';
import { useNavigate } from 'react-router-dom';

export default function MainPage() {
    const navigate = useNavigate();
    const token = localStorage.getItem('accessToken');
    const role = localStorage.getItem('userRole');

    // ログアウト処理
    const handleLogout = () => {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('userRole');
        navigate('/');
    };

    return (
        <div className="p-6 max-w-2xl mx-auto">
            <h1 className="text-2xl font-bold mb-6 text-center">グルメポートフォリオ メインページ</h1>

            {token ? (
                // ログイン済みのユーザー向け
                <div className="space-y-3">
                    <p className="text-green-600 font-semibold">ログイン中のユーザーです</p>
                    <div className="flex flex-col gap-2">
                        <button className="btn" onClick={() => navigate('/user')}>ユーザー専用ページ</button>
                        {role === 'ADMIN' && (
                            <button className="btn" onClick={() => navigate('/admin')}>管理者専用ページ</button>
                        )}
                        <button className="btn" onClick={() => navigate('/restaurant')}>飲食店を検索する</button>
                        <button className="btn" onClick={() => navigate('/review/list')}>レビュー一覧</button>
                        <button className="btn" onClick={() => navigate('/review/comments/view')}>レビューコメント一覧</button>
                        <button className="btn" onClick={() => navigate('/locations')}>地域一覧</button>
                        <button className="btn bg-red-500 hover:bg-red-600 text-white" onClick={handleLogout}>ログアウト</button>
                    </div>
                </div>
            ) : (
                <div className="flex flex-col gap-3">
                    <div className="flex gap-2">
                        <button className="btn" onClick={() => navigate('/login')}>ログイン</button>
                        <button className="btn" onClick={() => navigate('/register')}>新規登録</button>
                    </div>
                    <button className="btn" onClick={() => navigate('/restaurant')}>飲食店を検索する</button>
                    <button className="btn" onClick={() => navigate('/review/list')}>レビュー一覧</button>
                    <button className="btn" onClick={() => navigate('/review/comments/view')}>レビューコメント一覧</button>
                    <button className="btn" onClick={() => navigate('/locations')}>地域一覧</button>
                </div>
            )}
        </div>
    );
}
