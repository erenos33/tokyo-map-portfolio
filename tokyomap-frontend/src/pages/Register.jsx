import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

export default function Register() {
    const navigate = useNavigate();

    // 入力フォームの状態管理
    const [form, setForm] = useState({ email: '', password: '', nickname: '' });

    // メッセージの状態（成功・失敗の表示用）
    const [message, setMessage] = useState('');

    // 入力変更時にフォームの状態を更新
    const handleChange = (e) => {
        const { name, value } = e.target;
        setForm({ ...form, [name]: value });
    };

    // 登録ボタンをクリックしたときの処理
    const handleRegister = async () => {
        try {
            const res = await fetch('http://localhost:8080/api/users/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(form)
            });

            if (res.ok) {
                setMessage('会員登録に成功しました。認証メールを送信します。');
                navigate(`/email/send?email=${form.email}`);
            } else {
                const data = await res.json();
                setMessage(`${data.message || '会員登録に失敗しました。'}`);
            }
        } catch (e) {
            setMessage('サーバーエラーが発生しました。');
        }
    };

    return (
        <div className="bg-gray-100 min-h-screen py-10 px-4">
            <div className="max-w-md mx-auto bg-white p-6 rounded-xl shadow">
                <h2 className="text-2xl font-bold mb-6 text-center">会員登録</h2>

                <div className="mb-4">
                    <label className="block text-sm font-medium text-gray-700 mb-1">メールアドレス</label>
                    <input
                        type="email"
                        name="email"
                        placeholder="メールアドレス"
                        value={form.email}
                        onChange={handleChange}
                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring focus:border-blue-400"
                    />
                </div>

                <div className="mb-4">
                    <label className="block text-sm font-medium text-gray-700 mb-1">パスワード</label>
                    <input
                        type="password"
                        name="password"
                        placeholder="パスワード"
                        value={form.password}
                        onChange={handleChange}
                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring focus:border-blue-400"
                    />
                </div>

                <div className="mb-6">
                    <label className="block text-sm font-medium text-gray-700 mb-1">ニックネーム</label>
                    <input
                        type="text"
                        name="nickname"
                        placeholder="ニックネーム"
                        value={form.nickname}
                        onChange={handleChange}
                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring focus:border-blue-400"
                    />
                </div>

                <button
                    onClick={handleRegister}
                    className="w-full bg-blue-500 hover:bg-blue-600 text-white font-semibold py-2 px-4 rounded"
                >
                    会員登録
                </button>

                {message && <p className="mt-4 text-sm font-medium text-center">{message}</p>}
            </div>
        </div>
    );
}
