import React, { useState } from 'react';
import axiosInstance from '../api/axiosInstance';

export default function RestaurantPage() {
    // DB 검색 관련 상태
    const [category, setCategory] = useState('');
    const [city, setCity] = useState('');
    const [openNow, setOpenNow] = useState(false);
    const [restaurantList, setRestaurantList] = useState([]);

    // DB 상세조회 관련 상태
    const [restaurantId, setRestaurantId] = useState('');
    const [restaurantDetail, setRestaurantDetail] = useState(null);

    // Google 검색 관련 상태
    const [keyword, setKeyword] = useState('');
    const [location, setLocation] = useState('Tokyo');
    const [searchMode, setSearchMode] = useState('city');
    const [googleResults, setGoogleResults] = useState([]);
    const [nextPageToken, setNextPageToken] = useState(null);

    // 영업시간 토글 상태
    const [expandedHours, setExpandedHours] = useState({});
    const toggleHours = (placeId) =>
        setExpandedHours(prev => ({ ...prev, [placeId]: !prev[placeId] }));

    // 가격 레이블 정의
    const priceLabels = ['', '저렴', '보통', '비싸', '매우 비싸'];

    // 오늘 요약 문구 생성 (안전 체크 포함)
    const summarizeHours = (opening_hours) => {
        if (
            !opening_hours?.weekday_text ||
            !Array.isArray(opening_hours.weekday_text) ||
            opening_hours.open_now == null
        ) {
            return '';
        }
        const idx = new Date().getDay(); // 0=Sunday…6=Saturday
        const todayLine = opening_hours.weekday_text[idx];
        if (typeof todayLine !== 'string') return '';
        const parts = todayLine.split(': ');
        if (parts.length < 2) return '';
        const times = parts[1];
        if (!times.includes('–')) return '';
        const [, end] = times.split('–').map(s => s.trim());
        const [hms, period] = end.split(' ');
        const korPeriod = period === 'AM' ? '오전' : '오후';
        const action = opening_hours.open_now ? '영업 종료' : '영업 시작';
        return `${korPeriod} ${hms}에 ${action}`;
    };

    // 거리 계산 (GPS 필터링)
    const getDistanceFromLatLonInKm = (lat1, lon1, lat2, lon2) => {
        const R = 6371;
        const dLat = ((lat2 - lat1) * Math.PI) / 180;
        const dLon = ((lon2 - lon1) * Math.PI) / 180;
        const a =
            Math.sin(dLat / 2) ** 2 +
            Math.cos((lat1 * Math.PI) / 180) *
            Math.cos((lat2 * Math.PI) / 180) *
            Math.sin(dLon / 2) ** 2;
        const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    };

    // DB 검색
    const searchRestaurants = async () => {
        try {
            const resp = await axiosInstance.get('/restaurants/search', {
                params: { category, city, openNow },
            });
            setRestaurantList(resp.data.data.content);
        } catch (e) {
            console.error('맛집 검색 실패', e);
        }
    };

    // DB 상세조회
    const getRestaurantDetail = async () => {
        try {
            const resp = await axiosInstance.get(`/restaurants/${restaurantId}`);
            setRestaurantDetail(resp.data.data);
        } catch (e) {
            console.error('맛집 상세조회 실패', e);
        }
    };

    // Google Place 상세정보
    const fetchPlaceDetail = async (placeId) => {
        try {
            const resp = await axiosInstance.get('/maps/detail', {
                params: { placeId },
            });
            return resp.data;
        } catch (e) {
            console.error('📡 상세정보 불러오기 실패', e);
            return {};
        }
    };

    // Google Place 등록
    const registerGooglePlace = async (place) => {
        const token = localStorage.getItem('accessToken');
        if (!token) return alert('로그인이 필요합니다');
        const dto = {
            placeId: place.placeId,
            name: place.name,
            address:
                place.formatted_address ??
                place.vicinity ??
                place.detail?.formatted_address ??
                '',
            rating: place.rating ?? 0,
            latitude: place.geometry?.location?.lat ?? 0,
            longitude: place.geometry?.location?.lng ?? 0,
            openingHours:
                place.detail?.opening_hours?.weekday_text.join(', ') ?? '',
            priceLevel: place.detail?.price_level ?? null,
            phoneNumber: place.detail?.formatted_phone_number ?? '',
        };
        try {
            const resp = await axiosInstance.post(
                '/restaurants/register/google',
                dto,
                { headers: { Authorization: `Bearer ${token}` } }
            );
            alert('✅ 등록 성공! ID: ' + resp.data.data);
        } catch (e) {
            console.error('🚨 등록 실패', e);
            alert('등록에 실패했습니다');
        }
    };

    // Google 검색 (도시)
    const searchGooglePlaces = async () => {
        try {
            const resp = await axiosInstance.get('/maps/search', {
                params: { keyword, location },
            });
            const enriched = await Promise.all(
                resp.data.results.map(async raw => {
                    const detail = await fetchPlaceDetail(raw.place_id);
                    return { ...raw, placeId: raw.place_id, detail };
                })
            );
            setGoogleResults(enriched);
            setNextPageToken(resp.data.next_page_token);
        } catch (e) {
            console.error('Google 검색 실패', e);
        }
    };

    // Google 검색 (GPS)
    const searchGooglePlacesByGps = async () => {
        if (!navigator.geolocation)
            return alert('위치 기능을 사용할 수 없습니다');
        navigator.geolocation.getCurrentPosition(
            async ({ coords }) => {
                try {
                    const resp = await axiosInstance.get('/maps/search', {
                        params: {
                            keyword,
                            lat: coords.latitude,
                            lng: coords.longitude,
                        },
                    });
                    const filtered = resp.data.results.filter(r => {
                        const loc = r.geometry?.location;
                        return (
                            loc?.lat &&
                            loc?.lng &&
                            getDistanceFromLatLonInKm(
                                coords.latitude,
                                coords.longitude,
                                loc.lat,
                                loc.lng
                            ) <= 3
                        );
                    });
                    const enriched = await Promise.all(
                        filtered.map(async raw => {
                            const detail = await fetchPlaceDetail(raw.place_id);
                            return { ...raw, placeId: raw.place_id, detail };
                        })
                    );
                    setGoogleResults(enriched);
                    setNextPageToken(resp.data.next_page_token);
                } catch (e) {
                    console.error('GPS 검색 실패', e);
                }
            },
            err => {
                console.error(err);
                alert('위치 접근 실패');
            }
        );
    };

    // 다음 페이지
    const fetchNextPage = async () => {
        if (!nextPageToken) return;
        try {
            const resp = await axiosInstance.get('/maps/next', {
                params: { token: nextPageToken },
            });
            const enriched = await Promise.all(
                resp.data.results.map(async raw => {
                    const detail = await fetchPlaceDetail(raw.place_id);
                    return { ...raw, placeId: raw.place_id, detail };
                })
            );
            setGoogleResults(prev => [...prev, ...enriched]);
            setNextPageToken(resp.data.next_page_token);
        } catch (e) {
            console.error('다음 페이지 실패', e);
        }
    };

    return (
        <div className="bg-gray-100 min-h-screen py-10 px-4">
            <div className="max-w-4xl mx-auto">

                {/* DB 맛집 검색 */}
                <h2 className="text-2xl font-bold mb-6">🍽️ DB 맛집 검색</h2>
                <div className="bg-white p-6 rounded-xl shadow space-y-4 mb-10">
                    <input
                        className="w-full px-3 py-2 border"
                        placeholder="카테고리"
                        value={category}
                        onChange={e => setCategory(e.target.value)}
                    />
                    <input
                        className="w-full px-3 py-2 border"
                        placeholder="도시"
                        value={city}
                        onChange={e => setCity(e.target.value)}
                    />
                    <label className="text-sm flex items-center">
                        <input
                            type="checkbox"
                            className="mr-2"
                            checked={openNow}
                            onChange={() => setOpenNow(prev => !prev)}
                        />
                        현재 영업중만 보기
                    </label>
                    <button className="btn w-full" onClick={searchRestaurants}>
                        DB 맛집 검색
                    </button>
                </div>

                {/* DB 검색 결과 */}
                <h3 className="text-xl font-semibold mb-4">📋 DB 검색 결과</h3>
                <div className="space-y-4 mb-10">
                    {restaurantList.map(r => (
                        <div key={r.id} className="bg-white p-4 rounded shadow">
                            <p>🍴 이름: {r.name}</p>
                            <p>📍 주소: {r.address}</p>
                            <p>⭐ 평점: {r.rating}</p>
                            <button
                                className="btn bg-red-500 hover:bg-red-600 mt-2"
                                onClick={() => {
                                    if (window.confirm('삭제하시겠습니까?')) {
                                        axiosInstance
                                            .delete(`/restaurants/${r.id}`, {
                                                headers: { Authorization: `Bearer ${localStorage.getItem('accessToken')}` },
                                            })
                                            .then(() => alert('삭제 완료'))
                                            .catch(() => alert('삭제 실패'));
                                    }
                                }}
                            >
                                삭제하기
                            </button>
                        </div>
                    ))}
                </div>

                {/* DB 상세조회 */}
                <h2 className="text-2xl font-bold mt-10 mb-4">🏠 맛집 상세조회</h2>
                <div className="bg-white p-6 rounded-xl shadow space-y-3 mb-10">
                    <input
                        className="w-full px-3 py-2 border"
                        placeholder="음식점 ID 입력"
                        value={restaurantId}
                        onChange={e => setRestaurantId(e.target.value)}
                    />
                    <button className="btn w-full" onClick={getRestaurantDetail}>
                        상세조회
                    </button>
                </div>
                {restaurantDetail && (
                    <div className="bg-white p-4 rounded shadow mb-10">
                        <h3 className="text-xl font-bold mb-2">{restaurantDetail.name}</h3>
                        <p>📍 주소: {restaurantDetail.address}</p>
                        <p>⭐ 평점: {restaurantDetail.rating}</p>
                        <p>🌐 위도: {restaurantDetail.latitude}</p>
                        <p>🌐 경도: {restaurantDetail.longitude}</p>
                    </div>
                )}

                {/* Google 검색 */}
                <h2 className="text-2xl font-bold mt-10 mb-4">🗺️ 구글맵 맛집 검색</h2>
                <div className="bg-white p-4 rounded-xl shadow space-y-4 mb-10">
                    <div className="flex gap-2 mb-2">
                        <button
                            className={`w-1/2 px-3 py-2 border rounded ${
                                searchMode === 'city' ? 'bg-blue-500 text-white' : 'bg-white'
                            }`}
                            onClick={() => setSearchMode('city')}
                        >
                            도시 검색
                        </button>
                        <button
                            className={`w-1/2 px-3 py-2 border rounded ${
                                searchMode === 'gps' ? 'bg-blue-500 text-white' : 'bg-white'
                            }`}
                            onClick={() => setSearchMode('gps')}
                        >
                            📍 GPS 검색
                        </button>
                    </div>
                    <input
                        className="w-full px-3 py-2 border mb-2"
                        placeholder="검색 키워드"
                        value={keyword}
                        onChange={e => setKeyword(e.target.value)}
                    />
                    {searchMode === 'city' && (
                        <input
                            className="w-full px-3 py-2 border mb-2"
                            placeholder="도시 (예: Tokyo)"
                            value={location}
                            onChange={e => setLocation(e.target.value)}
                        />
                    )}
                    <button
                        className="btn w-full bg-blue-600 text-white py-2 rounded"
                        onClick={searchMode === 'city' ? searchGooglePlaces : searchGooglePlacesByGps}
                    >
                        {searchMode === 'city' ? '도시 기반 검색' : '내 위치 검색'}
                    </button>
                </div>

                {/* Google 검색 결과 */}
                <h3 className="text-xl font-semibold mb-4">📋 Google 검색 결과</h3>
                <div className="space-y-4 mb-10">
                    {googleResults.map((place, idx) => (
                        <div key={idx} className="bg-white p-4 rounded shadow">
                            <p>🍴 이름: {place.name}</p>
                            <p>
                                📍 주소:{' '}
                                {place.formatted_address ??
                                    place.vicinity ??
                                    place.detail?.formatted_address ??
                                    '정보 없음'}
                            </p>
                            <p>⭐ 평점: {place.rating ?? '정보 없음'}</p>

                            {/* 영업시간 요약 + 토글 */}
                            <p className="mt-2">
                                ⏰ 영업시간:{' '}
                                {place.detail?.opening_hours ? (
                                    <div
                                        className="flex items-center space-x-1 cursor-pointer"
                                        onClick={() => toggleHours(place.placeId)}
                                    >
                    <span
                        className={
                            place.detail.opening_hours.open_now
                                ? 'text-green-600'
                                : 'text-red-600'
                        }
                    >
                      {place.detail.opening_hours.open_now
                          ? '영업 중'
                          : '영업 종료'}
                    </span>
                                        <span>· {summarizeHours(place.detail.opening_hours)}</span>
                                        <span>
                      {expandedHours[place.placeId] ? '▲' : '▼'}
                    </span>
                                    </div>
                                ) : (
                                    '정보 없음'
                                )}
                            </p>

                            {/* 상세 리스트 */}
                            {expandedHours[place.placeId] &&
                                place.detail?.opening_hours && (
                                    <ul className="mt-1 list-disc list-inside text-sm">
                                        {place.detail.opening_hours.weekday_text.map(
                                            (line, i) => {
                                                const [engDay, times] = line.split(': ');
                                                const korDay =
                                                    {
                                                        Sunday: '일요일',
                                                        Monday: '월요일',
                                                        Tuesday: '화요일',
                                                        Wednesday: '수요일',
                                                        Thursday: '목요일',
                                                        Friday: '금요일',
                                                        Saturday: '토요일',
                                                    }[engDay] || engDay;
                                                return (
                                                    <li key={i}>
                                                        {korDay} {times}
                                                    </li>
                                                );
                                            }
                                        )}
                                    </ul>
                                )}

                            <p className="mt-2">
                                💰 가격대:{' '}
                                {typeof place.detail?.price_level === 'number'
                                    ? priceLabels[place.detail.price_level]
                                    : '정보 없음'}
                            </p>
                            <p className="mt-2">
                                ☎ 전화번호:{' '}
                                {place.detail?.formatted_phone_number ??
                                    '정보 없음'}
                            </p>

                            <button
                                className="btn mt-4"
                                onClick={() => registerGooglePlace(place)}
                            >
                                등록하기
                            </button>
                        </div>
                    ))}
                </div>

                {nextPageToken && (
                    <div className="mt-6">
                        <button className="btn w-full" onClick={fetchNextPage}>
                            다음 페이지 불러오기
                        </button>
                    </div>
                )}
            </div>
        </div>
    );
}
