import { BrowserRouter, Routes, Route } from "react-router-dom";
import MainPage from "./pages/MainPage";
import Login from "./pages/Login";
import Register from "./pages/Register";
import EmailSend from "./pages/EmailSend";
import EmailVerify from "./pages/EmailVerify";
import UserPage from "./pages/UserPage";
import AdminPage from "./pages/AdminPage";
import RequireAuth from "./components/RequireAuth";

export default function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<MainPage />} />
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                <Route path="/email/send" element={<EmailSend />} />
                <Route path="/email/verify" element={<EmailVerify />} />
                <Route
                    path="/user"
                    element={
                        <RequireAuth>
                            <UserPage />
                        </RequireAuth>
                    }
                />
                <Route
                    path="/admin"
                    element={
                        <RequireAuth>
                            <AdminPage />
                        </RequireAuth>
                    }
                />
            </Routes>
        </BrowserRouter>
    );
}
