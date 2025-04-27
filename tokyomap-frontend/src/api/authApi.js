// 최종 수정된 authApi.js
export async function fetchUserPage() {
    const token = localStorage.getItem('accessToken');
    try {
        const res = await fetch('http://localhost:8080/api/auth/test', {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });
        if (res.ok) {
            const result = await res.json();
            return result.data;
        } else {
            return '❌ 접근 실패 (사용자)';
        }
    } catch (e) {
        return '❌ 서버 오류';
    }
}

export async function fetchAdminPage() {
    const token = localStorage.getItem('accessToken');
    try {
        const res = await fetch('http://localhost:8080/api/auth/admin/only', {  // ✅ 경로 수정
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });
        if (res.ok) {
            const result = await res.json();
            return result.data; // 🔥 data 꺼내기
        } else {
            return '❌ 접근 실패 (관리자 전용)';
        }
    } catch (e) {
        return '❌ 서버 오류';
    }
}
