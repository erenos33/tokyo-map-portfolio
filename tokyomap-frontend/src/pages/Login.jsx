import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

export default function Login() {
    const [email, setEmail]       = useState('');
    const [password, setPassword] = useState('');
    const [message, setMessage]   = useState('');
    const navigate                = useNavigate();

    // ログイン処理
    const handleLogin = async () => {
        try {
            const res = await fetch(`${import.meta.env.VITE_API_BASE_URL}/api/auth/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, password }),
            });

            if (!res.ok) {
                let errorData;
                try {
                    errorData = await res.json(); // JSONレスポンスを解析（成功時）
                } catch {
                    const errorText = await res.text(); // 空のレスポンスまたはJSON以外の場合の処理
                    console.error("レスポンスが空またはJSON形式ではありません:", errorText);
                    setMessage('ログインに失敗しました。');
                    return;
                }

                // メール認証が未完了の場合のリダイレクト処理
                if (errorData.errorCode === 'EMAIL_NOT_VERIFIED') {
                    setMessage('メール認証が必要です。認証ページに移動します。');
                    localStorage.setItem('pendingEmail', email);
                    setTimeout(() => {
                        navigate(`/email/send?email=${encodeURIComponent(email)}`);
                    }, 2000);
                    return;
                }

                // その他のエラー時のメッセージ
                setMessage('ログインに失敗しました。');
                return;
            }


            // ログイン成功時、トークン保存とリダイレクト
            const result = await res.json();
            const { accessToken, expiresAt, role } = result.data;

            localStorage.setItem('accessToken', accessToken);
            localStorage.setItem('expiresAt', expiresAt);
            localStorage.setItem('userRole', role);
            setMessage('ログインに成功しました。メインページへ移動します。');
            setTimeout(() => navigate('/'), 1000);
        } catch (e) {
            console.error('ログインエラー:', e);
            setMessage('サーバーエラーが発生しました。');
        }
    };

    return (
        <div className="bg-gray-100 min-h-screen py-10 px-4">
            <div className="max-w-md mx-auto bg-white p-6 rounded-xl shadow">
                <h2 className="text-2xl font-bold mb-6 text-center">ログイン</h2>

                <div className="mb-4">
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                        メールアドレス
                    </label>
                    <input
                        type="email"
                        placeholder="メールアドレスを入力"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring focus:border-blue-400"
                    />
                </div>

                <div className="mb-6">
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                        パスワード
                    </label>
                    <input
                        type="password"
                        placeholder="パスワードを入力"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring focus:border-blue-400"
                    />
                </div>

                <button
                    onClick={handleLogin}
                    className="w-full bg-blue-500 hover:bg-blue-600 text-white font-semibold py-2 px-4 rounded"
                >
                    ログイン
                </button>

                {message && (
                    <p className="mt-4 text-center text-sm font-medium">{message}</p>
                )}
            </div>
        </div>
    );
}
