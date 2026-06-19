import { isAxiosError } from "axios";

/** Extracts a user-facing message from an API/Axios error, with a fallback. */
export function getApiErrorMessage(error: unknown, fallback: string): string {
  if (isAxiosError(error)) {
    const data = error.response?.data as { message?: string } | undefined;
    if (data?.message) {
      return data.message;
    }
    if (error.code === "ECONNABORTED" || !error.response) {
      return "Não foi possível conectar ao servidor. Verifique sua conexão.";
    }
  }
  return fallback;
}
