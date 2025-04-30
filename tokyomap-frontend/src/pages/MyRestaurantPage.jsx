// src/pages/MyRestaurantPage.jsx
import React, { useEffect, useState } from 'react';
import axiosInstance from '../api/axiosInstance';

export default function MyRestaurantPage() {
    const [myRestaurants, setMyRestaurants] = useState([]);
    const [reviews, setReviews] = useState({});
    const [newReview, setNewReview] = useState({});

    const fetchMyRestaurants = async () => {
        try {
            const response = await axiosInstance.get('/restaurants/my');
            setMyRestaurants(response.data.data.content);
        } catch (error) {
            console.error('내가 등록한 맛집 조회 실패', error);
        }
    };

    const fetchReviews = async (restaurantId) => {
        try {
            const response = await axiosInstance.get(`/restaurants/${restaurantId}/reviews`);
            setReviews((prev) => ({ ...prev, [restaurantId]: response.data.data.content ?? [] }));
        } catch (error) {
            console.error('리뷰 조회 실패', error);
        }
    };

    const deleteRestaurant = async (id) => {
        if (!window.confirm('정말 삭제하시겠습니까?')) return;
        try {
            await axiosInstance.delete(`/restaurants/${id}`);
            alert('삭제되었습니다.');
            fetchMyRestaurants();
        } catch (error) {
            console.error('삭제 실패', error);
            alert('삭제에 실패했습니다.');
        }
    };

    const handleReviewChange = (restaurantId, field, value) => {
        setNewReview((prev) => ({
            ...prev,
            [restaurantId]: {
                ...prev[restaurantId],
                [field]: value,
            },
        }));
    };

    const submitReview = async (restaurantId) => {
        const review = newReview[restaurantId];
        if (!review || !review.rating || !review.content) {
            alert('별점과 내용을 입력해주세요.');
            return;
        }
        try {
            await axiosInstance.post('/reviews', {
                restaurantId,
                rating: parseFloat(review.rating),
                content: review.content,
            });
            alert('리뷰가 등록되었습니다.');
            fetchReviews(restaurantId);
        } catch (error) {
            console.error('리뷰 등록 실패', error);
            alert('리뷰 등록에 실패했습니다.');
        }
    };

    const deleteReview = async (reviewId, restaurantId) => {
        if (!window.confirm('정말 리뷰를 삭제하시겠습니까?')) return;
        try {
            await axiosInstance.delete(`/reviews/${reviewId}`);
            fetchReviews(restaurantId);
        } catch (error) {
            console.error('리뷰 삭제 실패', error);
            alert('리뷰 삭제에 실패했습니다.');
        }
    };

    useEffect(() => {
        fetchMyRestaurants();
    }, []);

    return (
        <div style={{ padding: 30 }}>
            <h2>📋 내가 등록한 맛집 목록</h2>
            {myRestaurants.length === 0 ? (
                <p>등록한 음식점이 없습니다.</p>
            ) : (
                myRestaurants.map((r) => (
                    <div key={r.id} style={{ marginBottom: 30 }}>
                        <p>🍴 {r.name}</p>
                        <p>📍 {r.address}</p>
                        <p>⭐ 평점: {r.rating}</p>
                        <button onClick={() => deleteRestaurant(r.id)}>맛집 삭제하기</button>

                        <div style={{ marginTop: 10 }}>
                            <h4>✍️ 리뷰 작성</h4>
                            <input
                                type="number"
                                placeholder="별점 (1~5)"
                                value={newReview[r.id]?.rating || ''}
                                onChange={(e) => handleReviewChange(r.id, 'rating', e.target.value)}
                            />
                            <br />
                            <textarea
                                placeholder="리뷰 내용"
                                value={newReview[r.id]?.content || ''}
                                onChange={(e) => handleReviewChange(r.id, 'content', e.target.value)}
                            />
                            <br />
                            <button onClick={() => submitReview(r.id)}>리뷰 등록</button>
                        </div>

                        <div style={{ marginTop: 10 }}>
                            <h4>💬 리뷰 목록</h4>
                            <button onClick={() => fetchReviews(r.id)}>리뷰 불러오기</button>
                            {Array.isArray(reviews[r.id]) && reviews[r.id].map((review) => (
                                <div key={review.id} style={{ marginTop: 10 }}>
                                    <p>⭐ {review.rating}</p>
                                    <p>{review.content}</p>
                                    <button onClick={() => deleteReview(review.id, r.id)}>리뷰 삭제</button>
                                </div>
                            ))}
                        </div>
                        <hr />
                    </div>
                ))
            )}
        </div>
    );
}