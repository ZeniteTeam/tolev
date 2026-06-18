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

export default api;
