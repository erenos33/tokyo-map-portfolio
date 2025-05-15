import { Navigate } from "react-router-dom";

// 認証が必要なページにアクセスするためのコンポーネント
// アクセストークンがない場合は /login にリダイレクト
export default function RequireAuth({ children }) {
    const token = localStorage.getItem("accessToken");
    return token ? children : <Navigate to="/login" />;
}
