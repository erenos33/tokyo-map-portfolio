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
        <div className="bg-gray-100 min-h-screen py-10 px-4">
            <div className="max-w-4xl mx-auto">
                <h2 className="text-2xl font-bold mb-6">🍽️ 맛집 검색 페이지 (DB)</h2>

                <div className="bg-white p-6 rounded-xl shadow space-y-4 mb-10">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">카테고리</label>
                        <input
                            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring focus:border-blue-400 placeholder-gray-400"
                            placeholder="ex) 일식, 중식"
                            value={category}
                            onChange={(e) => setCategory(e.target.value)}
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">도시</label>
                        <input
                            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring focus:border-blue-400 placeholder-gray-400"
                            placeholder="ex) Tokyo, Osaka"
                            value={city}
                            onChange={(e) => setCity(e.target.value)}
                        />
                    </div>
                    <label className="text-sm flex items-center">
                        <input type="checkbox" checked={openNow} onChange={() => setOpenNow(!openNow)} className="mr-2" />
                        현재 영업중만 보기
                    </label>
                    <button className="btn w-full" onClick={searchRestaurants}>맛집 검색 (DB)</button>
                </div>

                <h3 className="text-xl font-semibold mb-4">검색된 맛집 목록 (DB)</h3>
                <div className="space-y-4 mb-12">
                    {restaurantList.map((restaurant) => (
                        <div key={restaurant.id} className="bg-white p-4 rounded shadow">
                            <p>🍴 {restaurant.name}</p>
                            <p>📍 {restaurant.address}</p>
                            <p>⭐ 평점: {restaurant.rating}</p>
                            <button className="btn bg-red-500 hover:bg-red-600 mt-2" onClick={() => deleteRestaurant(restaurant.id)}>삭제하기</button>
                        </div>
                    ))}
                </div>

                <h2 className="text-2xl font-bold mt-10 mb-4">🏠 맛집 상세조회 (DB)</h2>
                <div className="bg-white p-6 rounded-xl shadow space-y-3 mb-10">
                    <input
                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring focus:border-blue-400 placeholder-gray-400"
                        placeholder="음식점 ID 입력"
                        value={restaurantId}
                        onChange={(e) => setRestaurantId(e.target.value)}
                    />
                    <button className="btn w-full" onClick={getRestaurantDetail}>맛집 상세조회</button>
                </div>

                {restaurantDetail && (
                    <div className="bg-white p-4 rounded shadow mb-10">
                        <h3 className="text-xl font-bold mb-2">{restaurantDetail.name}</h3>
                        <p>주소: {restaurantDetail.address}</p>
                        <p>위도: {restaurantDetail.latitude}</p>
                        <p>경도: {restaurantDetail.longitude}</p>
                        <p>평점: {restaurantDetail.rating}</p>
                    </div>
                )}

                <h2 className="text-2xl font-bold mt-10 mb-4">🗺️ 구글맵 맛집 검색</h2>
                <div className="bg-white p-6 rounded-xl shadow space-y-3 mb-10">
                    <input
                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring focus:border-blue-400 placeholder-gray-400"
                        placeholder="검색 키워드"
                        value={keyword}
                        onChange={(e) => setKeyword(e.target.value)}
                    />
                    <input
                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring focus:border-blue-400 placeholder-gray-400"
                        placeholder="도시"
                        value={location}
                        onChange={(e) => setLocation(e.target.value)}
                    />
                    <button className="btn w-full" onClick={searchGooglePlaces}>구글맵 맛집 검색</button>
                </div>

                <h3 className="text-xl font-semibold mb-4">검색된 맛집 목록 (Google Maps)</h3>
                <div className="space-y-4">
                    {googleResults.map((place, index) => (
                        <div key={index} className="bg-white p-4 rounded shadow">
                            <p>🍴 이름: {place.name}</p>
                            <p>📍 주소: {place.formatted_address}</p>
                            <p>⭐ 평점: {place.rating}</p>
                            <p>🗺️ 위치: ({place.geometry?.location?.lat}, {place.geometry?.location?.lng})</p>
                            <button className="btn mt-2" onClick={() => registerGooglePlace(place)}>등록하기</button>
                        </div>
                    ))}
                </div>

                {nextPageToken && (
                    <div className="mt-6">
                        <button className="btn w-full" onClick={fetchNextPage}>다음 페이지 불러오기</button>
                    </div>
                )}
            </div>
        </div>
    );
}
