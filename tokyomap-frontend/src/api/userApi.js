// 수정된 userApi.js
import axios from './axiosInstance';

export async function registerUser(userData) {
    try {
        const response = await axios.post('/users/register', userData);
        return response.data; // 회원가입은 메시지만 받으니까 문제 없음
    } catch (err) {
        throw new Error('회원가입 실패');
    }
}

export async function getUserTest() {
    try {
        const res = await axios.get('/auth/test');
        return res.data.data; // 수정된 부분: 응답 안쪽 data 꺼내기
    } catch (err) {
        if (err.response?.status === 403) {
            throw new Error('❌ 접근 실패 - 유효한 사용자 아님');
        } else {
            throw new Error('❌ 서버 오류');
        }
    }
}
