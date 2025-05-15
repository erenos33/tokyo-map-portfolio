import React, { useState } from 'react';
import axiosInstance from '../api/axiosInstance';

export default function LocationPage() {
    const [adminLevel2, setAdminLevel2] = useState('');
    const [locations, setLocations] = useState([]);

    // 地域情報をAPIから取得する
    const fetchLocations = async () => {
        try {
            const res = await axiosInstance.get('/locations', {
                params: adminLevel2 ? { adminLevel2 } : {}
            });
            setLocations(res.data.data);
        } catch (error) {
            console.error('地域情報の取得に失敗しました:', error);
            alert('地域情報の取得に失敗しました。');
        }
    };

    return (
        <div className="bg-gray-100 min-h-screen py-10 px-4">
            <div className="max-w-4xl mx-auto bg-white p-6 rounded-xl shadow">
                <h2 className="text-2xl font-bold mb-6 text-center">🌏 지역 목록 조회</h2>

                <div className="flex flex-col sm:flex-row gap-3 mb-6">
                    <input
                        type="text"
                        placeholder="区・市の名前を入力（例：Shibuya City）"
                        value={adminLevel2}
                        onChange={(e) => setAdminLevel2(e.target.value)}
                        className="flex-1 px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring focus:border-blue-400 placeholder-gray-400"
                    />
                    <button className="btn" onClick={fetchLocations}>地域を検索する</button>
                </div>

                <div className="space-y-4">
                    {locations.length > 0 ? (
                        locations.map((loc, index) => (
                            <div key={index} className="border rounded-lg p-4 shadow-sm bg-gray-50">
                                <p><strong>国:</strong> {loc.country}</p>
                                <p><strong>都道府県:</strong> {loc.adminLevel}</p>
                                <p><strong>市区町村:</strong> {loc.adminLevel2}</p>
                                <p><strong>町名・地域:</strong> {loc.locality}</p>
                                <p><strong>住所:</strong> {loc.streetAddress}</p>
                                <p><strong>郵便番号:</strong> {loc.postalCode}</p>
                            </div>
                        ))
                    ) : (
                        <p className="text-gray-500">表示する地域情報がありません。</p>
                    )}
                    <div className="mt-10 text-center">
                        <button
                            className="btn bg-blue-500 hover:bg-blue-600 text-white"
                            onClick={() => window.location.href = '/'}
                        >
                            メインページに戻る
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}
