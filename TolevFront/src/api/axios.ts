import axios from "axios";
import Constants from "expo-constants";

const PORT = 8080;

/**
 * Resolves the backend base URL.
 * On a physical device / Android emulator `localhost` points at the device itself,
 * so we derive the dev machine host (LAN IP) from the Expo host URI when available.
 * Falls back to localhost for web / iOS simulator.
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
 * In-memory copy of the JWT, kept in sync by the auth store. The request
 * interceptor needs the token synchronously, so we can't read it from the
 * async SecureStore on every call.
 */
let authToken: string | null = null;

/** Sets (or clears) the bearer token sent on every request. */
export function setAuthToken(token: string | null): void {
  authToken = token;
}

/** Callback invoked when the API rejects the current token (401). */
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
