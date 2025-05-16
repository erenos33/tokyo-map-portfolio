
import { BrowserRouter, Routes, Route } from "react-router-dom";
import MainPage from "./pages/MainPage";
import Login from "./pages/Login";
import Register from "./pages/Register";
import EmailSend from "./pages/EmailSend";
import EmailVerify from "./pages/EmailVerify";
import UserPage from "./pages/UserPage";
import AdminPage from "./pages/AdminPage";
import RestaurantPage from "./pages/RestaurantPage";
import MyRestaurantPage from "./pages/MyRestaurantPage";
import ReviewCreatePage from "./pages/ReviewCreatePage";
import ReviewListPage from "./pages/ReviewListPage";
import ReviewEditPage from "./pages/ReviewEditPage";
import ReviewCommentPage from "./pages/ReviewCommentPage";
import ReviewCommentViewPage from "./pages/ReviewCommentViewPage";
import FavoritePage from "./pages/FavoritePage";
import LocationPage from "./pages/LocationPage";
import RequireAuth from "./components/RequireAuth";

export default function App() {
    return (
        <BrowserRouter>
            <Routes>
                {/* 公開ページ（誰でもアクセス可能） */}
                <Route path="/" element={<MainPage />} />
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                <Route path="/email/send" element={<EmailSend />} />
                <Route path="/email/verify" element={<EmailVerify />} />
                <Route path="/restaurant" element={<RestaurantPage />} />
                <Route path="/review/list" element={<ReviewListPage />} />
                <Route path="/review/comments/view" element={<ReviewCommentViewPage />} />
                <Route path="/locations" element={<LocationPage />} />

                {/* 認証済みユーザー専用ページ */}
                <Route path="/user" element={<RequireAuth><UserPage /></RequireAuth>} />
                <Route path="/restaurant/my" element={<RequireAuth><MyRestaurantPage /></RequireAuth>} />
                <Route path="/review/create" element={<RequireAuth><ReviewCreatePage /></RequireAuth>} />
                <Route path="/review/edit/:id" element={<RequireAuth><ReviewEditPage /></RequireAuth>} />
                <Route path="/review/comments" element={<RequireAuth><ReviewCommentPage /></RequireAuth>} />
                <Route path="/favorites" element={<RequireAuth><FavoritePage /></RequireAuth>} />

                {/* 管理者専用ページ */}
                <Route path="/admin" element={<RequireAuth><AdminPage /></RequireAuth>} />
            </Routes>
        </BrowserRouter>
    );
}
