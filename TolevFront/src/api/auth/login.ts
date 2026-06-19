import api from "../axios";
import type { AuthResponse, LoginRequest } from "../../types/auth";

export async function login(request: LoginRequest): Promise<AuthResponse> {
  try {
    const response = await api.post<AuthResponse>("/auth/login", request);
    return response.data;
  } catch (error) {
    console.error("Erro ao fazer login:", error);
    throw error;
  }
}
