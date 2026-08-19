import { create } from "zustand";
import { persist } from "zustand/middleware";
import type { User } from "../types/api";

interface AuthState {
  accessToken: string | null;
  user: User | null;
  setSession: (accessToken: string, user: User) => void;
  clearSession: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      accessToken: null,
      user: null,
      setSession: (accessToken, user) => {
        set({ accessToken, user });
      },
      clearSession: () => {
        set({ accessToken: null, user: null });
      },
    }),
    {
      name: "exacta.auth",
      partialize: (state) => ({
        accessToken: state.accessToken,
        user: state.user,
      }),
    },
  ),
);
