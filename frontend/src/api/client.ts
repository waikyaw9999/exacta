import axios from "axios";
import { useAuthStore } from "../stores/authStore";

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL ?? "/api/v1";

export const apiClient = axios.create({
  baseURL: apiBaseUrl,
  headers: {
    "Content-Type": "application/json",
  },
});

apiClient.interceptors.request.use((config) => {
  const token = useAuthStore.getState().accessToken;
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

apiClient.interceptors.response.use(
  (response) => response,
  (error: unknown) => {
    if (axios.isAxiosError(error)) {
      const status = error.response?.status;
      const url = error.config?.url ?? "";
      const isAuthCall = url.includes("/auth/");
      if (status === 401 && !isAuthCall) {
        useAuthStore.getState().clearSession();
      }
    }
    return Promise.reject(error);
  },
);
