import React from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';

export default function EmailSend() {
    const [searchParams] = useSearchParams();
    const email = searchParams.get('email');
    const navigate = useNavigate();

    // 認証メールを再送する処理
    const handleSend = async () => {
        try {
            const res = await fetch(`http://localhost:8080/api/email/send?email=${email}`, {
                method: 'POST'
            });

            if (res.ok) {
                navigate('/email/verify', { state: { email } });
            } else {
                alert('メール送信に失敗しました');
            }
        } catch (err) {
            alert('サーバーエラーが発生しました');
        }
    };

    return (
        <div className="bg-gray-100 min-h-screen py-10 px-4">
            <div className="max-w-md mx-auto bg-white p-6 rounded-xl shadow text-center">
                <h2 className="text-2xl font-bold mb-4">メール認証送信</h2>
                <p className="mb-6">入力されたメールアドレス <span className="font-semibold text-blue-600">{email}</span>に認証コードを送信します。</p>
                <button
                    onClick={handleSend}
                    className="w-full bg-blue-500 hover:bg-blue-600 text-white font-semibold py-2 px-4 rounded"
                >
                    認証メールを送信
                </button>
            </div>
        </div>
    );
}
