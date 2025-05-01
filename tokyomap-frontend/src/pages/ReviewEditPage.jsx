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
        <div className="bg-gray-100 min-h-screen py-10 px-4">
            <div className="max-w-xl mx-auto bg-white p-6 rounded-xl shadow">
                <h2 className="text-2xl font-bold mb-6 text-center">🖊️ 리뷰 수정 페이지</h2>

                <div className="mb-4">
                    <label className="block text-sm font-medium text-gray-700 mb-1">음식점 ID</label>
                    <input
                        type="text"
                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring focus:border-blue-400 placeholder-gray-400"
                        placeholder="ex) 1"
                        value={restaurantId}
                        onChange={(e) => setRestaurantId(e.target.value)}
                    />
                </div>

                <div className="mb-4">
                    <label className="block text-sm font-medium text-gray-700 mb-1">별점 (1~5)</label>
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
                    <label className="block text-sm font-medium text-gray-700 mb-1">리뷰 내용</label>
                    <textarea
                        rows="5"
                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring focus:border-blue-400 resize-none"
                        placeholder="리뷰를 입력하세요"
                        value={content}
                        onChange={(e) => setContent(e.target.value)}
                    ></textarea>
                </div>

                <button
                    className="w-full bg-blue-500 hover:bg-blue-600 text-white font-semibold py-2 px-4 rounded"
                    onClick={updateReview}
                >
                    리뷰 수정 완료
                </button>
            </div>
        </div>
    );
}
