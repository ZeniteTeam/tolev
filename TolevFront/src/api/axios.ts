import axios from "axios";
import Constants from "expo-constants";

const PORT = 8080;

/**
 * Em aparelho físico / emulador Android, `localhost` aponta para o próprio
 * aparelho — por isso o host da máquina de dev sai do host URI do Expo.
 */
function resolveBaseUrl(): string {
  const hostUri =
    (Constants.expoConfig as { hostUri?: string } | null)?.hostUri ??
    (Constants.expoGoConfig as { debuggerHost?: string } | null)?.debuggerHost;
  const host = hostUri?.split(":")[0];
  return host ? `http://${host}:${PORT}` : `http://localhost:${PORT}`;
}

const api = axios.create({
  baseURL: resolveBaseUrl(),
  headers: { "Content-Type": "application/json" },
  timeout: 15000,
});

/**
 * Cópia do JWT em memória. O interceptor precisa do token de forma síncrona,
 * então não dá para ler do SecureStore (async) a cada request.
 */
let authToken: string | null = null;

export function setAuthToken(token: string | null): void {
  authToken = token;
}

let onUnauthorized: (() => void) | null = null;

export function setOnUnauthorized(handler: (() => void) | null): void {
  onUnauthorized = handler;
}

api.interceptors.request.use((config) => {
  if (authToken) {
    config.headers.Authorization = `Bearer ${authToken}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      onUnauthorized?.();
    }
    return Promise.reject(error);
  },
);

export default api;
