import React, { useState } from 'react';
import axiosInstance from '../api/axiosInstance';
import { useNavigate } from 'react-router-dom';

export default function ReviewListPage() {
    const [restaurantId, setRestaurantId] = useState('');
    const [reviews, setReviews] = useState([]);
    const navigate = useNavigate();

    const fetchReviews = async () => {
        try {
            const res = await axiosInstance.get(
                `/restaurants/${restaurantId}/reviews`,
                { params: { page: 0, size: 10, sort: 'createdAt,desc' } }
            );
            setReviews(res.data.data.content);
        } catch (error) {
            alert('리뷰 조회 실패');
            console.error('리뷰 조회 실패:', error);
        }
    };

    const deleteReview = async (id) => {
        if (!window.confirm('정말 이 리뷰를 삭제할까요?')) return;

        try {
            await axiosInstance.delete(`/reviews/${id}`);
            alert('✅ 리뷰 삭제 성공!');
            fetchReviews();
        } catch (error) {
            console.error('리뷰 삭제 실패:', error);
            alert('❌ 리뷰 삭제 실패');
        }
    };

    const likeReview = async (id) => {
        try {
            await axiosInstance.post(`/reviews/${id}/like`);
            alert('✅ 좋아요 성공!');
            fetchReviews();
        } catch (error) {
            console.error('좋아요 실패:', error);
            alert('❌ 좋아요 실패');
        }
    };

    const unlikeReview = async (id) => {
        try {
            await axiosInstance.delete(`/reviews/${id}/like`);
            alert('✅ 좋아요 취소 성공!');
            fetchReviews();
        } catch (error) {
            console.error('좋아요 취소 실패:', error);
            alert('❌ 좋아요 취소 실패');
        }
    };

    const getLikeCount = async (id) => {
        try {
            const response = await axiosInstance.get(`/reviews/${id}/likes/count`);
            alert(`👍 좋아요 수: ${response.data.data.likeCount}`);
        } catch (error) {
            console.error('좋아요 수 조회 실패:', error);
            alert('❌ 좋아요 수 조회 실패');
        }
    };

    const getReviewStatistics = async () => {
        if (!restaurantId) {
            alert('음식점 ID를 먼저 입력하세요.');
            return;
        }

        try {
            const response = await axiosInstance.get(`/restaurants/${restaurantId}/reviews/statistics`);
            const { averageRating, totalReviews } = response.data.data;
            alert(`📊 평균 별점: ${averageRating}, 총 리뷰 수: ${totalReviews}`);
        } catch (error) {
            console.error('리뷰 통계 조회 실패:', error);
            alert('❌ 리뷰 통계 조회 실패');
        }
    };

    return (
        <div className="bg-gray-100 min-h-screen py-10 px-4">
            <div className="max-w-3xl mx-auto bg-white p-6 rounded-xl shadow">
                <h2 className="text-2xl font-bold mb-6 text-center">📖 리뷰 조회 페이지</h2>

                <div className="flex flex-col sm:flex-row gap-3 mb-6">
                    <input
                        type="text"
                        className="flex-1 px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring focus:border-blue-400 placeholder-gray-400"
                        placeholder="음식점 ID 입력"
                        value={restaurantId}
                        onChange={(e) => setRestaurantId(e.target.value)}
                    />
                    <button className="btn" onClick={fetchReviews}>리뷰 조회하기</button>
                    <button className="btn bg-green-500 hover:bg-green-600" onClick={getReviewStatistics}>📊 리뷰 통계</button>
                </div>

                <div className="space-y-4">
                    {reviews.map((review) => (
                        <div key={review.id} className="border rounded-lg p-4 shadow-sm bg-gray-50">
                            <h4 className="text-lg font-semibold mb-1">⭐️ 별점: {review.rating}</h4>
                            <p className="text-gray-800 mb-2">{review.content}</p>
                            <p className="text-sm text-gray-500 mb-3">작성자: {review.author}</p>
                            <div className="flex flex-wrap gap-2">
                                <button className="btn bg-yellow-500 hover:bg-yellow-600" onClick={() => navigate(`/review/edit/${review.id}`)}>🖊️ 수정</button>
                                <button className="btn bg-red-500 hover:bg-red-600" onClick={() => deleteReview(review.id)}>🗑️ 삭제</button>
                                <button className="btn" onClick={() => likeReview(review.id)}>👍 좋아요</button>
                                <button className="btn" onClick={() => unlikeReview(review.id)}>👎 취소</button>
                                <button className="btn bg-gray-400 hover:bg-gray-500" onClick={() => getLikeCount(review.id)}>📊 좋아요 수</button>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}