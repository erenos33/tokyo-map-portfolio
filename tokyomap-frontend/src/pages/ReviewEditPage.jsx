// src/pages/ReviewEditPage.jsx
import React, { useState, useEffect } from 'react';
import axiosInstance from '../api/axiosInstance';
import { useNavigate, useParams } from 'react-router-dom';

export default function ReviewEditPage() {
    const [restaurantId, setRestaurantId] = useState('');
    const [rating, setRating] = useState(5);
    const [content, setContent] = useState('');
    const navigate = useNavigate();
    const { id } = useParams(); // 리뷰 ID

    useEffect(() => {
        const fetchReview = async () => {
            try {
                const response = await axiosInstance.get(`/reviews/${id}`);
                const review = response.data.data;
                setRestaurantId(review.restaurantId);
                setRating(review.rating);
                setContent(review.content);
            } catch (error) {
                console.error('리뷰 조회 실패:', error);
            }
        };
        fetchReview();
    }, [id]);

    const updateReview = async () => {
        try {
            await axiosInstance.put(`/reviews/${id}`, {
                restaurantId: Number(restaurantId),
                rating: Number(rating),
                content,
            });
            alert('✅ 리뷰 수정 성공!');
            navigate('/');
        } catch (error) {
            console.error('리뷰 수정 실패:', error);
            alert('❌ 리뷰 수정 실패');
        }
    };

    return (
        <div style={{ padding: 30 }}>
            <h2>🖊️ 리뷰 수정 페이지</h2>
            <input
                type="text"
                placeholder="음식점 ID"
                value={restaurantId}
                onChange={(e) => setRestaurantId(e.target.value)}
            /><br /><br />

            <input
                type="number"
                placeholder="별점 (1~5)"
                min="1"
                max="5"
                value={rating}
                onChange={(e) => setRating(e.target.value)}
            /><br /><br />

            <textarea
                placeholder="리뷰 수정 내용"
                value={content}
                onChange={(e) => setContent(e.target.value)}
                rows="5"
                cols="50"
            /><br /><br />

            <button onClick={updateReview}>리뷰 수정 완료</button>
        </div>
    );
}
