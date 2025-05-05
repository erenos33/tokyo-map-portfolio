import React, { useEffect, useState } from 'react';
import axiosInstance from '../api/axiosInstance';

export default function MyRestaurantPage() {
    const [myRestaurants, setMyRestaurants] = useState([]);
    const [expandedHours, setExpandedHours] = useState({});
    const [reviewStates, setReviewStates] = useState({});
    // ★ 댓글 상태 추가
    const [commentStates, setCommentStates] = useState({});

    // 내가 등록한 맛집 조회
    const fetchMyRestaurants = async () => {
        try {
            const res = await axiosInstance.get('/restaurants/my');
            setMyRestaurants(res.data.data.content);
        } catch (e) {
            console.error('내가 등록한 맛집 조회 실패', e);
        }
    };

    useEffect(() => {
        fetchMyRestaurants();
    }, []);

    // 음식점 삭제
    const deleteRestaurant = async (id) => {
        if (!window.confirm('정말 이 음식점을 삭제하시겠습니까?')) return;
        try {
            await axiosInstance.delete(`/restaurants/${id}`);
            fetchMyRestaurants();
        } catch (e) {
            console.error('음식점 삭제 실패', e);
            alert('음식점 삭제 실패');
        }
    };

    // 영업시간 요약 함수
    const summarizeHours = (hoursText) => {
        if (!hoursText) return '';
        const parts = hoursText.split(/,\s*|\n/).map(s => s.trim());
        const idx = (new Date().getDay() + 6) % 7;
        const line = parts[idx] || '';
        const m = line.match(/(\d{1,2}:\d{2})\s*([AP]M)\s*[–-]\s*(\d{1,2}:\d{2})\s*([AP]M)/);
        if (!m) return '';
        const [, , , end, period] = m;
        return `${period === 'AM' ? '오전' : '오후'} ${end}에 영업 종료`;
    };

    // 현재 영업중 여부 판단
    const isOpenNow = (hoursText) => {
        if (!hoursText) return false;
        const parts = hoursText.split(/,\s*|\n/).map(s => s.trim());
        const idx = (new Date().getDay() + 6) % 7;
        const line = parts[idx] || '';
        const m = line.match(/(\d{1,2}:\d{2})\s*([AP]M)\s*[–-]\s*(\d{1,2}:\d{2})\s*([AP]M)/);
        if (!m) return false;
        const [, start, sp, end, ep] = m;
        const toMin = (t, p) => {
            let [h, m] = t.split(':').map(Number);
            if (p === 'PM' && h !== 12) h += 12;
            if (p === 'AM' && h === 12) h = 0;
            return h * 60 + m;
        };
        const nowMin = new Date().getHours() * 60 + new Date().getMinutes();
        return nowMin >= toMin(start, sp) && nowMin <= toMin(end, ep);
    };
    // 리뷰 상태 업데이트
    const handleReviewChange = (rid, field, value) => {
        setReviewStates(prev => ({
            ...prev,
            [rid]: { ...(prev[rid] || {}), [field]: value }
        }));
    };

    // 리뷰 조회
    const fetchReviews = async (restaurantId) => {
        try {
            const res = await axiosInstance.get(
                `/restaurants/${restaurantId}/reviews`,
                { params: { page: 0, size: 10, sort: 'createdAt,desc' } }
            );
            setReviewStates(prev => ({
                ...prev,
                [restaurantId]: { ...(prev[restaurantId] || {}), reviews: res.data.data.content }
            }));
        } catch (e) {
            console.error('리뷰 조회 실패', e);
        }
    };

    // 리뷰 작성
    const submitReview = async (restaurantId) => {
        const { rating = 5, content = '' } = reviewStates[restaurantId] || {};
        try {
            await axiosInstance.post('/reviews', { restaurantId, rating: Number(rating), content });
            fetchReviews(restaurantId);
            setReviewStates(prev => ({ ...prev, [restaurantId]: {} }));
        } catch (e) {
            console.error('리뷰 작성 실패', e);
            alert('리뷰 작성 실패');
        }
    };

    // 리뷰 수정 시작
    const startEditReview = (restaurantId, review) => {
        handleReviewChange(restaurantId, 'editId', review.id);
        handleReviewChange(restaurantId, 'editRating', review.rating);
        handleReviewChange(restaurantId, 'editContent', review.content);
    };

    // 리뷰 수정 완료
    const updateReview = async (restaurantId) => {
        const { editId, editRating, editContent } = reviewStates[restaurantId] || {};
        try {
            await axiosInstance.put(`/reviews/${editId}`, { restaurantId, rating: editRating, content: editContent });
            fetchReviews(restaurantId);
            setReviewStates(prev => ({
                ...prev,
                [restaurantId]: { ...(prev[restaurantId] || {}), editId: null }
            }));
        } catch (e) {
            console.error('리뷰 수정 실패', e);
            alert('리뷰 수정 실패');
        }
    };

    // 리뷰 삭제
    const deleteReview = async (restaurantId, reviewId) => {
        if (!window.confirm('리뷰를 삭제하시겠습니까?')) return;
        try {
            await axiosInstance.delete(`/reviews/${reviewId}`);
            fetchReviews(restaurantId);
        } catch (e) {
            console.error('리뷰 삭제 실패', e);
            alert('리뷰 삭제 실패');
        }
    };

    // 좋아요 / 좋아요 취소 / 좋아요 수 조회
    const likeReview = async (id, restaurantId) => {
        try { await axiosInstance.post(`/reviews/${id}/like`); fetchReviews(restaurantId); } catch {}
    };
    const unlikeReview = async (id, restaurantId) => {
        try { await axiosInstance.delete(`/reviews/${id}/like`); fetchReviews(restaurantId); } catch {}
    };
    const getLikeCount = async (id) => {
        try {
            const res = await axiosInstance.get(`/reviews/${id}/likes/count`);
            alert(`좋아요 수: ${res.data.data.likeCount}`);
        } catch {}
    };
    // 댓글 상태 변경
    const handleCommentChange = (reviewId, field, value) => {
        setCommentStates(prev => ({
            ...prev,
            [reviewId]: { ...(prev[reviewId] || {}), [field]: value }
        }));
    };

    // 댓글 조회
    const fetchComments = async (reviewId) => {
        try {
            const res = await axiosInstance.get(
                `/reviews/${reviewId}/comments`,
                { params: { page: 0, size: 10, sort: 'createdAt,desc' } }
            );
            setCommentStates(prev => ({
                ...prev,
                [reviewId]: { ...(prev[reviewId] || {}), comments: res.data.data.content }
            }));
        } catch (e) {
            console.error('댓글 조회 실패', e);
        }
    };

    // 댓글 작성
    const submitComment = async (reviewId) => {
        const content = commentStates[reviewId]?.newContent || '';
        if (!content) return alert('댓글 내용을 입력하세요');
        try {
            await axiosInstance.post(`/reviews/${reviewId}/comments`, { content });
            handleCommentChange(reviewId, 'newContent', '');
            fetchComments(reviewId);
        } catch (e) {
            console.error('댓글 작성 실패', e);
            alert('댓글 작성 실패');
        }
    };

    // 댓글 수정 시작
    const startEditComment = (reviewId, comment) => {
        handleCommentChange(reviewId, 'editCommentId', comment.id);
        handleCommentChange(reviewId, 'editContent', comment.content);
    };

    // 댓글 수정 완료
    const updateComment = async (reviewId) => {
        const { editCommentId, editContent } = commentStates[reviewId] || {};
        if (!editContent) return alert('수정할 내용을 입력하세요');
        try {
            await axiosInstance.put(
                `/reviews/${reviewId}/comments/${editCommentId}`,
                { content: editContent }
            );
            setCommentStates(prev => ({
                ...prev,
                [reviewId]: { ...(prev[reviewId] || {}), editCommentId: null }
            }));
            fetchComments(reviewId);
        } catch (e) {
            console.error('댓글 수정 실패', e);
            alert('댓글 수정 실패');
        }
    };

    // 댓글 삭제
    const deleteComment = async (reviewId, commentId) => {
        if (!window.confirm('댓글을 삭제하시겠습니까?')) return;
        try {
            await axiosInstance.delete(`/reviews/${reviewId}/comments/${commentId}`);
            fetchComments(reviewId);
        } catch (e) {
            console.error('댓글 삭제 실패', e);
            alert('댓글 삭제 실패');
        }
    };
    return (
        <div className="bg-gray-100 min-h-screen py-10 px-4">
            <div className="max-w-5xl mx-auto">
                <h2 className="text-3xl font-bold text-center mb-8">🍽 내가 등록한 맛집</h2>

                {myRestaurants.map(r => {
                    const open = isOpenNow(r.openingHours);
                    const lines = r.openingHours ? r.openingHours.split(/,\s*|\n/) : [];
                    const state = reviewStates[r.id] || {};
                    const reviews = state.reviews || [];
                    const editId = state.editId;

                    return (
                        <div key={r.id} className="bg-white rounded-2xl shadow-lg p-6 mb-10">
                            {/* 음식점 상세 정보 (원본 그대로) */}
                            <div className="border-b pb-4 mb-4">
                                <h3 className="text-2xl font-semibold text-blue-600 mb-1">{r.name}</h3>
                                <p className="text-gray-600 text-sm">📍 {r.address}</p>
                            </div>
                            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-sm mb-4">
                                <p>⭐ 평점: <span className="font-semibold">{r.rating ?? '정보 없음'}</span></p>
                                <p>💰 가격대: {r.priceRange?.trim() || '정보 없음'}</p>
                                <p>☎ {r.phoneNumber?.trim() || '정보 없음'}</p>
                                <div>
                                    ⏰ 영업시간:{' '}
                                    {r.openingHours ? (
                                        <div
                                            onClick={() => setExpandedHours(prev => ({ ...prev, [r.id]: !prev[r.id] }))}
                                            className="inline-flex items-center space-x-1 cursor-pointer hover:underline"
                                        >
                      <span className={open ? 'text-green-600' : 'text-red-500'}>
                        {open ? '영업 중' : '영업 전'}
                      </span>
                                            <span className="text-blue-600">· {summarizeHours(r.openingHours)}</span>
                                            <span className="text-blue-600">{expandedHours[r.id] ? '▲' : '▼'}</span>
                                        </div>
                                    ) : '정보 없음'}
                                    {expandedHours[r.id] && lines.length > 0 && (
                                        <ul className="mt-1 list-disc list-inside text-xs text-gray-600">
                                            {lines.map((line, i) => <li key={i}>{line}</li>)}
                                        </ul>
                                    )}
                                </div>
                            </div>
                            <div className="flex justify-end mb-4">
                                <button
                                    className="bg-red-500 hover:bg-red-600 text-white text-sm px-4 py-2 rounded-lg"
                                    onClick={() => deleteRestaurant(r.id)}
                                >🗑️ 삭제하기</button>
                            </div>

                            {/* 리뷰 작성/조회/수정/삭제/좋아요 (원본 그대로) */}
                            <div className="border-t pt-4">
                                <h4 className="text-sm font-semibold mb-2">✍ 리뷰 작성</h4>
                                <input
                                    type="number" min="1" max="5"
                                    className="w-20 px-2 py-1 border rounded text-sm mr-2"
                                    placeholder="별점"
                                    value={state.rating || ''}
                                    onChange={e => handleReviewChange(r.id, 'rating', e.target.value)}
                                />
                                <textarea
                                    rows="2"
                                    className="w-full px-3 py-1 border rounded text-sm mt-2"
                                    placeholder="리뷰 내용"
                                    value={state.content || ''}
                                    onChange={e => handleReviewChange(r.id, 'content', e.target.value)}
                                />
                                <div className="text-right mt-2">
                                    <button
                                        className="bg-blue-500 hover:bg-blue-600 text-white px-4 py-1 rounded text-sm"
                                        onClick={() => submitReview(r.id)}
                                    >등록</button>
                                </div>
                                <button
                                    className="text-sm text-blue-600 hover:underline mt-4"
                                    onClick={() => fetchReviews(r.id)}
                                >📄 리뷰 보기</button>

                                {/* 리뷰 목록 + 댓글 UI */}
                                {reviews.length > 0 && (
                                    <div className="mt-4 space-y-3">
                                        {reviews.map(rev => {
                                            const cState = commentStates[rev.id] || {};
                                            return (
                                                <div key={rev.id} className="border rounded-lg p-4 shadow-sm bg-gray-50">
                                                    <h5 className="font-semibold mb-1">👤 {rev.nickname}</h5>
                                                    <p className="text-xs text-gray-400 mb-1">
                                                        작성일: {rev.createdAt?.slice(0,10)}
                                                    </p>

                                                    {editId === rev.id ? (
                                                        <>
                                                            <input
                                                                type="number" min="1" max="5"
                                                                className="w-20 px-2 py-1 border rounded text-sm mr-2"
                                                                value={state.editRating || rev.rating}
                                                                onChange={e => handleReviewChange(r.id, 'editRating', e.target.value)}
                                                            />
                                                            <textarea
                                                                rows="2"
                                                                className="w-full px-3 py-1 border rounded text-sm mt-2"
                                                                value={state.editContent || rev.content}
                                                                onChange={e => handleReviewChange(r.id, 'editContent', e.target.value)}
                                                            />
                                                            <div className="mt-2 space-x-2">
                                                                <button
                                                                    className="bg-green-500 hover:bg-green-600 text-white px-3 py-1 rounded text-sm"
                                                                    onClick={() => updateReview(r.id)}
                                                                >저장</button>
                                                                <button
                                                                    className="bg-gray-300 hover:bg-gray-400 text-gray-700 px-3 py-1 rounded text-sm"
                                                                    onClick={() => handleReviewChange(r.id, 'editId', null)}
                                                                >취소</button>
                                                            </div>
                                                        </>
                                                    ) : (
                                                        <>
                                                            <p className="text-gray-800 mb-2">{rev.content}</p>
                                                            <div className="flex flex-wrap gap-2 mb-2">
                                                                <button
                                                                    className="px-2 py-1 bg-yellow-500 hover:bg-yellow-600 text-white text-sm rounded"
                                                                    onClick={() => startEditReview(r.id, rev)}
                                                                >🖊️ 수정</button>
                                                                <button
                                                                    className="px-2 py-1 bg-red-500 hover:bg-red-600 text-white text-sm rounded"
                                                                    onClick={() => deleteReview(r.id, rev.id)}
                                                                >🗑️ 삭제</button>
                                                                {/* ★ 댓글 보기 버튼 ★ */}
                                                                <button
                                                                    className="px-2 py-1 border rounded text-sm"
                                                                    onClick={() => fetchComments(rev.id)}
                                                                >💬 댓글 보기</button>
                                                                <button
                                                                    className="px-2 py-1 border rounded text-sm"
                                                                    onClick={() => likeReview(rev.id, r.id)}
                                                                >👍 좋아요</button>
                                                                <button
                                                                    className="px-2 py-1 border rounded text-sm"
                                                                    onClick={() => unlikeReview(rev.id, r.id)}
                                                                >👎 취소</button>
                                                                <button
                                                                    className="px-2 py-1 bg-gray-400 hover:bg-gray-500 text-white text-sm rounded"
                                                                    onClick={() => getLikeCount(rev.id)}
                                                                >📊 좋아요 수</button>
                                                            </div>

                                                            {/* ★ 댓글 작성 폼 ★ */}
                                                            <textarea
                                                                rows="2"
                                                                className="w-full px-3 py-1 border rounded text-sm mb-2"
                                                                placeholder="댓글 작성"
                                                                value={cState.newContent || ''}
                                                                onChange={e => handleCommentChange(rev.id, 'newContent', e.target.value)}
                                                            />
                                                            <button
                                                                className="bg-blue-500 hover:bg-blue-600 text-white px-3 py-1 rounded text-sm mb-3"
                                                                onClick={() => submitComment(rev.id)}
                                                            >댓글 등록</button>

                                                            {/* ★ 댓글 목록 ★ */}
                                                            {cState.comments && cState.comments.length > 0 && (
                                                                <div className="space-y-2">
                                                                    {cState.comments.map(com => (
                                                                        <div key={com.id} className="bg-white p-2 rounded border">
                                                                            <p className="mb-1">{com.content}</p>
                                                                            <p className="text-xs text-gray-500 mb-1">
                                                                                {com.nickname} · {com.createdAt?.slice(0,10)}
                                                                            </p>
                                                                            {cState.editCommentId === com.id ? (
                                                                                <>
                                          <textarea
                                              rows="2"
                                              className="w-full px-2 py-1 border rounded text-sm"
                                              value={cState.editContent || com.content}
                                              onChange={e => handleCommentChange(rev.id, 'editContent', e.target.value)}
                                          />
                                                                                    <button
                                                                                        className="text-sm text-green-600"
                                                                                        onClick={() => updateComment(rev.id)}
                                                                                    >저장</button>
                                                                                    <button
                                                                                        className="text-sm text-gray-600"
                                                                                        onClick={() => handleCommentChange(rev.id, 'editCommentId', null)}
                                                                                    >취소</button>
                                                                                </>
                                                                            ) : (
                                                                                <>
                                                                                    <button
                                                                                        className="text-sm text-yellow-600"
                                                                                        onClick={() => startEditComment(rev.id, com)}
                                                                                    >수정</button>
                                                                                    <button
                                                                                        className="text-sm text-red-600"
                                                                                        onClick={() => deleteComment(rev.id, com.id)}
                                                                                    >삭제</button>
                                                                                </>
                                                                            )}
                                                                        </div>
                                                                    ))}
                                                                </div>
                                                            )}
                                                        </>
                                                    )}
                                                </div>
                                            );
                                        })}
                                    </div>
                                )}
                            </div>
                        </div>
                    );
                })}
            </div>
        </div>
    );
}
