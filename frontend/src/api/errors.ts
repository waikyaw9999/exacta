import { isAxiosError } from "axios";

interface ApiErrorBody {
  message?: string;
}

export function getApiErrorMessage(error: unknown, fallback: string): string {
  if (isAxiosError(error)) {
    const data = error.response?.data;
    if (data && typeof data === "object" && "message" in data) {
      const message = (data as ApiErrorBody).message;
      if (message && message.trim()) {
        return message;
      }
    }
  }
  if (error instanceof Error && error.message.trim()) {
    return error.message;
  }
  return fallback;
}
