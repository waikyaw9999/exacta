import { NavLink, Outlet, useNavigate } from "react-router-dom";
import { useAuthStore } from "../../stores/authStore";

const navLinkClass = ({ isActive }: { isActive: boolean }) =>
  `rounded-lg px-3 py-2 text-sm font-medium transition ${
    isActive
      ? "bg-white/10 text-white"
      : "text-slate-400 hover:bg-white/5 hover:text-slate-100"
  }`;

export function AppShell() {
  const user = useAuthStore((state) => state.user);
  const clearSession = useAuthStore((state) => state.clearSession);
  const navigate = useNavigate();

  function handleLogout() {
    clearSession();
    navigate("/login", { replace: true });
  }

  return (
    <div className="min-h-screen pb-56 sm:pb-8">
      <header className="border-b border-white/10 bg-slate-950/80 backdrop-blur">
        <div className="mx-auto flex max-w-6xl items-center justify-between gap-4 px-4 py-3 sm:px-6">
          <div>
            <p className="text-xs font-semibold uppercase tracking-[0.2em] text-blue-300">
              Exacta
            </p>
            <p className="text-sm text-slate-400">Time tracking & billing</p>
          </div>
          <div className="flex items-center gap-3">
            <nav className="flex items-center gap-1" aria-label="Primary">
              <NavLink to="/" className={navLinkClass} end>
                Dashboard
              </NavLink>
              <NavLink to="/entries" className={navLinkClass}>
                Time entries
              </NavLink>
            </nav>
            <div className="hidden items-center gap-3 border-l border-white/10 pl-3 sm:flex">
              <p className="text-sm text-slate-300">{user?.name}</p>
              <button
                type="button"
                onClick={handleLogout}
                className="rounded-lg px-3 py-2 text-sm text-slate-400 transition hover:bg-white/5 hover:text-white"
              >
                Log out
              </button>
            </div>
            <button
              type="button"
              onClick={handleLogout}
              className="rounded-lg px-3 py-2 text-sm text-slate-400 transition hover:bg-white/5 hover:text-white sm:hidden"
            >
              Log out
            </button>
          </div>
        </div>
      </header>
      <main className="mx-auto max-w-6xl px-4 py-8 sm:px-6">
        <Outlet />
      </main>
    </div>
  );
}
