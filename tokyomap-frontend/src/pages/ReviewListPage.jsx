import React, { useState } from 'react';
import axiosInstance from '../api/axiosInstance';
import { useNavigate } from 'react-router-dom';

export default function ReviewListPage() {
    // 状態管理
    const [restaurantId, setRestaurantId] = useState('');
    const [reviews, setReviews] = useState([]);
    const navigate = useNavigate();

    // レビュー一覧取得
    const fetchReviews = async () => {
        try {
            const res = await axiosInstance.get(
                `/restaurants/${restaurantId}/reviews`,
                { params: { page: 0, size: 10, sort: 'createdAt,desc' } }
            );
            setReviews(res.data.data.content);
        } catch (error) {
            alert('レビュー取得に失敗しました。');
            console.error('レビュー取得失敗:', error);
        }
    };

    // レビュー削除
    const deleteReview = async (id) => {
        if (!window.confirm('本当にこのレビューを削除しますか？')) return;

        try {
            await axiosInstance.delete(`/reviews/${id}`);
            alert('レビューを削除しました。');
            fetchReviews();
        } catch (error) {
            console.error('レビュー削除失敗:', error);
            alert('レビューの削除に失敗しました。');
        }
    };

    // レビューにいいね
    const likeReview = async (id) => {
        try {
            await axiosInstance.post(`/reviews/${id}/like`);
            alert('いいねしました。');
            fetchReviews();
        } catch (error) {
            console.error('いいね失敗:', error);
            alert('いいねに失敗しました。');
        }
    };

    // いいねを取り消す
    const unlikeReview = async (id) => {
        try {
            await axiosInstance.delete(`/reviews/${id}/like`);
            alert('いいねを取り消しました。');
            fetchReviews();
        } catch (error) {
            console.error('いいね取り消し失敗:', error);
            alert('いいねの取り消しに失敗しました。');
        }
    };

    // いいね数を表示
    const getLikeCount = async (id) => {
        try {
            const response = await axiosInstance.get(`/reviews/${id}/likes/count`);
            alert(`いいね数: ${response.data.data.likeCount}`);
        } catch (error) {
            console.error('いいね数取得失敗:', error);
            alert('いいね数の取得に失敗しました。');
        }
    };

    // レビュー統計（平均評価・レビュー数）
    const getReviewStatistics = async () => {
        if (!restaurantId) {
            alert('レストランIDを先に入力してください。');
            return;
        }

        try {
            const response = await axiosInstance.get(`/restaurants/${restaurantId}/reviews/statistics`);
            const { averageRating, totalReviews } = response.data.data;
            alert(`平均評価: ${averageRating}, 総レビュー数: ${totalReviews}`);
        } catch (error) {
            console.error('統計取得失敗:', error);
            alert('レビュー統計の取得に失敗しました。');
        }
    };

    return (
        <div className="bg-gray-100 min-h-screen py-10 px-4">
            <div className="max-w-3xl mx-auto bg-white p-6 rounded-xl shadow">
                <h2 className="text-2xl font-bold mb-6 text-center">レビュー一覧ページ</h2>

                <div className="flex flex-col sm:flex-row gap-3 mb-6">
                    <input
                        type="text"
                        className="flex-1 px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring focus:border-blue-400 placeholder-gray-400"
                        placeholder="レストランIDを入力"
                        value={restaurantId}
                        onChange={(e) => setRestaurantId(e.target.value)}
                    />
                    <button className="btn" onClick={fetchReviews}>レビューを表示</button>
                    <button className="btn bg-green-500 hover:bg-green-600" onClick={getReviewStatistics}>統計を表示</button>
                </div>

                <div className="space-y-4">
                    {reviews.map((review) => (
                        <div key={review.id} className="border rounded-lg p-4 shadow-sm bg-gray-50">
                            <h4 className="text-lg font-semibold mb-1">評価: {review.rating}</h4>
                            <p className="text-gray-800 mb-2">{review.content}</p>
                            <p className="text-sm text-gray-500 mb-3">作成者: {review.author}</p>
                            <div className="flex flex-wrap gap-2">
                                <button className="btn bg-yellow-500 hover:bg-yellow-600" onClick={() => navigate(`/review/edit/${review.id}`)}>修正</button>
                                <button className="btn bg-red-500 hover:bg-red-600" onClick={() => deleteReview(review.id)}>削除</button>
                                <button className="btn" onClick={() => likeReview(review.id)}>いいね</button>
                                <button className="btn" onClick={() => unlikeReview(review.id)}>いいね取消</button>
                                <button className="btn bg-gray-400 hover:bg-gray-500" onClick={() => getLikeCount(review.id)}>いいね数を見る</button>
                            </div>
                        </div>
                    ))}
                </div>
                <div className="mt-10 text-center">
                    <button
                        className="btn bg-blue-500 hover:bg-blue-600 text-white"
                        onClick={() => navigate('/')}
                    >
                        メインページに戻る
                    </button>
                </div>
            </div>
        </div>
    );
}