import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate, useLocation } from 'react-router-dom';

export default function EmailVerify() {
    const navigate = useNavigate();
    const location = useLocation();

    // 状態：メール、認証コード、表示メッセージ、送信中フラグ
    const [email] = useState(
        location.state?.email ||
        localStorage.getItem('pendingEmail') ||
        ''
    );
    const [code, setCode] = useState('');
    const [message, setMessage] = useState('');
    const [sending, setSending] = useState(false);

    // 認証コードを再送信する処理
    const handleResend = async () => {
        if (!email) return;
        setSending(true);
        try {
            await axios.post(
                'http://localhost:8080/api/email/send',
                null,
                { params: { email } }
            );
            setMessage('認証コードを再送信しました。メールをご確認ください。');
            localStorage.setItem('pendingEmail', email);
        } catch (err) {
            console.error('再送信失敗', err);
            setMessage('認証コードの再送信に失敗しました。');
        } finally {
            setSending(false);
        }
    };

    // 認証コードを検証する処理
    const handleVerify = async () => {
        const trimmed = code.trim();
        if (!email || !trimmed) {
            setMessage('メールアドレスと認証コードを入力してください。');
            return;
        }

        try {
            console.log('📦リクエスト送信内容:', { email, code: trimmed });

            await axios.post(
                'http://localhost:8080/api/email/verify',
                { email, code: trimmed },
                { headers: { 'Content-Type': 'application/json' } }
            );

            localStorage.removeItem('pendingEmail');
            setMessage('認証に成功しました。メインページへ移動します。');
            setTimeout(() => navigate('/'), 1000);
        } catch (err) {
            const errorMsg = err.response?.data?.message || err.message;
            console.error('認証失敗詳細:', err.response?.data || err);
            setMessage(`認証に失敗しました: ${errorMsg}`);
        }
    };

    return (
        <div className="bg-gray-100 min-h-screen py-10 px-4">
            <div className="max-w-md mx-auto bg-white p-6 rounded-xl shadow text-center">
                <h2 className="text-2xl font-bold mb-4">🔐メール認証確認</h2>
                <p className="mb-6">
                    メールアドレス
                    <span className="font-semibold text-blue-600">{email}</span>に送信された認証コードを入力してください。
                </p>
                <input
                    type="text"
                    value={code}
                    onChange={(e) => setCode(e.target.value)}
                    placeholder="認証コード"
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring focus:border-blue-400 placeholder-gray-400"
                />

                <button
                    onClick={handleVerify}
                    className="w-full mt-4 bg-blue-500 hover:bg-blue-600 text-white font-semibold py-2 px-4 rounded"
                >
                    認証を確認する
                </button>

                <button
                    onClick={handleResend}
                    disabled={sending}
                    className="w-full mt-2 bg-gray-200 hover:bg-gray-300 text-gray-700 font-medium py-2 px-4 rounded"
                >
                    {sending ? '再送信中...' : '認証コードを再送信'}
                </button>

                {message && (
                    <p className="mt-4 text-sm font-medium">
                        {message}
                    </p>
                )}
            </div>
        </div>
    );
}
