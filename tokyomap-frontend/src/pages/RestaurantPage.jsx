import React, { useState } from 'react';
import axiosInstance from '../api/axiosInstance';

export default function RestaurantPage() {
    const [category, setCategory] = useState('');
    const [city, setCity] = useState('');
    const [openNow, setOpenNow] = useState(false);
    const [restaurantList, setRestaurantList] = useState([]);
    const [restaurantDetail, setRestaurantDetail] = useState(null);
    const [restaurantId, setRestaurantId] = useState('');

    const [keyword, setKeyword] = useState('');
    const [location, setLocation] = useState('Tokyo');
    const [googleResults, setGoogleResults] = useState([]);
    const [nextPageToken, setNextPageToken] = useState(null);

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

    const searchGooglePlaces = async () => {
        try {
            const response = await axiosInstance.get('/maps/search', {
                params: { keyword, location },
            });
            setGoogleResults(response.data.results);
            setNextPageToken(response.data.next_page_token);
        } catch (error) {
            console.error('구글맵 맛집 검색 실패', error);
        }
    };

    const fetchNextPage = async () => {
        try {
            const response = await axiosInstance.get('/maps/next', {
                params: { token: nextPageToken },
            });
            setGoogleResults((prev) => [...prev, ...response.data.results]);
            setNextPageToken(response.data.next_page_token);
        } catch (error) {
            console.error('구글맵 다음 페이지 검색 실패', error);
        }
    };

    const registerGooglePlace = async (place) => {
        const token = localStorage.getItem('accessToken');
        if (!token) return alert('로그인이 필요합니다');

        // 🔒 필수 필드 검증
        if (!place.place_id || !place.name || !place.formatted_address || !place.geometry?.location) {
            alert('⚠️ 장소 정보가 부족하여 등록할 수 없습니다.');
            return;
        }

        const dto = {
            placeId: place.place_id,
            name: place.name,
            address: place.formatted_address,
            rating: place.rating ?? 0,
            latitude: place.geometry.location.lat,
            longitude: place.geometry.location.lng
        };

        console.log('📦 등록 요청 DTO:', dto);

        try {
            const response = await axiosInstance.post('/restaurants/register/google', dto, {
                headers: { Authorization: `Bearer ${token}` },
            });
            alert('✅ 등록 성공! ID: ' + response.data.data);
        } catch (error) {
            if (error.response?.status === 409) {
                alert('⚠️ 이미 등록된 음식점입니다.');
            } else if (error.response?.status === 401) {
                alert('🔐 로그인이 필요합니다.');
            } else {
                alert('🚨 등록 중 오류 발생');
            }
            console.error(error);
        }
    };

    const deleteRestaurant = async (id) => {
        const token = localStorage.getItem('accessToken');
        if (!token) return alert('로그인이 필요합니다');

        if (!window.confirm('정말 삭제하시겠습니까?')) return;

        try {
            await axiosInstance.delete(`/restaurants/${id}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            alert('삭제 완료');
        } catch (error) {
            alert('삭제 실패');
            console.error(error);
        }
    };

    return (
        <div style={{ padding: 30 }}>
            <h2>🍽️ 맛집 검색 페이지 (DB)</h2>

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
                    <p>📍 {restaurant.address}</p>
                    <p>⭐ 평점: {restaurant.rating}</p>
                    <button onClick={() => deleteRestaurant(restaurant.id)}>삭제하기</button>
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
                    <p>주소: {restaurantDetail.address}</p>
                    <p>위도: {restaurantDetail.latitude}</p>
                    <p>경도: {restaurantDetail.longitude}</p>
                    <p>평점: {restaurantDetail.rating}</p>
                </div>
            )}

            <hr style={{ margin: '50px 0' }} />

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
                    <button onClick={() => registerGooglePlace(place)}>등록하기</button>
                    <hr />
                </div>
            ))}

            {nextPageToken && (
                <div style={{ marginTop: 20 }}>
                    <button onClick={fetchNextPage}>다음 페이지 불러오기</button>
                </div>
            )}
        </div>
    );
}
