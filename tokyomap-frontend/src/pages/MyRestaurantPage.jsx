import React, { useEffect, useState } from 'react';
import axiosInstance from '../api/axiosInstance';
import { useNavigate } from 'react-router-dom';

export default function MyRestaurantPage() {
    // 状態の定義
    const navigate = useNavigate();
    const [myRestaurants, setMyRestaurants]   = useState([]);
    const [expandedHours, setExpandedHours]   = useState({});
    const [reviewStates,  setReviewStates]    = useState({});
    const [commentStates, setCommentStates]   = useState({});
    const [favoriteStates, setFavoriteStates] = useState({});
    const [showFavoritesOnly, setShowFavoritesOnly] = useState(false);

    // お気に入り登録状態を確認する
    const checkFavorite = async (restaurantId) => {
        try {
            const res = await axiosInstance.get('/favorites/check', { params: { restaurantId } });
            setFavoriteStates(prev => ({ ...prev, [restaurantId]: res.data.data.favorite }));
        } catch (e) { console.error('お気に入り確認失敗', e); }
    };

    // お気に入りに追加
    const addFavorite = async (restaurantId) => {
        try {
            await axiosInstance.post('/favorites', { restaurantId });
            setFavoriteStates(prev => ({ ...prev, [restaurantId]: true }));
        } catch (e) {
            console.error('お気に入り追加失敗', e);
            alert(e.response?.data?.message ?? 'お気に入り追加に失敗しました');
        }
    };

    // お気に入りから削除
    const removeFavorite = async (restaurantId) => {
        try {
            await axiosInstance.request({ url: '/favorites', method: 'DELETE', data: { restaurantId } });
            setFavoriteStates(prev => ({ ...prev, [restaurantId]: false }));
        } catch (e) {
            console.error('お気に入り削除失敗', e);
            alert(e.response?.data?.message ?? 'お気に入り削除に失敗しました');
        }
    };

    // 自分が登録した飲食店を取得
    const fetchMyRestaurants = async () => {
        try {
            const res  = await axiosInstance.get('/restaurants/my');
            const list = res.data.data.content;
            setMyRestaurants(list);
            await Promise.all(list.map(r => checkFavorite(r.id)));
        } catch (e) {
            console.error('登録済み飲食店の取得に失敗', e);
            if (e.response?.status === 403) {
                alert("メール認証が必要です。");
                navigate("/email/verify");
            } else {
                alert("飲食店一覧の取得に失敗しました。");
            }
        }
    };
    useEffect(() => { fetchMyRestaurants(); }, []);

    // 飲食店を削除
    const deleteRestaurant = async (id) => {
        if (!window.confirm('本当に削除しますか？')) return;
        try {
            const res = await axiosInstance.delete(`/restaurants/${id}`);
            alert(res.data?.data ?? '削除完了');
            fetchMyRestaurants();
        } catch (e) {
            console.error('削除失敗', e);
            alert(e.response?.data?.message ?? '削除に失敗しました');
        }
    };

    // 営業時間の要約
    const summarizeHours = (hoursText) => {
        if (!hoursText) return '';

        const weekday = new Date().getDay(); // 0 = Sunday
        const jpDays = ['日曜日', '月曜日', '火曜日', '水曜日', '木曜日', '金曜日', '土曜日'];
        const today = jpDays[weekday];

        const lines = hoursText.split(/,\s*|\n/).map(s => s.trim());

        // 오늘 요일로 시작하거나, 이전 줄이 오늘 요일이었고 이번 줄은 숫자로 시작하면 포함
        const todayLines = [];
        let include = false;

        for (const line of lines) {
            if (line.startsWith(today)) {
                todayLines.push(line);
                include = true;
            } else if (include && /^[\d０-９]{1,2}/.test(line)) {
                todayLines.push(line);
            } else {
                include = false;
            }
        }

        if (todayLines.length === 0) return '';

        const lastLine = todayLines[todayLines.length - 1];

        // 일본어 시간 포맷 처리
        const jaMatch = lastLine.match(/(\d{1,2})時(\d{2})分[～〜\-~](\d{1,2})時(\d{2})分/);
        if (jaMatch) {
            const [, , , eh, em] = jaMatch;
            const period = parseInt(eh) < 12 ? '午前' : '午後';
            return `${period} ${eh}:${em}に営業終了`;
        }

        // 영어 포맷 처리
        const enMatch = lastLine.match(/(\d{1,2}:\d{2})\s*([AP]M).*?[–-〜~]\s*(\d{1,2}:\d{2})\s*([AP]M)/);
        if (enMatch) {
            const [, , , end, period] = enMatch;
            return `${period === 'AM' ? '午前' : '午後'} ${end}に営業終了`;
        }

        return '';
    };

    const isOpenNow = (hoursText) => {
        if (!hoursText) return false;

        const weekday = new Date().getDay();
        const jpDays = ['日曜日', '月曜日', '火曜日', '水曜日', '木曜日', '金曜日', '土曜日'];
        const today = jpDays[weekday];
        const nowMin = new Date().getHours() * 60 + new Date().getMinutes();

        const lines = hoursText.split(/,\s*|\n/).map(s => s.trim());
        const todayLines = [];
        let include = false;

        for (const line of lines) {
            if (line.startsWith(today)) {
                todayLines.push(line);
                include = true;
            } else if (include && /^[\d０-９]{1,2}/.test(line)) {
                todayLines.push(line);
            } else {
                include = false;
            }
        }

        const toMin = (h, m) => h * 60 + m;

        for (const line of todayLines) {
            const jaMatch = line.match(/(\d{1,2})時(\d{2})分[～〜\-~](\d{1,2})時(\d{2})分/);
            if (jaMatch) {
                const [, sh, sm, eh, em] = jaMatch.map(Number);
                const start = toMin(sh, sm);
                const end = toMin(eh, em);
                if (nowMin >= start && nowMin <= end) return true;
            }

            const enMatch = line.match(/(\d{1,2}:\d{2})\s*([AP]M).*?[–-〜~]\s*(\d{1,2}:\d{2})\s*([AP]M)/);
            if (enMatch) {
                const [, start, sp, end, ep] = enMatch;
                const conv = (t, p) => {
                    let [h, m] = t.split(':').map(Number);
                    if (p === 'PM' && h !== 12) h += 12;
                    if (p === 'AM' && h === 12) h = 0;
                    return h * 60 + m;
                };
                const startMin = conv(start, sp);
                const endMin = conv(end, ep);
                if (nowMin >= startMin && nowMin <= endMin) return true;
            }
        }

        return false;
    };


    // レビュー編集状態の管理
    const handleReviewChange = (rid, field, value) => {
        setReviewStates(prev => ({ ...prev, [rid]: { ...(prev[rid] || {}), [field]: value } }));
    };

    // レビュー取得
    const fetchReviews = async (restaurantId) => {
        try {
            const res = await axiosInstance.get(`/restaurants/${restaurantId}/reviews`,
                { params: { page: 0, size: 10, sort: 'createdAt,desc' } });
            setReviewStates(prev => ({
                ...prev,
                [restaurantId]: { ...(prev[restaurantId] || {}), reviews: res.data.data.content },
            }));
        } catch (e) { console.error('レビュー取得失敗', e); }
    };

    // レビュー投稿
    const submitReview = async (restaurantId) => {
        const { rating = 5, content = '' } = reviewStates[restaurantId] || {};
        try { await axiosInstance.post('/reviews', { restaurantId, rating: Number(rating), content });
            fetchReviews(restaurantId); setReviewStates(p => ({ ...p, [restaurantId]: {} })); }
        catch (e) { console.error('レビュー投稿失敗', e);
            alert('レビュー投稿に失敗しました');
        }
    };

    // レビュー編集開始
    const startEditReview = (rid, rv) => {
        handleReviewChange(rid, 'editId', rv.id);
        handleReviewChange(rid, 'editRating', rv.rating);
        handleReviewChange(rid, 'editContent', rv.content);
    };

    // レビュー更新
    const updateReview = async (rid) => {
        const { editId, editRating, editContent } = reviewStates[rid] || {};
        try {
            await axiosInstance.put(`/reviews/${editId}`, {
                restaurantId: rid,
                rating: editRating,
                content: editContent
            });
            fetchReviews(rid);
            setReviewStates(p => ({
                ...p,
                [rid]: { ...(p[rid]||{}), editId:null }
            }));
        } catch (e){
            console.error('レビュー更新失敗',e);
            alert('レビュー更新に失敗しました');
        }
    };

    // レビュー削除
    const deleteReview = async (rid, rvId) => {
        if(!window.confirm('レビューを削除しますか？')) return;
        try {
            await axiosInstance.delete(`/reviews/${rvId}`);
            fetchReviews(rid);
        } catch(e){
            console.error('レビュー削除失敗',e);
            alert('レビュー削除に失敗しました');
        }
    };

    // いいね・取り消し・数取得
    const likeReview   = async (id,rid)=>{ try{ await axiosInstance.post(`/reviews/${id}/like`);   fetchReviews(rid);}catch{} };
    const unlikeReview = async (id,rid)=>{ try{ await axiosInstance.delete(`/reviews/${id}/like`); fetchReviews(rid);}catch{} };
    const getLikeCount = async (id)=>{
        try{
            const res=await axiosInstance.get(`/reviews/${id}/likes/count`);
            alert(`いいね数: ${res.data.data.likeCount}`);
        }catch{}
    };

    // コメント入力・取得・登録・編集・削除
    const handleCommentChange = (rvId,f,v)=>{
        setCommentStates(p=>({ ...p,[rvId]:{...(p[rvId]||{}),[f]:v}}));
    };

    const fetchComments = async (rvId)=>{
        try{
            const res=await axiosInstance.get(`/reviews/${rvId}/comments`,{
                params:{page:0,size:10,sort:'createdAt,desc'}
            });
            setCommentStates(p=>({
                ...p,
                [rvId]:{...(p[rvId]||{}),comments:res.data.data.content}
            }));
        } catch(e){
            console.error('コメント取得失敗',e);}
    };

    const submitComment = async (rvId)=>{
        const content=commentStates[rvId]?.newContent||'';
        if(!content) return alert('コメント内容を入力してください');
        try{
            await axiosInstance.post(`/reviews/${rvId}/comments`,{content});
            handleCommentChange(rvId,'newContent','');
            fetchComments(rvId);
        } catch(e){
            console.error('コメント投稿失敗',e);
            alert('コメント投稿に失敗しました');
        }
    };

    const startEditComment=(rvId,c)=>{
        handleCommentChange(rvId,'editCommentId',c.id);
        handleCommentChange(rvId,'editContent',c.content);
    };

    const updateComment = async (rvId)=>{
        const { editCommentId,editContent }=commentStates[rvId]||{};
        if(!editContent) return alert('修正内容を入力してください');
        try{
            await axiosInstance.put(`/reviews/${rvId}/comments/${editCommentId}`,{content:editContent});
            setCommentStates(p=>({
                ...p,
                [rvId]:{...(p[rvId]||{}),editCommentId:null}
            }));
            fetchComments(rvId);
        } catch(e){
            console.error('コメント更新失敗',e);
            alert('コメント更新に失敗しました');
        }
    };

    const deleteComment=async(rvId,cId)=>{
        if(!window.confirm('コメントを削除しますか？')) return;
        try{
            await axiosInstance.delete(`/reviews/${rvId}/comments/${cId}`);
            fetchComments(rvId);
        } catch(e){
            console.error('コメント削除失敗',e);
            alert('コメント削除に失敗しました');
        }
    };

    // フィルター：お気に入りのみ表示
    const filteredRestaurants = showFavoritesOnly
        ? myRestaurants.filter(r => favoriteStates[r.id])
        : myRestaurants;

    /* ========================= JSX ========================= */
    return (
        <div className="bg-gray-100 min-h-screen py-10 px-4">
            <div className="max-w-5xl mx-auto">
                <h2 className="text-3xl font-bold text-center mb-8">
                    🍽 {showFavoritesOnly ? 'お気に入りリスト' : '自分が登録したレストラン'}
                </h2>

                {/* お気に入りフィルター切替ボタン */}
                <div className="text-center mb-6">
                    <button
                        className="inline-flex items-center px-4 py-2 text-sm rounded-lg border hover:bg-gray-50"
                        onClick={() => setShowFavoritesOnly(!showFavoritesOnly)}
                    >
                        {showFavoritesOnly ? 'すべての飲食店を表示' : 'お気に入りのみ表示'}
                    </button>
                </div>

                {/* データなし時の表示 */}
                {filteredRestaurants.length === 0 && (
                    <div className="text-center text-gray-500 py-20">
                        {showFavoritesOnly
                            ? 'お気に入りに登録した飲食店はありません。'
                            : '登録済みの飲食店はありません。'}
                    </div>
                )}

                {/* 飲食店カードの表示 */}
                {filteredRestaurants.map(r => {
                    const open      = isOpenNow(r.openingHours);
                    const lines     = r.openingHours ? r.openingHours.split(/,\s*|\n/) : [];
                    const state     = reviewStates[r.id] || {};
                    const reviews   = state.reviews || [];
                    const editId    = state.editId;
                    const favored   = favoriteStates[r.id];

                    return (
                        <div key={r.id} className="bg-white rounded-2xl shadow-lg p-6 mb-10">
                            {/* ヘッダー部 */}
                            <div className="border-b pb-4 mb-4 flex items-start justify-between">
                                <div>
                                    <h3 className="text-2xl font-semibold text-blue-600 mb-1">{r.name}</h3>
                                    <p className="text-gray-600 text-sm">📍 {r.address}</p>
                                </div>
                                {/* お気に入り切替ボタン */}
                                <button
                                    className="text-2xl select-none"
                                    title={favored ? 'お気に入りを解除' : 'お気に入りに登録'}
                                    onClick={() => (favored ? removeFavorite(r.id) : addFavorite(r.id))}
                                >
                                    {favored ? '⭐' : '☆'}
                                </button>
                            </div>

                            {/* 詳細情報 */}
                            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-sm mb-4">
                                <p>評価: <span className="font-semibold">{r.rating ?? '情報なし'}</span></p>
                                <p>価格帯: {r.priceRange?.trim() || '情報なし'}</p>
                                <p>電話番号: {r.phoneNumber?.trim() || '情報なし'}</p>
                                <div>
                                    営業時間:{' '}
                                    {r.openingHours ? (
                                        <div
                                            onClick={() => setExpandedHours(p => ({ ...p, [r.id]: !p[r.id] }))}
                                            className="inline-flex items-center space-x-1 cursor-pointer hover:underline"
                                        >
                      <span className={open ? 'text-green-600' : 'text-red-500'}>
                        {open ? '営業中' : '営業時間外'}
                      </span>
                                            <span className="text-blue-600">· {summarizeHours(r.openingHours)}</span>
                                            <span className="text-blue-600">{expandedHours[r.id] ? '▲' : '▼'}</span>
                                        </div>
                                    ) : '情報なし'}
                                    {expandedHours[r.id] && lines.length > 0 && (
                                        <ul className="mt-1 list-disc list-inside text-xs text-gray-600">
                                            {lines.map((line, i) => <li key={i}>{line}</li>)}
                                        </ul>
                                    )}
                                </div>
                            </div>

                            {/* 削除ボタン */}
                            <div className="flex justify-end mb-4">
                                <button
                                    className="bg-red-500 hover:bg-red-600 text-white text-sm px-4 py-2 rounded-lg"
                                    onClick={() => deleteRestaurant(r.id)}
                                >削除</button>
                            </div>

                            {/* レビュー投稿 */}
                            <div className="border-t pt-4">
                                <h4 className="text-sm font-semibold mb-2">レビュー投稿</h4>
                                <input
                                    type="number" min="1" max="5"
                                    className="w-20 px-2 py-1 border rounded text-sm mr-2"
                                    placeholder="評価"
                                    value={state.rating || ''}
                                    onChange={e => handleReviewChange(r.id, 'rating', e.target.value)}
                                />
                                <textarea
                                    rows="2"
                                    className="w-full px-3 py-1 border rounded text-sm mt-2"
                                    placeholder="レビュー内容"
                                    value={state.content || ''}
                                    onChange={e => handleReviewChange(r.id, 'content', e.target.value)}
                                />
                                <div className="text-right mt-2">
                                    <button
                                        className="bg-blue-500 hover:bg-blue-600 text-white px-4 py-1 rounded text-sm"
                                        onClick={() => submitReview(r.id)}
                                    >投稿</button>
                                </div>
                                <button
                                    className="text-sm text-blue-600 hover:underline mt-4"
                                    onClick={() => fetchReviews(r.id)}
                                >レビュー一覧を表示</button>

                                {/* レビュー一覧 */}
                                {reviews.length > 0 && (
                                    <div className="mt-4 space-y-3">
                                        {reviews.map(rev => {
                                            const cState = commentStates[rev.id] || {};
                                            return (
                                                <div key={rev.id} className="border rounded-lg p-4 shadow-sm bg-gray-50">
                                                    <div className="flex items-center justify-between mb-1">
                                                        <h5 className="font-semibold">👤 {rev.nickname}</h5>
                                                        <span className="text-yellow-500 text-sm select-none">
        {'★'.repeat(rev.rating)}{'☆'.repeat(5 - rev.rating)}
                                                            <span className="ml-1 text-gray-600">{rev.rating}</span>
    </span>
                                                    </div>
                                                    <p className="text-xs text-gray-400 mb-1">投稿日: {rev.createdAt?.slice(0,10)}</p>

                                                    {editId === rev.id ? (
                                                        <>
                                                            <input
                                                                type="number" min="1" max="5"
                                                                className="w-20 px-2 py-1 border rounded text-sm mr-2"
                                                                value={state.editRating || rev.rating}
                                                                onChange={e => handleReviewChange(r.id, 'editRating', e.target.value)}
                                                            />
                                                            <textarea
                                                                rows="2"
                                                                className="w-full px-3 py-1 border rounded text-sm mt-2"
                                                                value={state.editContent || rev.content}
                                                                onChange={e => handleReviewChange(r.id, 'editContent', e.target.value)}
                                                            />
                                                            <div className="mt-2 space-x-2">
                                                                <button
                                                                    className="bg-green-500 hover:bg-green-600 text-white px-3 py-1 rounded text-sm"
                                                                    onClick={() => updateReview(r.id)}
                                                                >保存</button>
                                                                <button
                                                                    className="bg-gray-300 hover:bg-gray-400 text-gray-700 px-3 py-1 rounded text-sm"
                                                                    onClick={() => handleReviewChange(r.id, 'editId', null)}
                                                                >キャンセル</button>
                                                            </div>
                                                        </>
                                                    ) : (
                                                        <>
                                                            <p className="text-gray-800 mb-2">{rev.content}</p>
                                                            <div className="flex flex-wrap gap-2 mb-2">
                                                                <button className="px-2 py-1 bg-yellow-500 hover:bg-yellow-600 text-white text-sm rounded"
                                                                        onClick={() => startEditReview(r.id, rev)}>編集</button>
                                                                <button className="px-2 py-1 bg-red-500 hover:bg-red-600 text-white text-sm rounded"
                                                                        onClick={() => deleteReview(r.id, rev.id)}>削除</button>
                                                                <button className="px-2 py-1 border rounded text-sm"
                                                                        onClick={() => fetchComments(rev.id)}>コメント表示</button>
                                                                <button className="px-2 py-1 border rounded text-sm"
                                                                        onClick={() => likeReview(rev.id, r.id)}>いいね</button>
                                                                <button className="px-2 py-1 border rounded text-sm"
                                                                        onClick={() => unlikeReview(rev.id, r.id)}>いいね取り消し</button>
                                                                <button className="px-2 py-1 bg-gray-400 hover:bg-gray-500 text-white text-sm rounded"
                                                                        onClick={() => getLikeCount(rev.id)}>いいね数確認</button>
                                                            </div>

                                                            {/* コメント投稿 */}
                                                            <textarea
                                                                rows="2"
                                                                className="w-full px-3 py-1 border rounded text-sm mb-2"
                                                                placeholder="コメント内容を入力"
                                                                value={cState.newContent || ''}
                                                                onChange={e => handleCommentChange(rev.id, 'newContent', e.target.value)}
                                                            />
                                                            <button
                                                                className="bg-blue-500 hover:bg-blue-600 text-white px-3 py-1 rounded text-sm mb-3"
                                                                onClick={() => submitComment(rev.id)}
                                                            >コメント投稿</button>

                                                            {/* コメント一覧 */}
                                                            {cState.comments && cState.comments.length > 0 && (
                                                                <div className="space-y-2">
                                                                    {cState.comments.map(com => (
                                                                        <div key={com.id} className="bg-white p-2 rounded border">
                                                                            <p className="mb-1">{com.content}</p>
                                                                            <p className="text-xs text-gray-500 mb-1">
                                                                                {com.nickname} · {com.createdAt?.slice(0,10)}
                                                                            </p>
                                                                            {cState.editCommentId === com.id ? (
                                                                                <>
                                          <textarea
                                              rows="2"
                                              className="w-full px-2 py-1 border rounded text-sm"
                                              value={cState.editContent || com.content}
                                              onChange={e => handleCommentChange(rev.id, 'editContent', e.target.value)}
                                          />
                                                                                    <button className="text-sm text-green-600"
                                                                                            onClick={() => updateComment(rev.id)}>保存</button>
                                                                                    <button className="text-sm text-gray-600"
                                                                                            onClick={() => handleCommentChange(rev.id, 'editCommentId', null)}>キャンセル</button>
                                                                                </>
                                                                            ) : (
                                                                                <>
                                                                                    <button className="text-sm text-yellow-600"
                                                                                            onClick={() => startEditComment(rev.id, com)}>編集</button>
                                                                                    <button className="text-sm text-red-600"
                                                                                            onClick={() => deleteComment(rev.id, com.id)}>削除</button>
                                                                                </>
                                                                            )}
                                                                        </div>
                                                                    ))}
                                                                </div>
                                                            )}
                                                        </>
                                                    )}
                                                </div>
                                            );
                                        })}
                                    </div>
                                )}
                            </div>
                        </div>
                    );
                })}
            </div>
        </div>
    );
}
