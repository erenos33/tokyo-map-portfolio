import React, { useState } from 'react';
import axiosInstance from '../api/axiosInstance';

export default function RestaurantPage() {
    // ✅ 기존 상태들
    const [category, setCategory] = useState('');
    const [city, setCity] = useState('');
    const [openNow, setOpenNow] = useState(false);
    const [restaurantList, setRestaurantList] = useState([]);
    const [restaurantDetail, setRestaurantDetail] = useState(null);
    const [restaurantId, setRestaurantId] = useState('');

    const [keyword, setKeyword] = useState('');
    const [location, setLocation] = useState('Tokyo');
    const [googleResults, setGoogleResults] = useState([]);
    const [nextPageToken, setNextPageToken] = useState(null); // 🔥 추가

    // ✅ DB 검색
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

    // ✅ 구글맵 검색
    const searchGooglePlaces = async () => {
        try {
            const response = await axiosInstance.get('/maps/search', {
                params: { keyword, location },
            });
            setGoogleResults(response.data.results);
            setNextPageToken(response.data.next_page_token); // 🔥 next page token 저장
        } catch (error) {
            console.error('구글맵 맛집 검색 실패', error);
        }
    };

    // ✅ 다음 페이지 검색
    const fetchNextPage = async () => {
        try {
            const response = await axiosInstance.get('/maps/next', {
                params: { token: nextPageToken },
            });
            setGoogleResults((prev) => [...prev, ...response.data.results]); // 🔥 이어붙이기
            setNextPageToken(response.data.next_page_token); // 다음 토큰 갱신
        } catch (error) {
            console.error('구글맵 다음 페이지 검색 실패', error);
        }
    };

    return (
        <div style={{ padding: 30 }}>
            <h2>🍽️ 맛집 검색 페이지 (DB)</h2>

            {/* DB 검색 UI */}
            <input placeholder="카테고리" value={category} onChange={(e) => setCategory(e.target.value)} />
            <br /><br />
            <input placeholder="도시" value={city} onChange={(e) => setCity(e.target.value)} />
            <br /><br />
            <label>
                <input type="checkbox" checked={openNow} onChange={() => setOpenNow(!openNow)} />
                현재 영업중만 보기
            </label>
            <br /><br />
            <button onClick={searchRestaurants}>맛집 검색 (DB)</button>

            <hr />

            <h3>검색된 맛집 목록 (DB)</h3>
            {restaurantList.map((restaurant) => (
                <div key={restaurant.id}>
                    <p>🍴 {restaurant.name}</p>
                </div>
            ))}

            <hr />

            <h2>🏠 맛집 상세조회 (DB)</h2>
            <input placeholder="음식점 ID 입력" value={restaurantId} onChange={(e) => setRestaurantId(e.target.value)} />
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

            <hr style={{ margin: '50px 0' }} />

            {/* 구글맵 검색 UI */}
            <h2>🗺️ 구글맵 맛집 검색</h2>
            <input placeholder="검색 키워드" value={keyword} onChange={(e) => setKeyword(e.target.value)} />
            <br /><br />
            <input placeholder="도시" value={location} onChange={(e) => setLocation(e.target.value)} />
            <br /><br />
            <button onClick={searchGooglePlaces}>구글맵 맛집 검색</button>

            <hr />

            <h3>검색된 맛집 목록 (Google Maps)</h3>
            {googleResults.map((place, index) => (
                <div key={index} style={{ marginBottom: 20 }}>
                    <p>🍴 이름: {place.name}</p>
                    <p>📍 주소: {place.formatted_address}</p>
                    <p>⭐ 평점: {place.rating}</p>
                    <p>🗺️ 위치: ({place.geometry?.location?.lat}, {place.geometry?.location?.lng})</p>
                    <hr />
                </div>
            ))}

            {/* 🔥 다음 페이지 버튼 */}
            {nextPageToken && (
                <div style={{ marginTop: 20 }}>
                    <button onClick={fetchNextPage}>다음 페이지 불러오기</button>
                </div>
            )}
        </div>
    );
}
