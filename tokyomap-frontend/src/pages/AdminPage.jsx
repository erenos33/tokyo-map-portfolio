import React, { useEffect, useState } from 'react';
import axiosInstance from '../api/axiosInstance';
import { useNavigate } from 'react-router-dom';

const AdminPage = () => {
    // 状態変数の定義（メッセージ、検索条件、結果など）
    const [message, setMessage] = useState('');
    const [category, setCategory] = useState('');
    const [city, setCity] = useState('');
    const [openNow, setOpenNow] = useState(false);
    const [restaurantList, setRestaurantList] = useState([]);
    const [expandedHours, setExpandedHours] = useState({});
    const navigate = useNavigate();

    // 管理者ページアクセス時に認証チェックを行う
    useEffect(() => {
        const fetchAdminPage = async () => {
            try {
                const response = await axiosInstance.get('/auth/admin/only');
                setMessage(response.data.data);
            } catch {
                setMessage('アクセスに失敗しました（管理者権限が必要です）');
            }
        };
        fetchAdminPage();
    }, []);

    // 飲食店をカテゴリ・都市・営業時間条件で検索する
    const searchRestaurants = async () => {
        try {
            const res = await axiosInstance.get('/restaurants/search', {
                params: { category, city, openNow },
            });
            setRestaurantList(res.data.data.content);
        } catch {
            alert('データベース検索に失敗しました');
        }
    };

    const toggleHours = (id) => {
        setExpandedHours(prev => ({ ...prev, [id]: !prev[id] }));
    };

    // 営業終了時間を要約して表示する関数（曜日別テキストから抽出）
    const summarizeHours = (hoursText) => {
        if (!hoursText) return '';
        const match = hoursText
            .split(/,\s*|\n/)
            .map(s => s.trim())
            .find(line => line.startsWith(new Date().toLocaleDateString('en-US', { weekday: 'long' }) + ':'))
            ?.match(/(\d{1,2}:\d{2})\s*([AP]M)\s*[–-]\s*(\d{1,2}:\d{2})\s*([AP]M)/);
        if (!match) return '';
        const [, , , endTime, endPeriod] = match;
        const kor = endPeriod === 'AM' ? '午前' : '午後';
        return `${kor} ${endTime}に営業終了`;
    };

    // 現在営業中かどうかを判定するロジック
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

    // 飲食店を削除する（確認ダイアログあり）
    const deleteRestaurant = async (id) => {
        if (!window.confirm('本当に削除しますか？')) return;
        try {
            await axiosInstance.delete(`/restaurants/${id}`);
            setRestaurantList(prev => prev.filter(r => r.id !== id));
        } catch {
            alert('削除に失敗しました');
        }
    };

    return (
        <div className="bg-gray-100 min-h-screen py-10 px-4">
            <div className="max-w-4xl mx-auto">
                <h2 className="text-2xl font-bold mb-4">管理者専用ページ</h2>
                <p className="mb-6">{message}</p>
                <h3 className="text-xl font-semibold mb-4">データベース飲食店検索</h3>
                <div className="bg-white p-6 rounded-xl shadow space-y-4 mb-10">
                    <input
                        className="w-full px-3 py-2 border"
                        placeholder="カテゴリ"
                        value={category}
                        onChange={e => setCategory(e.target.value)}
                    />
                    <input
                        className="w-full px-3 py-2 border"
                        placeholder="都市"
                        value={city}
                        onChange={e => setCity(e.target.value)}
                    />
                    <label className="text-sm flex items-center">
                        <input
                            type="checkbox"
                            className="mr-2"
                            checked={openNow}
                            onChange={() => setOpenNow(!openNow)}
                        /> 現在営業中のみ表示
                    </label>
                    <button
                        className="btn w-full bg-blue-600 text-white py-2 rounded"
                        onClick={searchRestaurants}
                    >飲食店を検索</button>
                </div>

                {restaurantList.length > 0 && (
                    <div className="grid gap-4 mb-10">
                        {restaurantList.map(r => {
                            const open = isOpenNow(r.openingHours);
                            return (
                                <div key={r.id} className="bg-white p-5 rounded-xl shadow-md space-y-2">
                                    <h4 className="text-lg font-bold">{r.name}</h4>
                                    <p><span className="font-medium">住所:</span> {r.address}</p>
                                    <p><span className="font-medium">評価:</span> {r.rating ?? '情報なし'}</p>
                                    <p className="mt-2">営業状況: {' '}
                                        <div className="flex items-center space-x-1 cursor-pointer" onClick={() => toggleHours(r.id)}>
                                            {open
                                                ? <span className="text-green-600">営業中</span>
                                                : <span className="text-red-500">営業時間外</span>}
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
                                    <p>💰 <span className="font-medium">価格帯:</span> {r.priceRange?.trim() || '情報なし'}</p>
                                    <p>☎ <span className="font-medium">電話番号:</span> {r.phoneNumber?.trim() || '情報なし'}</p>
                                    <button onClick={() => deleteRestaurant(r.id)} className="btn bg-red-500 hover:bg-red-600 mt-2 text-white px-4 py-2 rounded">
                                        削除する
                                    </button>
                                </div>
                            );
                        })}
                    </div>
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
    );
};

export default AdminPage;
