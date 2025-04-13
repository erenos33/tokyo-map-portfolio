export async function fetchUserPage() {
    const token = localStorage.getItem('accessToken');
    try {
        const res = await fetch('http://localhost:8080/api/auth/test', {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });
        return res.ok ? await res.text() : '❌ 접근 실패 (사용자)';
    } catch (e) {
        return '❌ 서버 오류';
    }
}

export async function fetchAdminPage() {
    const token = localStorage.getItem('accessToken');
    try {
        const res = await fetch('http://localhost:8080/api/auth/api/admin/only', {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });
        return res.ok ? await res.text() : '❌ 접근 실패 (관리자 전용)';
    } catch (e) {
        return '❌ 서버 오류';
    }
}
