import React, { useEffect, useState } from 'react';
import axiosInstance from '../api/axiosInstance';

export default function MyRestaurantPage() {
    const [myRestaurants, setMyRestaurants] = useState([]);
    const [reviews, setReviews] = useState({});
    const [newReview, setNewReview] = useState({});
    const [comments, setComments] = useState({});
    const [newComment, setNewComment] = useState({});
    const [editingCommentId, setEditingCommentId] = useState(null);
    const [editingContent, setEditingContent] = useState('');

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

    const likeReview = async (reviewId) => {
        try {
            await axiosInstance.post(`/reviews/${reviewId}/like`);
            alert('👍 좋아요 성공!');
        } catch (error) {
            console.error('좋아요 실패:', error);
            alert('❌ 좋아요 실패');
        }
    };

    const unlikeReview = async (reviewId) => {
        try {
            await axiosInstance.delete(`/reviews/${reviewId}/like`);
            alert('👎 좋아요 취소 성공!');
        } catch (error) {
            console.error('좋아요 취소 실패:', error);
            alert('❌ 좋아요 취소 실패');
        }
    };

    const getLikeCount = async (reviewId) => {
        try {
            const res = await axiosInstance.get(`/reviews/${reviewId}/likes/count`);
            alert(`👍 좋아요 수: ${res.data.data.likeCount}`);
        } catch (error) {
            console.error('좋아요 수 조회 실패:', error);
            alert('❌ 좋아요 수 조회 실패');
        }
    };

    const fetchComments = async (reviewId) => {
        try {
            const res = await axiosInstance.get(`/reviews/${reviewId}/comments`, {
                params: { page: 0, size: 10, sort: 'createdAt,desc' }
            });
            setComments((prev) => ({ ...prev, [reviewId]: res.data.data.content }));
        } catch (error) {
            console.error('댓글 조회 실패:', error);
            alert('❌ 댓글 조회 실패');
        }
    };

    const submitComment = async (reviewId) => {
        const content = newComment[reviewId];
        if (!content) {
            alert('댓글 내용을 입력하세요.');
            return;
        }
        try {
            await axiosInstance.post(`/reviews/${reviewId}/comments`, { content });
            setNewComment((prev) => ({ ...prev, [reviewId]: '' }));
            fetchComments(reviewId);
        } catch (error) {
            console.error('댓글 작성 실패:', error);
            alert('❌ 댓글 작성 실패');
        }
    };

    const deleteComment = async (reviewId, commentId) => {
        if (!window.confirm('댓글을 삭제하시겠습니까?')) return;
        try {
            await axiosInstance.delete(`/reviews/${reviewId}/comments/${commentId}`);
            fetchComments(reviewId);
        } catch (error) {
            console.error('댓글 삭제 실패:', error);
            alert('❌ 댓글 삭제 실패');
        }
    };

    const updateComment = async (reviewId, commentId) => {
        if (!editingContent.trim()) {
            alert('수정할 내용을 입력하세요.');
            return;
        }
        try {
            await axiosInstance.put(`/reviews/${reviewId}/comments/${commentId}`, { content: editingContent });
            setEditingCommentId(null);
            setEditingContent('');
            fetchComments(reviewId);
        } catch (error) {
            console.error('댓글 수정 실패:', error);
            alert('❌ 댓글 수정 실패');
        }
    };

    useEffect(() => {
        fetchMyRestaurants();
    }, []);

    return (
        <div className="bg-gray-100 min-h-screen py-10 px-4">
            <div className="max-w-4xl mx-auto">
                <h2 className="text-2xl font-bold mb-6">📋 내가 등록한 맛집 목록</h2>
                {myRestaurants.map((r) => (
                    <div key={r.id} className="bg-white rounded-xl shadow p-6 mb-8">
                        <p className="text-lg font-semibold mb-1">🍴 {r.name}</p>
                        <p className="text-sm text-gray-600 mb-1">📍 {r.address}</p>
                        <p className="text-sm text-gray-600 mb-3">⭐ 평점: {r.rating}</p>
                        <button className="bg-red-500 hover:bg-red-600 text-white text-sm px-3 py-1 rounded mb-4" onClick={() => deleteRestaurant(r.id)}>
                            맛집 삭제하기
                        </button>

                        <div className="border-t pt-4 mt-4">
                            <h4 className="text-md font-semibold mb-2">✍️ 리뷰 작성</h4>
                            <div className="flex flex-col sm:flex-row gap-2 mb-3">
                                <input
                                    type="number"
                                    placeholder="별점 (1~5)"
                                    className="border rounded px-2 py-1 w-24"
                                    value={newReview[r.id]?.rating || ''}
                                    onChange={(e) => handleReviewChange(r.id, 'rating', e.target.value)}
                                />
                                <input
                                    type="text"
                                    placeholder="리뷰 내용"
                                    className="border rounded px-3 py-1 flex-1 min-w-[200px]"
                                    value={newReview[r.id]?.content || ''}
                                    onChange={(e) => handleReviewChange(r.id, 'content', e.target.value)}
                                />
                                <button className="bg-blue-500 hover:bg-blue-600 text-white px-3 py-1 rounded" onClick={() => submitReview(r.id)}>
                                    등록
                                </button>
                            </div>
                        </div>

                        <div className="border-t pt-4 mt-4">
                            <h4 className="text-md font-semibold mb-2">💬 리뷰 목록</h4>
                            <button className="mb-3 text-sm text-blue-600 underline" onClick={() => fetchReviews(r.id)}>
                                리뷰 불러오기
                            </button>
                            {Array.isArray(reviews[r.id]) && reviews[r.id].map((review) => (
                                <div key={review.id} className="bg-gray-100 rounded-lg p-4 mb-4 text-sm">
                                    <p className="font-semibold mb-1">⭐ {review.rating}</p>
                                    <p className="text-gray-800 mb-2">{review.content}</p>
                                    <button className="text-red-500 text-xs mb-2" onClick={() => deleteReview(review.id, r.id)}>
                                        리뷰 삭제
                                    </button>
                                    <div className="flex flex-wrap gap-2 mb-3">
                                        <button className="btn bg-yellow-500 hover:bg-yellow-600 text-xs" onClick={() => likeReview(review.id)}>👍 좋아요</button>
                                        <button className="btn bg-gray-400 hover:bg-gray-500 text-xs" onClick={() => unlikeReview(review.id)}>👎 취소</button>
                                        <button className="btn bg-blue-500 hover:bg-blue-600 text-xs" onClick={() => getLikeCount(review.id)}>📊 좋아요 수</button>
                                    </div>

                                    <div className="bg-white border mt-2 p-3 rounded">
                                        <h5 className="font-semibold mb-1">💬 댓글</h5>
                                        <div className="flex gap-2 mb-2">
                                            <input
                                                type="text"
                                                placeholder="댓글 내용 입력"
                                                className="flex-1 border px-2 py-1 rounded"
                                                value={newComment[review.id] || ''}
                                                onChange={(e) => setNewComment((prev) => ({ ...prev, [review.id]: e.target.value }))}
                                            />
                                            <button className="btn px-3 py-1" onClick={() => submitComment(review.id)}>등록</button>
                                            <button className="text-sm text-blue-600 underline" onClick={() => fetchComments(review.id)}>조회</button>
                                        </div>
                                        {comments[review.id]?.map((comment) => (
                                            <div key={comment.id} className="bg-gray-50 border rounded px-3 py-2 mb-2">
                                                <p className="text-sm">✍️ {comment.content}</p>
                                                <p className="text-xs text-gray-500">작성자: {comment.nickname}</p>
                                                <p className="text-xs text-gray-400">작성 시간: {new Date(comment.createdAt).toLocaleString()}</p>

                                                {comment.isAuthor && editingCommentId !== comment.id && (
                                                    <div className="flex gap-2 mt-1">
                                                        <button className="btn bg-yellow-500 hover:bg-yellow-600 text-xs" onClick={() => {
                                                            setEditingCommentId(comment.id);
                                                            setEditingContent(comment.content);
                                                        }}>🖊️ 수정</button>
                                                        <button className="btn bg-red-500 hover:bg-red-600 text-xs" onClick={() => deleteComment(review.id, comment.id)}>🗑️ 삭제</button>
                                                    </div>
                                                )}

                                                {editingCommentId === comment.id && (
                                                    <div className="mt-2">
                                                        <textarea
                                                            rows="2"
                                                            className="w-full px-2 py-1 border rounded resize-none text-xs"
                                                            value={editingContent}
                                                            onChange={(e) => setEditingContent(e.target.value)}
                                                        />
                                                        <div className="flex gap-2 mt-1">
                                                            <button className="btn bg-green-500 hover:bg-green-600 text-xs" onClick={() => updateComment(review.id, comment.id)}>✅ 수정 완료</button>
                                                            <button className="btn bg-gray-400 hover:bg-gray-500 text-xs" onClick={() => setEditingCommentId(null)}>❌ 취소</button>
                                                        </div>
                                                    </div>
                                                )}
                                            </div>
                                        ))}
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}
