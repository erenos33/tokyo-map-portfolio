import React, { useEffect, useState } from 'react';
import axiosInstance from '../api/axiosInstance';
import { useNavigate } from 'react-router-dom';

const AdminPage = () => {
    const [message, setMessage] = useState('');
    const [category, setCategory] = useState('');
    const [city, setCity] = useState('');
    const [openNow, setOpenNow] = useState(false);
    const [restaurantList, setRestaurantList] = useState([]);
    const [expandedHours, setExpandedHours] = useState({});
    const navigate = useNavigate();

    useEffect(() => {
        const fetchAdminPage = async () => {
            try {
                const response = await axiosInstance.get('/auth/admin/only');
                setMessage(response.data.data);
            } catch {
                setMessage('❌ 접근 실패 - 관리자 권한 필요');
            }
        };
        fetchAdminPage();
    }, []);

    const searchRestaurants = async () => {
        try {
            const res = await axiosInstance.get('/restaurants/search', {
                params: { category, city, openNow },
            });
            setRestaurantList(res.data.data.content);
        } catch {
            alert('DB 검색 실패');
        }
    };

    const toggleHours = (id) => {
        setExpandedHours(prev => ({ ...prev, [id]: !prev[id] }));
    };

    // summarizeHours: today closing summary (same logic as MyRestaurantPage)
    const summarizeHours = (hoursText) => {
        if (!hoursText) return '';
        const match = hoursText
            .split(/,\s*|\n/)
            .map(s => s.trim())
            .find(line => line.startsWith(new Date().toLocaleDateString('en-US', { weekday: 'long' }) + ':'))
            ?.match(/(\d{1,2}:\d{2})\s*([AP]M)\s*[–-]\s*(\d{1,2}:\d{2})\s*([AP]M)/);
        if (!match) return '';
        const [, , , endTime, endPeriod] = match;
        const kor = endPeriod === 'AM' ? '오전' : '오후';
        return `${kor} ${endTime}에 영업 종료`;
    };

    // isOpenNow: same robust logic
    const isOpenNow = (hoursText) => {
        if (!hoursText) return false;
        const todayLabel = new Date().toLocaleDateString('en-US', { weekday: 'long' }) + ':';
        const todayLine = hoursText.split(/,\s*|\n/).map(s => s.trim()).find(line => line.startsWith(todayLabel));
        if (!todayLine) return false;
        const match = todayLine.match(/(\d{1,2}:\d{2})\s*([AP]M)\s*[–-]\s*(\d{1,2}:\d{2})\s*([AP]M)/);
        if (!match) return false;
        const [, startTime, startPeriod, endTime, endPeriod] = match;
        const parseTime = (time, period) => {
            let [h, m] = time.split(':').map(Number);
            if (period === 'PM' && h !== 12) h += 12;
            if (period === 'AM' && h === 12) h = 0;
            return h * 60 + m;
        };
        const now = new Date();
        const nowMin = now.getHours() * 60 + now.getMinutes();
        const startMin = parseTime(startTime, startPeriod);
        const endMin = parseTime(endTime, endPeriod);
        if (startMin <= endMin) {
            return nowMin >= startMin && nowMin <= endMin;
        }
        return nowMin >= startMin || nowMin <= endMin;
    };

    const deleteRestaurant = async (id) => {
        if (!window.confirm('정말 삭제하시겠습니까?')) return;
        try {
            await axiosInstance.delete(`/restaurants/${id}`);
            setRestaurantList(prev => prev.filter(r => r.id !== id));
        } catch {
            alert('삭제 실패');
        }
    };

    return (
        <div className="bg-gray-100 min-h-screen py-10 px-4">
            <div className="max-w-4xl mx-auto">
                <h2 className="text-2xl font-bold mb-4">👑 관리자 전용 페이지</h2>
                <p className="mb-6">{message}</p>
                <button
                    className="btn bg-gray-500 text-white py-2 px-4 rounded mb-10"
                    onClick={() => navigate('/')}
                >⬅️ 메인페이지로 돌아가기</button>

                <h3 className="text-xl font-semibold mb-4">📋 DB 맛집 검색</h3>
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
                            onChange={() => setOpenNow(!openNow)}
                        /> 현재 영업중만 보기
                    </label>
                    <button
                        className="btn w-full bg-blue-600 text-white py-2 rounded"
                        onClick={searchRestaurants}
                    >DB 맛집 검색</button>
                </div>

                {restaurantList.length > 0 && (
                    <div className="grid gap-4 mb-10">
                        {restaurantList.map(r => {
                            const open = isOpenNow(r.openingHours);
                            return (
                                <div key={r.id} className="bg-white p-5 rounded-xl shadow-md space-y-2">
                                    <h4 className="text-lg font-bold">{r.name}</h4>
                                    <p>📍 <span className="font-medium">주소:</span> {r.address}</p>
                                    <p>⭐ <span className="font-medium">평점:</span> {r.rating ?? '정보 없음'}</p>
                                    <p className="mt-2">⏰ 영업시간: {' '}
                                        <div className="flex items-center space-x-1 cursor-pointer" onClick={() => toggleHours(r.id)}>
                                            {open
                                                ? <span className="text-green-600">영업 중</span>
                                                : <span className="text-red-500">영업 전</span>}
                                            <span>· {summarizeHours(r.openingHours)}</span>
                                            <span>{expandedHours[r.id] ? '▲' : '▼'}</span>
                                        </div>
                                    </p>
                                    {expandedHours[r.id] && r.openingHours && (
                                        <ul className="mt-1 list-disc list-inside text-sm">
                                            {r.openingHours.split(/,\s*|\n/).map((line, i) => (
                                                <li key={i}>{line.trim()}</li>
                                            ))}
                                        </ul>
                                    )}
                                    <p>💰 <span className="font-medium">가격대:</span> {r.priceRange?.trim() || '정보 없음'}</p>
                                    <p>☎ <span className="font-medium">전화번호:</span> {r.phoneNumber?.trim() || '정보 없음'}</p>
                                    <button onClick={() => deleteRestaurant(r.id)} className="btn bg-red-500 hover:bg-red-600 mt-2 text-white px-4 py-2 rounded">
                                        🗑️ 삭제하기
                                    </button>
                                </div>
                            );
                        })}
                    </div>
                )}
            </div>
        </div>
    );
};

export default AdminPage;
