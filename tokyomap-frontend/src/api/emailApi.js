export async function sendVerificationEmail(email) {
    try {
        const response = await fetch(`http://localhost:8080/api/email/send?email=${email}`, {
            method: 'POST',
        });
        return response.ok;
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
        return response.ok;
    } catch (error) {
        console.error('이메일 인증 실패:', error);
        return false;
    }
}
