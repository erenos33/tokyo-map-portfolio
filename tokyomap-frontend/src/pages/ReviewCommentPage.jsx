import React, { useState } from 'react';
import axiosInstance from '../api/axiosInstance';

export default function ReviewCommentPage() {
    // 入力状態管理
    const [reviewId, setReviewId] = useState('');
    const [commentContent, setCommentContent] = useState('');
    const [comments, setComments] = useState([]);
    const [editCommentId, setEditCommentId] = useState(null);
    const [editContent, setEditContent] = useState('');

    // コメント一覧を取得
    const fetchComments = async () => {
        if (!reviewId) {
            alert('レビューIDを入力してください。');
            return;
        }
        try {
            const res = await axiosInstance.get(`/reviews/${reviewId}/comments`, {
                params: { page: 0, size: 10, sort: 'createdAt,desc' }
            });
            setComments(res.data.data.content);
        } catch (error) {
            console.error('コメント取得失敗:', error);
            alert('コメントの取得に失敗しました。');
        }
    };

    // コメントを新規作成
    const createComment = async () => {
        if (!reviewId || !commentContent) {
            alert('レビューIDとコメント内容を入力してください。');
            return;
        }
        try {
            await axiosInstance.post(`/reviews/${reviewId}/comments`, {
                content: commentContent
            });
            alert('コメント作成に成功しました。');
            setCommentContent('');
            fetchComments();
        } catch (error) {
            console.error('コメント作成失敗:', error);
            alert('コメントの作成に失敗しました。');
        }
    };

    // コメントを編集
    const updateComment = async () => {
        if (!editContent) {
            alert('修正内容を入力してください。');
            return;
        }
        try {
            await axiosInstance.put(`/reviews/${reviewId}/comments/${editCommentId}`, {
                content: editContent
            });
            alert('コメントを修正しました。');
            setEditCommentId(null);
            setEditContent('');
            fetchComments();
        } catch (error) {
            console.error('コメント修正失敗:', error);
            alert('コメントの修正に失敗しました。');
        }
    };

    // コメントを削除
    const deleteComment = async (commentId) => {
        if (!window.confirm('本当にこのコメントを削除しますか？')) return;
        try {
            await axiosInstance.delete(`/reviews/${reviewId}/comments/${commentId}`);
            alert('コメントを削除しました。');
            fetchComments();
        } catch (error) {
            console.error('コメント削除失敗:', error);
            alert('コメントの削除に失敗しました。');
        }
    };

    return (
        <div className="bg-gray-100 min-h-screen py-10 px-4">
            <div className="max-w-3xl mx-auto bg-white p-6 rounded-xl shadow">
                <h2 className="text-2xl font-bold mb-6 text-center">レビューコメント 作成 / 編集 / 削除</h2>

                {/* レビューID入力とコメント一覧取得ボタン */}
                <div className="flex flex-col sm:flex-row gap-3 mb-4">
                    <input
                        type="text"
                        placeholder="レビューIDを入力"
                        value={reviewId}
                        onChange={(e) => setReviewId(e.target.value)}
                        className="flex-1 px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring focus:border-blue-400"
                    />
                    <button className="btn" onClick={fetchComments}>コメント一覧を表示</button>
                </div>

                <div className="mb-6">
                    <textarea
                        placeholder="コメントを入力"
                        value={commentContent}
                        onChange={(e) => setCommentContent(e.target.value)}
                        rows="3"
                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring focus:border-blue-400 resize-none"
                    />
                    <button className="btn mt-2" onClick={createComment}>コメントを投稿</button>
                </div>

                <div className="space-y-4">
                    {comments.map((comment) => (
                        <div key={comment.id} className="bg-gray-50 border rounded-lg p-4 shadow-sm">
                            <p className="mb-1">✍️ {comment.content}</p>
                            <p className="text-sm text-gray-500">作成者: {comment.nickname}</p>
                            <p className="text-sm text-gray-400 mb-2">作成日時: {new Date(comment.createdAt).toLocaleString()}</p>

                            {editCommentId === comment.id ? (
                                <>
                                    <textarea
                                        value={editContent}
                                        onChange={(e) => setEditContent(e.target.value)}
                                        rows="3"
                                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring focus:border-blue-400 resize-none mb-2"
                                    />
                                    <div className="flex gap-2">
                                        <button className="btn bg-green-500 hover:bg-green-600" onClick={updateComment}>修正を保存</button>
                                        <button className="btn bg-gray-400 hover:bg-gray-500" onClick={() => setEditCommentId(null)}>キャンセル</button>
                                    </div>
                                </>
                            ) : (
                                comment.isAuthor && (
                                    <div className="flex gap-2 mt-2">
                                        <button className="btn bg-yellow-500 hover:bg-yellow-600" onClick={() => {
                                            setEditCommentId(comment.id);
                                            setEditContent(comment.content);
                                        }}>修正</button>
                                        <button className="btn bg-red-500 hover:bg-red-600" onClick={() => deleteComment(comment.id)}>削除</button>
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
                            メインページに戻る
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}