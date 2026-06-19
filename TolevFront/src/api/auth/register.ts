import api from "../axios";
import type { AuthResponse, RegisterRequest } from "../../types/auth";

export async function register(request: RegisterRequest): Promise<AuthResponse> {
  try {
    const response = await api.post<AuthResponse>("/auth/register", request);
    return response.data;
  } catch (error) {
    console.error("Erro ao registrar usuário:", error);
    throw error;
  }
}
