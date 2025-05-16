import React, { useState } from 'react';
import axiosInstance from '../api/axiosInstance';

export default function RestaurantPage() {
    // 検索キーワード、位置、モードなどの状態管理
    const [keyword, setKeyword] = useState('');
    const [location, setLocation] = useState('Tokyo');
    const [searchMode, setSearchMode] = useState('city');
    const [googleResults, setGoogleResults] = useState([]);
    const [nextPageToken, setNextPageToken] = useState(null);
    const [expandedHours, setExpandedHours] = useState({});
    const [currentCoords, setCurrentCoords] = useState(null);

    // 営業時間の展開・非展開を切り替え
    const toggleHours = (placeId) =>
        setExpandedHours(prev => ({ ...prev, [placeId]: !prev[placeId] }));
    const priceLabels = ['', '安い', '普通', '高い', '非常に高い'];

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
        if (parts.length < 2 || !parts[1].includes('–')) return '';

        const [start, end] = parts[1].split('–').map(s => s.trim());
        const timeToUse = opening_hours.open_now ? end : start;
        const [timeStr, period] = timeToUse.split(' ');
        const korPeriod = period === 'AM' ? '午前' : '午後';
        const action = opening_hours.open_now ? '営業終了' : '営業開始';

        return `${korPeriod} ${timeStr}에 ${action}`;
    };

    // Google詳細情報取得API呼び出し
    const fetchPlaceDetail = async (placeId) => {
        try {
            const resp = await axiosInstance.get('/maps/detail', {
                params: { placeId },
            });
            return resp.data;
        } catch (e) {
            console.error('詳細情報取得に失敗', e);
            return {};
        }
    };

    // ログインユーザーによるGoogleプレイスの登録処理
    const registerGooglePlace = async (place) => {
        const token = localStorage.getItem('accessToken');
        if (!token) return alert('ログインが必要です');
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
            alert('登録に成功しました。ID: ' + resp.data.data);
        } catch (e) {
            console.error('登録失敗', e);
            alert('登録に失敗しました');
        }
    };

    // 都市名とキーワードによるGoogleプレイス検索
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
            console.error('Google検索に失敗しました', e);
        }
    };

    // GPSを使って現在地から検索（半径3km以内にフィルタリング）
    const searchGooglePlacesByGps = async () => {
        if (!navigator.geolocation)
            return alert('位置情報サービスを使用できません');
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
                    console.error('GPS検索に失敗しました', e);
                }
            },
            err => {
                console.error(err);
                alert('位置情報へのアクセスに失敗しました');
            }
        );
    };

    // 緯度・経度から距離を計算（キロメートル）
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

    // 次のページの結果を取得（ページネーション対応）
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
            console.error('次のページの読み込みに失敗しました', e);
        }
    };

    // 画面出力（検索入力・結果表示UI）
    return (
        <div className="bg-gray-100 min-h-screen py-10 px-4">
            <div className="max-w-4xl mx-auto">
                <h2 className="text-2xl font-bold mt-10 mb-4">Googleマップ飲食店検索</h2>
                <div className="bg-white p-4 rounded-xl shadow space-y-4 mb-10">
                    <div className="flex gap-2 mb-2">
                        <button
                            className={`w-1/2 px-3 py-2 border rounded ${
                                searchMode === 'city' ? 'bg-blue-500 text-white' : 'bg-white'
                            }`}
                            onClick={() => setSearchMode('city')}
                        >
                            都市名で検索
                        </button>
                        <button
                            className={`w-1/2 px-3 py-2 border rounded ${
                                searchMode === 'gps' ? 'bg-blue-500 text-white' : 'bg-white'
                            }`}
                            onClick={() => setSearchMode('gps')}
                        >
                            現在地で検索
                        </button>
                    </div>
                    <input
                        className="w-full px-3 py-2 border mb-2"
                        placeholder="キーワード（例: ラーメン）"
                        value={keyword}
                        onChange={e => setKeyword(e.target.value)}
                    />
                    {searchMode === 'city' && (
                        <input
                            className="w-full px-3 py-2 border mb-2"
                            placeholder="都市名（例: Tokyo）"
                            value={location}
                            onChange={e => setLocation(e.target.value)}
                        />
                    )}
                    <button
                        className="btn w-full bg-blue-600 text-white py-2 rounded"
                        onClick={searchMode === 'city' ? searchGooglePlaces : searchGooglePlacesByGps}
                    >
                        {searchMode === 'city' ? '都市検索' : '現在地検索'}
                    </button>
                </div>
                {/* 検索結果一覧の表示 */}
                <h3 className="text-xl font-semibold mb-4">Google検索結果</h3>
                <div className="space-y-4 mb-10">
                    {googleResults.map((place, idx) => (
                        <div key={idx} className="bg-white p-4 rounded shadow">
                            <p>店名: {place.name}</p>
                            <p>
                                住所:{' '}
                                {place.formatted_address ??
                                    place.vicinity ??
                                    place.detail?.formatted_address ??
                                    '情報なし'}
                            </p>
                            <p>評価: {place.rating ?? '情報なし'}</p>
                            <p className="mt-2">
                                営業時間:{' '}
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
                                                ? '営業中'
                                                : '営業時間外'}
                                        </span>
                                        <span>· {summarizeHours(place.detail.opening_hours)}</span>
                                        <span>
                                            {expandedHours[place.placeId] ? '▲' : '▼'}
                                        </span>
                                    </div>
                                ) : (
                                    '情報なし'
                                )}
                            </p>
                            {expandedHours[place.placeId] &&
                                place.detail?.opening_hours && (
                                    <ul className="mt-1 list-disc list-inside text-sm">
                                        {place.detail.opening_hours.weekday_text.map(
                                            (line, i) => {
                                                const [engDay, times] = line.split(': ');
                                                const jpDay =
                                                    {
                                                        Sunday: '日曜日',
                                                        Monday: '月曜日',
                                                        Tuesday: '火曜日',
                                                        Wednesday: '水曜日',
                                                        Thursday: '木曜日',
                                                        Friday: '金曜日',
                                                        Saturday: '土曜日',
                                                    }[engDay] || engDay;
                                                return (
                                                    <li key={i}>
                                                        {jpDay} {times}
                                                    </li>
                                                );
                                            }
                                        )}
                                    </ul>
                                )}
                            <p className="mt-2">
                                価格帯:{' '}
                                {typeof place.detail?.price_level === 'number'
                                    ? priceLabels[place.detail.price_level]
                                    : '情報なし'}
                            </p>
                            <p className="mt-2">
                                電話番号:{' '}
                                {place.detail?.formatted_phone_number ?? '情報なし'}
                            </p>
                            <button
                                className="btn mt-4"
                                onClick={() => registerGooglePlace(place)}
                            >
                                登録する
                            </button>
                        </div>
                    ))}
                </div>
                {nextPageToken && (
                    <div className="mt-6">
                        <button className="btn w-full" onClick={fetchNextPage}>
                            次のページを読み込む
                        </button>
                    </div>
                )}
                <div className="mt-12 text-center">
                    <button
                        className="btn bg-blue-500 hover:bg-blue-600 text-white"
                        onClick={() => window.location.href = '/'}
                    >
                        メインページへ戻る
                    </button>
                </div>
            </div>
        </div>
    );
}
