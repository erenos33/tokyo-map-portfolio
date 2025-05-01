import React, { useState } from 'react';
import axiosInstance from '../api/axiosInstance';

export default function FavoritePage() {
    const [restaurantId, setRestaurantId] = useState('');
    const [liked, setLiked] = useState(null);
    const [favorites, setFavorites] = useState([]);
    const [page, setPage] = useState(0);

    const addFavorite = async () => {
        if (!restaurantId) {
            alert('음식점 ID를 입력하세요.');
            return;
        }
        try {
            await axiosInstance.post('/favorites', { restaurantId: Number(restaurantId) });
            alert('✅ 즐겨찾기 등록 성공!');
        } catch (error) {
            console.error('즐겨찾기 등록 실패:', error);
            alert('❌ 즐겨찾기 등록 실패');
        }
    };

    const removeFavorite = async () => {
        if (!restaurantId) {
            alert('음식점 ID를 입력하세요.');
            return;
        }
        try {
            await axiosInstance.delete('/favorites', { data: { restaurantId: Number(restaurantId) } });
            alert('✅ 즐겨찾기 삭제 성공!');
        } catch (error) {
            console.error('즐겨찾기 삭제 실패:', error);
            alert('❌ 즐겨찾기 삭제 실패');
        }
    };

    const checkFavorite = async () => {
        if (!restaurantId) {
            alert('음식점 ID를 입력하세요.');
            return;
        }
        try {
            const res = await axiosInstance.get('/favorites/check', { params: { restaurantId: Number(restaurantId) } });
            setLiked(res.data.data.liked);
        } catch (error) {
            console.error('즐겨찾기 여부 조회 실패:', error);
            alert('❌ 즐겨찾기 여부 조회 실패');
        }
    };

    const fetchFavorites = async () => {
        try {
            const res = await axiosInstance.get('/favorites/my', { params: { page, size: 10 } });
            setFavorites(res.data.data.content);
        } catch (error) {
            console.error('내 즐겨찾기 조회 실패:', error);
            alert('❌ 내 즐겨찾기 조회 실패');
        }
    };

    return (
        <div className="bg-gray-100 min-h-screen py-10 px-4">
            <div className="max-w-3xl mx-auto bg-white p-6 rounded-xl shadow">
                <h2 className="text-2xl font-bold mb-6 text-center">⭐ 즐겨찾기 페이지</h2>

                <div className="flex flex-col sm:flex-row sm:items-center gap-3 mb-6">
                    <input
                        type="text"
                        className="flex-1 px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring focus:border-blue-400 placeholder-gray-400"
                        placeholder="음식점 ID 입력"
                        value={restaurantId}
                        onChange={(e) => setRestaurantId(e.target.value)}
                    />
                    <div className="flex flex-wrap gap-2">
                        <button className="btn" onClick={addFavorite}>➕ 등록</button>
                        <button className="btn bg-red-500 hover:bg-red-600" onClick={removeFavorite}>➖ 삭제</button>
                        <button className="btn bg-yellow-500 hover:bg-yellow-600" onClick={checkFavorite}>🔍 여부 확인</button>
                        <button className="btn bg-gray-500 hover:bg-gray-600" onClick={fetchFavorites}>📖 즐겨찾기 목록</button>
                    </div>
                </div>

                {liked !== null && (
                    <div className="mb-6 text-center font-semibold">
                        <span className={liked ? "text-green-600" : "text-red-500"}>
                            이 음식점 즐겨찾기 여부: {liked ? '✅ 등록됨' : '❌ 등록 안 됨'}
                        </span>
                    </div>
                )}

                <div className="space-y-4">
                    {favorites.map(fav => (
                        <div key={fav.restaurantId} className="border rounded-lg p-4 shadow-sm bg-gray-50">
                            <p className="text-lg font-semibold mb-1">🍴 이름: {fav.name}</p>
                            <p className="text-sm text-gray-600 mb-1">📍 주소: {fav.address}</p>
                            <p className="text-sm text-gray-600">⭐ 평점: {fav.averageRating} / 리뷰 수: {fav.reviewCount}</p>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}
