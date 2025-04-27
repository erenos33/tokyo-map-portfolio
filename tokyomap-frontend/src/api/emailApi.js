// 수정된 emailApi.js
export async function sendVerificationEmail(email) {
    try {
        const response = await fetch(`http://localhost:8080/api/email/send?email=${email}`, {
            method: 'POST',
        });
        if (response.ok) {
            const result = await response.json();
            return result.status === 200; // 수정된 부분: ApiResponse 구조 확인
        } else {
            return false;
        }
    } catch (error) {
        console.error('인증 메일 전송 실패:', error);
        return false;
    }
}

export async function verifyEmailCode(email, code) {
    try {
        const response = await fetch(`http://localhost:8080/api/email/verify`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ email, code }),
        });
        if (response.ok) {
            const result = await response.json();
            return result.status === 200; // 수정된 부분: ApiResponse 구조 확인
        } else {
            return false;
        }
    } catch (error) {
        console.error('이메일 인증 실패:', error);
        return false;
    }
}