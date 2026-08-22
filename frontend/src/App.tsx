import { Navigate, Route, Routes } from "react-router-dom";
import { ProtectedRoute } from "./components/auth/ProtectedRoute";
import { AppShell } from "./components/layout/AppShell";
import { FloatingTimer } from "./components/timer/FloatingTimer";
import { useAuthHydrated } from "./hooks/useAuthHydrated";
import { useAuthStore } from "./stores/authStore";
import DashboardPage from "./pages/DashboardPage";
import LandingPage from "./pages/LandingPage";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import TimeEntriesPage from "./pages/TimeEntriesPage";

export default function App() {
  const hydrated = useAuthHydrated();
  const accessToken = useAuthStore((state) => state.accessToken);

  if (!hydrated) {
    return <div className="min-h-screen bg-[#f7faf8]" />;
  }

  return (
    <>
      <Routes>
        <Route path="/" element={<LandingPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route element={<ProtectedRoute />}>
          <Route path="/app" element={<AppShell />}>
            <Route index element={<DashboardPage />} />
            <Route path="entries" element={<TimeEntriesPage />} />
          </Route>
        </Route>
        <Route path="*" element={<Navigate to={accessToken ? "/app" : "/"} replace />} />
      </Routes>
      {accessToken ? <FloatingTimer /> : null}
    </>
  );
}
