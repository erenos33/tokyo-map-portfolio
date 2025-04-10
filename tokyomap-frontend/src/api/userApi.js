// src/api/userApi.js
export async function registerUser(data) {
    try {
        const response = await fetch('http://localhost:8080/api/users/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });

        return response.ok;
    } catch (error) {
        console.error('회원가입 요청 실패:', error);
        return false;
    }
}
