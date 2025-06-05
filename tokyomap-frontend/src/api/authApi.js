// authApi.js（認証テスト用API呼び出し）

// ユーザーページ情報を取得
export async function fetchUserPage() {
    const token = localStorage.getItem('accessToken');
    try {
        const res = await fetch(`${import.meta.env.VITE_API_BASE_URL}/api/auth/test`, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });
        if (res.ok) {
            const result = await res.json();
            return result.data;
        } else {
            return 'アクセスに失敗しました（ユーザー）';
        }
    } catch (e) {
        return 'サーバーエラーが発生しました';
    }
}

// 管理者専用ページ情報を取得
export async function fetchAdminPage() {
    const token = localStorage.getItem('accessToken');
    try {
        const res = await fetch(`${import.meta.env.VITE_API_BASE_URL}/api/auth/admin/only`, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });
        if (res.ok) {
            const result = await res.json();
            return result.data; // 🔥 data 꺼내기
        } else {
            return 'アクセスに失敗しました（管理者専用）';
        }
    } catch (e) {
        return 'サーバーエラーが発生しました';
    }
}
