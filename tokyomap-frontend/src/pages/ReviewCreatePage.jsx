import React, { useState } from 'react';
import axiosInstance from '../api/axiosInstance';
import { useNavigate } from 'react-router-dom';

export default function ReviewCreatePage() {
    // フォーム入力状態
    const [restaurantId, setRestaurantId] = useState('');
    const [rating, setRating] = useState(5);
    const [content, setContent] = useState('');
    const navigate = useNavigate();

    // レビュー作成リクエスト送信
    const createReview = async () => {
        try {
            await axiosInstance.post(`/reviews`, {
                restaurantId: Number(restaurantId),
                rating: Number(rating),
                content,
            });
            alert('レビューを作成しました。');
            navigate('/');
        } catch (error) {
            console.error('レビュー作成失敗:', error);
            alert('レビューの作成に失敗しました。');
        }
    };

    return (
        <div className="bg-gray-100 min-h-screen py-10 px-4">
            <div className="max-w-xl mx-auto bg-white p-6 rounded-xl shadow">
                <h2 className="text-2xl font-bold mb-6 text-center">レビュー作成ページ</h2>

                <div className="mb-4">
                    <label className="block text-sm font-medium text-gray-700 mb-1">レストランID</label>
                    <input
                        type="text"
                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring focus:border-blue-400 placeholder-gray-400"
                        placeholder="ex) 1"
                        value={restaurantId}
                        onChange={(e) => setRestaurantId(e.target.value)}
                    />
                </div>

                <div className="mb-4">
                    <label className="block text-sm font-medium text-gray-700 mb-1">評価（1〜5）</label>
                    <input
                        type="number"
                        min="1"
                        max="5"
                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring focus:border-blue-400"
                        value={rating}
                        onChange={(e) => setRating(e.target.value)}
                    />
                </div>

                <div className="mb-6">
                    <label className="block text-sm font-medium text-gray-700 mb-1">レビュー内容</label>
                    <textarea
                        rows="5"
                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring focus:border-blue-400 resize-none"
                        placeholder="レビューを入力してください"
                        value={content}
                        onChange={(e) => setContent(e.target.value)}
                    ></textarea>
                </div>

                <button
                    className="w-full bg-blue-500 hover:bg-blue-600 text-white font-semibold py-2 px-4 rounded"
                    onClick={createReview}
                >
                    レビューを投稿する
                </button>
            </div>
            <div className="mt-10 text-center">
                <button
                    className="btn bg-blue-500 hover:bg-blue-600 text-white"
                    onClick={() => window.location.href = '/'}
                >
                    メインページに戻る
                </button>
            </div>
        </div>
    );
}