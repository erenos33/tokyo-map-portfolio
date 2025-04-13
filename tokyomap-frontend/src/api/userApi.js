import axios from './axiosInstance';

export async function registerUser(userData) {
    try {
        const response = await axios.post('/users/register', userData);
        return response.data;
    } catch (err) {
        throw new Error('회원가입 실패');
    }
}

export async function getUserTest() {
    try {
        const res = await axios.get('/auth/test');
        return res.data;
    } catch (err) {
        if (err.response?.status === 403) {
            throw new Error('❌ 접근 실패 - 유효한 사용자 아님');
        } else {
            throw new Error('❌ 서버 오류');
        }
    }
}
