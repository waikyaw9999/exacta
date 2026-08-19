import type { User } from "../types/api";

export interface AuthResponse {
  accessToken: string;
  tokenType: string;
  user: User;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
  hourlyRate?: number;
}
