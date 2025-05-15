import axios from 'axios';

// Axiosのインスタンスを作成（APIの共通設定）
const axiosInstance = axios.create({
    baseURL: 'http://localhost:8080/api', // APIのベースURL
    withCredentials: true, // 認証情報をリクエストに含める
});

// リクエスト送信前にJWTトークンをヘッダーに追加
axiosInstance.interceptors.request.use((config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

export default axiosInstance;
