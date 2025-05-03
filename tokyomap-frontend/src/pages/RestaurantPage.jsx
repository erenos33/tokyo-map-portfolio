import React, { useState } from 'react';
import axiosInstance from '../api/axiosInstance';

export default function RestaurantPage() {
    const [keyword, setKeyword] = useState('');
    const [location, setLocation] = useState('Tokyo');
    const [searchMode, setSearchMode] = useState('city');
    const [googleResults, setGoogleResults] = useState([]);
    const [nextPageToken, setNextPageToken] = useState(null);
    const [expandedHours, setExpandedHours] = useState({});
    const [currentCoords, setCurrentCoords] = useState(null);

    const toggleHours = (placeId) =>
        setExpandedHours(prev => ({ ...prev, [placeId]: !prev[placeId] }));
    const priceLabels = ['', '저렴', '보통', '비싸', '매우 비싸'];

    const summarizeHours = (opening_hours) => {
        if (
            !opening_hours?.weekday_text ||
            !Array.isArray(opening_hours.weekday_text) ||
            opening_hours.open_now == null
        ) return '';
        const jsDay = new Date().getDay();
        const googleIndex = (jsDay + 6) % 7;
        const todayLine = opening_hours.weekday_text[googleIndex];
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

    const registerGooglePlace = async (place) => {
        const token = localStorage.getItem('accessToken');
        if (!token) return alert('로그인이 필요합니다');
        const dto = {
            placeId: place.placeId,
            name: place.name,
            address:
                place.formatted_address ?? place.vicinity ?? place.detail?.formatted_address ?? '',
            rating: place.rating ?? 0,
            latitude: place.geometry?.location?.lat ?? 0,
            longitude: place.geometry?.location?.lng ?? 0,
            openingHours:
                place.detail?.opening_hours?.weekday_text.join(', ') ?? '',
            priceRange:
                typeof place.detail?.price_level === 'number'
                    ? priceLabels[place.detail.price_level]
                    : '',
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

    const searchGooglePlacesByGps = async () => {
        if (!navigator.geolocation)
            return alert('위치 기능을 사용할 수 없습니다');
        navigator.geolocation.getCurrentPosition(
            async ({ coords }) => {
                const { latitude, longitude } = coords;
                setCurrentCoords({ latitude, longitude });
                try {
                    const resp = await axiosInstance.get('/maps/search', {
                        params: {
                            keyword,
                            lat: latitude,
                            lng: longitude,
                        },
                    });
                    const filtered = resp.data.results.filter(r => {
                        const loc = r.geometry?.location;
                        return (
                            loc?.lat &&
                            loc?.lng &&
                            getDistanceFromLatLonInKm(
                                latitude,
                                longitude,
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
                    if (filtered.length >= 20) {
                        setNextPageToken(resp.data.next_page_token);
                    } else {
                        setNextPageToken(null);
                    }
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

    const fetchNextPage = async () => {
        if (!nextPageToken) return;
        try {
            const resp = await axiosInstance.get('/maps/next', {
                params: { token: nextPageToken },
            });
            let results = resp.data.results;
            if (searchMode === 'gps' && currentCoords) {
                const { latitude, longitude } = currentCoords;
                results = results.filter(r => {
                    const loc = r.geometry?.location;
                    return (
                        loc?.lat &&
                        loc?.lng &&
                        getDistanceFromLatLonInKm(
                            latitude,
                            longitude,
                            loc.lat,
                            loc.lng
                        ) <= 3
                    );
                });
            }
            const enriched = await Promise.all(
                results.map(async raw => {
                    const detail = await fetchPlaceDetail(raw.place_id);
                    return { ...raw, placeId: raw.place_id, detail };
                })
            );
            setGoogleResults(prev => [...prev, ...enriched]);
            if (searchMode === 'gps' && results.length < 20) {
                setNextPageToken(null);
            } else {
                setNextPageToken(resp.data.next_page_token);
            }
        } catch (e) {
            console.error('다음 페이지 실패', e);
        }
    };

    return (
        <div className="bg-gray-100 min-h-screen py-10 px-4">
            <div className="max-w-4xl mx-auto">
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
                                {place.detail?.formatted_phone_number ?? '정보 없음'}
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
