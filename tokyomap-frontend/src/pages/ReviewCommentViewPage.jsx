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
        <div className="bg-gray-100 min-h-screen py-10 px-4">
            <div className="max-w-3xl mx-auto bg-white p-6 rounded-xl shadow">
                <h2 className="text-2xl font-bold mb-6 text-center">📖 리뷰 댓글 조회</h2>

                <div className="flex flex-col sm:flex-row gap-3 mb-6">
                    <input
                        type="text"
                        placeholder="댓글을 볼 리뷰 ID 입력"
                        value={reviewId}
                        onChange={(e) => setReviewId(e.target.value)}
                        className="flex-1 px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring focus:border-blue-400"
                    />
                    <button className="btn" onClick={fetchComments}>📖 댓글 조회하기</button>
                </div>

                <div className="space-y-4">
                    {comments.map((comment) => (
                        <div key={comment.id} className="bg-gray-50 border rounded-lg p-4 shadow-sm">
                            <p className="mb-1">✍️ {comment.content}</p>
                            <p className="text-sm text-gray-500">작성자: {comment.nickname}</p>
                            <p className="text-sm text-gray-400 mb-2">작성 시간: {new Date(comment.createdAt).toLocaleString()}</p>

                            {editCommentId === comment.id ? (
                                <>
                                    <textarea
                                        value={editContent}
                                        onChange={(e) => setEditContent(e.target.value)}
                                        rows="3"
                                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring focus:border-blue-400 resize-none mb-2"
                                    />
                                    <div className="flex gap-2">
                                        <button className="btn bg-green-500 hover:bg-green-600" onClick={updateComment}>✅ 수정 완료</button>
                                        <button className="btn bg-gray-400 hover:bg-gray-500" onClick={() => setEditCommentId(null)}>❌ 수정 취소</button>
                                    </div>
                                </>
                            ) : (
                                comment.isAuthor && (
                                    <div className="flex gap-2 mt-2">
                                        <button className="btn bg-yellow-500 hover:bg-yellow-600" onClick={() => {
                                            setEditCommentId(comment.id);
                                            setEditContent(comment.content);
                                        }}>🖊️ 수정</button>
                                        <button className="btn bg-red-500 hover:bg-red-600" onClick={() => deleteComment(comment.id)}>🗑️ 삭제</button>
                                    </div>
                                )
                            )}
                        </div>
                    ))}
                    <div className="mt-10 text-center">
                        <button
                            className="btn bg-blue-500 hover:bg-blue-600 text-white"
                            onClick={() => window.location.href = '/'}
                        >
                            ⬅️ 메인페이지로 돌아가기
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}
