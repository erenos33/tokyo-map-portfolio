// src/pages/LocationPage.jsx
import React, { useState } from 'react';
import axiosInstance from '../api/axiosInstance';

export default function LocationPage() {
    const [adminLevel2, setAdminLevel2] = useState('');
    const [locations, setLocations] = useState([]);

    const fetchLocations = async () => {
        try {
            const res = await axiosInstance.get('/locations', {
                params: adminLevel2 ? { adminLevel2 } : {}
            });
            setLocations(res.data.data);
        } catch (error) {
            console.error('지역 정보 조회 실패:', error);
            alert('❌ 지역 정보 조회 실패');
        }
    };

    return (
        <div style={{ padding: 30 }}>
            <h2>🌏 지역 목록 조회</h2>

            <input
                type="text"
                placeholder="구/시 이름 입력 (예: Shibuya City)"
                value={adminLevel2}
                onChange={(e) => setAdminLevel2(e.target.value)}
            />
            <br /><br />
            <button onClick={fetchLocations}>📖 지역 조회하기</button>

            <div style={{ marginTop: 30 }}>
                {locations.length > 0 ? (
                    locations.map((loc, index) => (
                        <div key={index} style={{ border: '1px solid #ccc', marginBottom: '10px', padding: '10px' }}>
                            <p>국가: {loc.country}</p>
                            <p>광역시/도: {loc.adminLevel}</p>
                            <p>시/구: {loc.adminLevel2}</p>
                            <p>동/지역: {loc.locality}</p>
                            <p>도로명 주소: {loc.streetAddress}</p>
                            <p>우편번호: {loc.postalCode}</p>
                        </div>
                    ))
                ) : (
                    <p>표시할 지역 정보가 없습니다.</p>
                )}
            </div>
        </div>
    );
}
