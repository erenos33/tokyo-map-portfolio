// ReviewCreatePage.jsx
import React, { useState } from 'react';
import axiosInstance from '../api/axiosInstance';
import { useNavigate } from 'react-router-dom';

export default function ReviewCreatePage() {
    const [restaurantId, setRestaurantId] = useState('');
    const [rating, setRating] = useState(5);
    const [content, setContent] = useState('');
    const navigate = useNavigate();

    const createReview = async () => {
        try {
            await axiosInstance.post(`/reviews`, {
                restaurantId: Number(restaurantId),
                rating: Number(rating),
                content,
            });
            alert('✅ 리뷰 작성 성공!');
            navigate('/');
        } catch (error) {
            console.error('리뷰 작성 실패:', error);
            alert('❌ 리뷰 작성 실패');
        }
    };

    return (
        <div style={{ padding: 30 }}>
            <h2>✍️ 리뷰 작성 페이지</h2>
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
                placeholder="리뷰 내용 작성"
                value={content}
                onChange={(e) => setContent(e.target.value)}
                rows="5"
                cols="50"
            /><br /><br />

            <button onClick={createReview}>리뷰 작성하기</button>
        </div>
    );
}
