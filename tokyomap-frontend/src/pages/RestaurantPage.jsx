// RestaurantPage.jsx
import React, { useState } from 'react';
import axiosInstance from '../api/axiosInstance';

export default function RestaurantPage() {
    const [category, setCategory] = useState('');
    const [city, setCity] = useState('');
    const [openNow, setOpenNow] = useState(false);
    const [restaurantList, setRestaurantList] = useState([]);
    const [restaurantDetail, setRestaurantDetail] = useState(null);
    const [restaurantId, setRestaurantId] = useState('');

    const searchRestaurants = async () => {
        try {
            const response = await axiosInstance.get('/restaurants/search', {
                params: { category, city, openNow },
            });
            setRestaurantList(response.data.data.content);
        } catch (error) {
            console.error('맛집 검색 실패', error);
        }
    };

    const getRestaurantDetail = async () => {
        try {
            const response = await axiosInstance.get(`/restaurants/${restaurantId}`);
            setRestaurantDetail(response.data.data);
        } catch (error) {
            console.error('맛집 상세조회 실패', error);
        }
    };

    return (
        <div style={{ padding: 30 }}>
            <h2>🍽️ 맛집 검색 페이지</h2>
            <input
                placeholder="카테고리 (ex: 일식, 한식)"
                value={category}
                onChange={(e) => setCategory(e.target.value)}
            />
            <br /><br />
            <input
                placeholder="도시 (ex: Tokyo)"
                value={city}
                onChange={(e) => setCity(e.target.value)}
            />
            <br /><br />
            <label>
                <input
                    type="checkbox"
                    checked={openNow}
                    onChange={() => setOpenNow(!openNow)}
                />
                현재 영업중만 보기
            </label>
            <br /><br />
            <button onClick={searchRestaurants}>맛집 검색</button>

            <hr />

            <h3>검색된 맛집 목록</h3>
            {restaurantList.map((restaurant) => (
                <div key={restaurant.id}>
                    <p>🍴 {restaurant.name}</p>
                </div>
            ))}

            <hr />

            <h2>🏠 맛집 상세조회</h2>
            <input
                placeholder="음식점 ID 입력"
                value={restaurantId}
                onChange={(e) => setRestaurantId(e.target.value)}
            />
            <br /><br />
            <button onClick={getRestaurantDetail}>맛집 상세조회</button>

            {restaurantDetail && (
                <div style={{ marginTop: 20 }}>
                    <h3>{restaurantDetail.name}</h3>
                    <p>{restaurantDetail.description}</p>
                    <p>주소: {restaurantDetail.address}</p>
                    <p>운영시간: {restaurantDetail.openingHours}</p>
                </div>
            )}
        </div>
    );
}
