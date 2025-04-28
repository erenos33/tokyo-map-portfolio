// src/pages/FavoritePage.jsx
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
        <div style={{ padding: 30 }}>
            <h2>⭐ 즐겨찾기 페이지</h2>

            <input
                type="text"
                placeholder="음식점 ID 입력"
                value={restaurantId}
                onChange={(e) => setRestaurantId(e.target.value)}
            />
            <br /><br />

            <button onClick={addFavorite}>➕ 즐겨찾기 등록</button>
            <button onClick={removeFavorite} style={{ marginLeft: 10 }}>➖ 즐겨찾기 삭제</button>
            <button onClick={checkFavorite} style={{ marginLeft: 10 }}>🔍 즐겨찾기 여부 확인</button>
            <button onClick={fetchFavorites} style={{ marginLeft: 10 }}>📖 내 즐겨찾기 목록 조회</button>

            {liked !== null && (
                <div style={{ marginTop: 20 }}>
                    <strong>이 음식점 즐겨찾기 여부: {liked ? '✅ 등록됨' : '❌ 등록 안 됨'}</strong>
                </div>
            )}

            <div style={{ marginTop: 30 }}>
                {favorites.map(fav => (
                    <div key={fav.restaurantId} style={{ border: '1px solid #ccc', padding: '10px', marginBottom: '10px' }}>
                        <p>🍴 이름: {fav.name}</p>
                        <p>📍 주소: {fav.address}</p>
                        <p>⭐ 평점: {fav.averageRating} / 리뷰 수: {fav.reviewCount}</p>
                    </div>
                ))}
            </div>
        </div>
    );
}
