// emailApi.js（メール認証関連のAPI）

// 認証メールを送信するAPI
export async function sendVerificationEmail(email) {
    try {
        const response = await fetch(`http://localhost:8080/api/email/send?email=${email}`, {
            method: 'POST',
        });
        if (response.ok) {
            const result = await response.json();
            return result.status === 200; // ApiResponse構造の確認
        } else {
            return false;
        }
    } catch (error) {
        console.error('認証メールの送信に失敗しました:', error);
        return false;
    }
}

// 認証コードを確認するAPI
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
            return result.status === 200; // ApiResponse構造の確認
        } else {
            return false;
        }
    } catch (error) {
        console.error('メール認証に失敗しました:', error);
        return false;
    }
}