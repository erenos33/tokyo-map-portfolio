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
        <div style={{ padding: 30 }}>
            <h2>📖 리뷰 조회 페이지</h2>
            <input
                type="text"
                placeholder="음식점 ID 입력"
                value={restaurantId}
                onChange={(e) => setRestaurantId(e.target.value)}
            />
            <button onClick={fetchReviews}>리뷰 조회하기</button>
            <button onClick={getReviewStatistics} style={{ marginLeft: 10 }}>📊 리뷰 통계 조회하기</button> {/* ✅ 통계 조회 버튼 추가 */}

            <div style={{ marginTop: 20 }}>
                {reviews.map((review) => (
                    <div key={review.id} style={{ border: '1px solid #ccc', marginTop: '10px', padding: '10px' }}>
                        <h4>⭐️ 별점: {review.rating}</h4>
                        <p>{review.content}</p>
                        <small>작성자: {review.author}</small>
                        <br /><br />
                        <button onClick={() => navigate(`/review/edit/${review.id}`)}>🖊️ 수정하기</button>
                        <button onClick={() => deleteReview(review.id)}>🗑️ 삭제하기</button>
                        <button onClick={() => likeReview(review.id)}>👍 좋아요</button>
                        <button onClick={() => unlikeReview(review.id)}>👎 좋아요 취소</button>
                        <button onClick={() => getLikeCount(review.id)}>📊 좋아요 수 조회</button>
                    </div>
                ))}
            </div>
        </div>
    );
}
