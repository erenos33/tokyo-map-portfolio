import React, { useState } from 'react';
import axiosInstance from '../api/axiosInstance';

export default function FavoritePage() {
    const [restaurantId, setRestaurantId] = useState('');
    const [liked, setLiked] = useState(null);
    const [favorites, setFavorites] = useState([]);
    const [page, setPage] = useState(0);

    // お気に入りに登録
    const addFavorite = async () => {
        if (!restaurantId) {
            alert('飲食店IDを入力してください。');
            return;
        }
        try {
            await axiosInstance.post('/favorites', { restaurantId: Number(restaurantId) });
            alert('お気に入りに登録しました。');
        } catch (error) {
            console.error('登録失敗:', error);
            alert('お気に入り登録に失敗しました。');
        }
    };

    // お気に入りから削除
    const removeFavorite = async () => {
        if (!restaurantId) {
            alert('飲食店IDを入力してください。');
            return;
        }
        try {
            await axiosInstance.delete('/favorites', { data: { restaurantId: Number(restaurantId) } });
            alert('お気に入りから削除しました。');
        } catch (error) {
            console.error('削除失敗:', error);
            alert('お気に入り削除に失敗しました。');
        }
    };

    // お気に入りかどうか確認
    const checkFavorite = async () => {
        if (!restaurantId) {
            alert('飲食店IDを入力してください。');
            return;
        }
        try {
            const res = await axiosInstance.get('/favorites/check', { params: { restaurantId: Number(restaurantId) } });
            setLiked(res.data.data.liked);
        } catch (error) {
            console.error('確認失敗:', error);
            alert('お気に入り確認に失敗しました。');
        }
    };

    // 自分のお気に入り一覧を取得
    const fetchFavorites = async () => {
        try {
            const res = await axiosInstance.get('/favorites/my', { params: { page, size: 10 } });
            setFavorites(res.data.data.content);
        } catch (error) {
            console.error('取得失敗:', error);
            alert('お気に入り一覧の取得に失敗しました。');
        }
    };

    return (
        <div className="bg-gray-100 min-h-screen py-10 px-4">
            <div className="max-w-3xl mx-auto bg-white p-6 rounded-xl shadow">
                <h2 className="text-2xl font-bold mb-6 text-center">お気に入りページ</h2>

                <div className="flex flex-col sm:flex-row sm:items-center gap-3 mb-6">
                    <input
                        type="text"
                        className="flex-1 px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring focus:border-blue-400 placeholder-gray-400"
                        placeholder="飲食店IDを入力"
                        value={restaurantId}
                        onChange={(e) => setRestaurantId(e.target.value)}
                    />
                    <div className="flex flex-wrap gap-2">
                        <button className="btn" onClick={addFavorite}>登録</button>
                        <button className="btn bg-red-500 hover:bg-red-600" onClick={removeFavorite}>削除</button>
                        <button className="btn bg-yellow-500 hover:bg-yellow-600" onClick={checkFavorite}>確認</button>
                        <button className="btn bg-gray-500 hover:bg-gray-600" onClick={fetchFavorites}>一覧表示</button>
                    </div>
                </div>

                {liked !== null && (
                    <div className="mb-6 text-center font-semibold">
                        <span className={liked ? "text-green-600" : "text-red-500"}>
                            この飲食店のお気に入り状態: {liked ? '登録済み' : '未登録'}
                        </span>
                    </div>
                )}

                <div className="space-y-4">
                    {favorites.map(fav => (
                        <div key={fav.restaurantId} className="border rounded-lg p-4 shadow-sm bg-gray-50">
                            <p className="text-lg font-semibold mb-1">名前: {fav.name}</p>
                            <p className="text-sm text-gray-600 mb-1">住所: {fav.address}</p>
                            <p className="text-sm text-gray-600">評価: {fav.averageRating} / レビュー数: {fav.reviewCount}</p>
                        </div>
                    ))}
                </div>
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
    );
}
