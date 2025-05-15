// userApi.js（ユーザー登録および認証確認のAPI）
import axios from './axiosInstance';

// ユーザー新規登録API
export async function registerUser(userData) {
    try {
        const response = await axios.post('/users/register', userData);
        return response.data; // 新規登録はメッセージのみ返却されるのでOK
    } catch (err) {
        throw new Error('ユーザー登録に失敗しました');
    }
}

// ログイン状態のテスト取得API（一般ユーザー確認用）
export async function getUserTest() {
    try {
        const res = await axios.get('/auth/test');
        return res.data.data; // 応答のdataプロパティを抽出
    } catch (err) {
        if (err.response?.status === 403) {
            throw new Error('アクセスに失敗しました（ユーザーではありません）');
        } else {
            throw new Error('サーバーエラーが発生しました');
        }
    }
}
