import React, { useState, useEffect } from 'react';
import axiosInstance from '../api/axiosInstance';
import { useNavigate, useParams } from 'react-router-dom';

export default function ReviewEditPage() {
    // 入力状態管理
    const [restaurantId, setRestaurantId] = useState('');
    const [rating, setRating] = useState(5);
    const [content, setContent] = useState('');
    const navigate = useNavigate();
    const { id } = useParams();

    // レビュー詳細取得（初回マウント時）
    useEffect(() => {
        const fetchReview = async () => {
            try {
                const response = await axiosInstance.get(`/reviews/${id}`);
                const review = response.data.data;
                setRestaurantId(review.restaurantId);
                setRating(review.rating);
                setContent(review.content);
            } catch (error) {
                console.error('レビュー取得失敗:', error);
            }
        };
        fetchReview();
    }, [id]);

    // レビュー更新リクエスト送信
    const updateReview = async () => {
        try {
            await axiosInstance.put(`/reviews/${id}`, {
                restaurantId: Number(restaurantId),
                rating: Number(rating),
                content,
            });
            alert('レビューを修正しました。');
            navigate('/');
        } catch (error) {
            console.error('レビュー修正失敗:', error);
            alert('レビューの修正に失敗しました。');
        }
    };

    return (
        <div className="bg-gray-100 min-h-screen py-10 px-4">
            <div className="max-w-xl mx-auto bg-white p-6 rounded-xl shadow">
                <h2 className="text-2xl font-bold mb-6 text-center">レビュー修正ページ</h2>

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
                    onClick={updateReview}
                >
                    レビューを修正する
                </button>
            </div>
        </div>
    );
}
