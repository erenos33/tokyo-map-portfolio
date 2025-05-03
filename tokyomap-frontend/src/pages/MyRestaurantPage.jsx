import React, { useEffect, useState } from 'react';
import axiosInstance from '../api/axiosInstance';

export default function MyRestaurantPage() {
    const [myRestaurants, setMyRestaurants] = useState([]);
    const [expandedHours, setExpandedHours] = useState({});

    const fetchMyRestaurants = async () => {
        try {
            const res = await axiosInstance.get('/restaurants/my');
            setMyRestaurants(res.data.data.content);
        } catch (e) {
            console.error('내가 등록한 맛집 조회 실패', e);
        }
    };

    useEffect(() => {
        fetchMyRestaurants();
    }, []);

    const toggleHours = (id) =>
        setExpandedHours((prev) => ({ ...prev, [id]: !prev[id] }));

    // 오늘 요일 기준 영업시간 종료 요약
    const summarizeHours = (hoursText) => {
        if (!hoursText) return '';
        const parts = hoursText.split(/,\s*|\n/).map((s) => s.trim());
        const idx = (new Date().getDay() + 6) % 7; // 월요일=0
        const todayLine = parts[idx];
        if (!todayLine) return '';
        const match = todayLine.match(/(\d{1,2}:\d{2})\s*([AP]M)\s*[–-]\s*(\d{1,2}:\d{2})\s*([AP]M)/);
        if (!match) return '';
        const [, , , endTime, endPeriod] = match;
        const kor = endPeriod === 'AM' ? '오전' : '오후';
        return `${kor} ${endTime}에 영업 종료`;
    };

    // 현재 영업중 여부 판단 (시간대 파싱 robust)
    const isOpenNow = (hoursText) => {
        if (!hoursText) return false;
        const parts = hoursText.split(/,\s*|\n/).map((s) => s.trim());
        const idx = (new Date().getDay() + 6) % 7;
        const todayLine = parts[idx];
        if (!todayLine) return false;

        // 정규식으로 시각과 AM/PM 추출
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

        return nowMin >= startMin && nowMin <= endMin;
    };

    return (
        <div className="bg-gray-100 min-h-screen py-10 px-4">
            <div className="max-w-5xl mx-auto">
                <h2 className="text-3xl font-bold text-center mb-8">🍽 내가 등록한 맛집</h2>

                {myRestaurants.map((r) => {
                    const open = isOpenNow(r.openingHours);
                    const lines = r.openingHours ? r.openingHours.split(/,\s*|\n/) : [];

                    return (
                        <div key={r.id} className="bg-white rounded-2xl shadow-lg p-6 mb-10">
                            {/* 헤더 */}
                            <div className="border-b pb-4 mb-4">
                                <h3 className="text-2xl font-semibold text-blue-600 mb-1">{r.name}</h3>
                                <p className="text-gray-600 text-sm">📍 {r.address}</p>
                            </div>

                            {/* 정보 그리드 */}
                            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-sm mb-4">
                                <p>⭐ 평점: <span className="font-semibold">{r.rating ?? '정보 없음'}</span></p>
                                <p>💰 가격대: {r.priceRange?.trim() || '정보 없음'}</p>
                                <p>☎ 전화번호: {r.phoneNumber?.trim() || '정보 없음'}</p>

                                <div>
                                    ⏰ 영업시간:{' '}
                                    {r.openingHours ? (
                                        <div
                                            onClick={() => toggleHours(r.id)}
                                            className="inline-flex items-center space-x-1 cursor-pointer hover:underline"
                                        >
                                            {open ? (
                                                <span className="text-green-600 font-semibold">영업 중</span>
                                            ) : (
                                                <span className="text-red-500 font-semibold">영업 전</span>
                                            )}
                                            <span className="text-blue-600">· {summarizeHours(r.openingHours)}</span>
                                            <span className="text-blue-600">{expandedHours[r.id] ? '▲' : '▼'}</span>
                                        </div>
                                    ) : (
                                        '정보 없음'
                                    )}
                                    {expandedHours[r.id] && lines.length > 0 && (
                                        <ul className="mt-1 list-disc list-inside text-xs text-gray-600">
                                            {lines.map((line, i) => (
                                                <li key={i}>{line.trim()}</li>
                                            ))}
                                        </ul>
                                    )}
                                </div>
                            </div>

                            {/* 삭제 버튼 */}
                            <div className="flex justify-end">
                                <button
                                    className="bg-red-500 hover:bg-red-600 text-white text-sm px-4 py-2 rounded-lg"
                                    onClick={() => alert('삭제 기능 구현 예정')}
                                >
                                    🗑️ 삭제하기
                                </button>
                            </div>
                        </div>
                    );
                })}
            </div>
        </div>
    );
}