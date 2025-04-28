import React, { useState } from 'react';
import axiosInstance from '../api/axiosInstance';

export default function ReviewCommentViewPage() {
    const [reviewId, setReviewId] = useState('');
    const [comments, setComments] = useState([]);
    const [editCommentId, setEditCommentId] = useState(null);
    const [editContent, setEditContent] = useState('');

    const fetchComments = async () => {
        if (!reviewId) {
            alert('리뷰 ID를 입력하세요.');
            return;
        }

        try {
            const res = await axiosInstance.get(`/reviews/${reviewId}/comments`, {
                params: { page: 0, size: 10, sort: 'createdAt,desc' },
            });
            setComments(res.data.data.content);
        } catch (error) {
            console.error('댓글 조회 실패:', error);
            alert('❌ 댓글 조회 실패');
        }
    };

    const updateComment = async () => {
        if (!editContent) {
            alert('수정할 내용을 입력하세요.');
            return;
        }
        try {
            await axiosInstance.put(`/reviews/${reviewId}/comments/${editCommentId}`, {
                content: editContent
            });
            alert('✅ 댓글 수정 성공!');
            setEditCommentId(null);
            setEditContent('');
            fetchComments();
        } catch (error) {
            console.error('댓글 수정 실패:', error);
            alert('❌ 댓글 수정 실패');
        }
    };

    const deleteComment = async (commentId) => {
        if (!window.confirm('정말 이 댓글을 삭제할까요?')) return;
        try {
            await axiosInstance.delete(`/reviews/${reviewId}/comments/${commentId}`);
            alert('✅ 댓글 삭제 성공!');
            fetchComments();
        } catch (error) {
            console.error('댓글 삭제 실패:', error);
            alert('❌ 댓글 삭제 실패');
        }
    };

    return (
        <div style={{ padding: 30 }}>
            <h2>📖 리뷰 댓글 조회 페이지</h2>

            <input
                type="text"
                placeholder="댓글을 볼 리뷰 ID 입력"
                value={reviewId}
                onChange={(e) => setReviewId(e.target.value)}
            />
            <br /><br />
            <button onClick={fetchComments}>댓글 조회하기</button>

            <div style={{ marginTop: 20 }}>
                {comments.map((comment) => (
                    <div key={comment.id} style={{ border: '1px solid #ccc', marginTop: '10px', padding: '10px' }}>
                        <p>✍️ {comment.content}</p>
                        <small>작성자: {comment.nickname}</small><br />
                        <small>작성 시간: {new Date(comment.createdAt).toLocaleString()}</small><br /><br />
                        {editCommentId === comment.id ? (
                            <>
                                <textarea
                                    value={editContent}
                                    onChange={(e) => setEditContent(e.target.value)}
                                    rows="3"
                                    cols="50"
                                />
                                <br />
                                <button onClick={updateComment}>✅ 수정 완료</button>
                                <button onClick={() => setEditCommentId(null)}>❌ 수정 취소</button>
                            </>
                        ) : (
                            <>
                                {comment.isAuthor && ( // ✅ 내 댓글일 때만 수정/삭제
                                    <>
                                        <button onClick={() => {
                                            setEditCommentId(comment.id);
                                            setEditContent(comment.content);
                                        }}>🖊️ 수정</button>
                                        <button onClick={() => deleteComment(comment.id)}>🗑️ 삭제</button>
                                    </>
                                )}
                            </>
                        )}
                    </div>
                ))}
            </div>
        </div>
    );
}
