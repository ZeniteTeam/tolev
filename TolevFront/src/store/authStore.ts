import * as SecureStore from "expo-secure-store";
import { Platform } from "react-native";
import { create } from "zustand";
import { createJSONStorage, persist } from "zustand/middleware";
import { setAuthToken } from "../api/axios";
import type { UsuarioResponse } from "../types/auth";

interface AuthState {
  token: string | null;
  userId: number | null;
  user: UsuarioResponse | null;
  isAuthenticated: () => boolean;
  setAuth: (token: string, user: UsuarioResponse) => void;
  clearUser: () => void;
}

// SecureStore is unavailable on web, so fall back to localStorage there.
// (Passing `storage: undefined` is NOT an option: in zustand v5 it overrides
// the default storage and makes the persist middleware skip attaching the
// `.persist` API entirely.)
const secureStorage = createJSONStorage(() =>
  Platform.OS === "web"
    ? window.localStorage
    : {
        getItem: (name: string) => SecureStore.getItemAsync(name),
        setItem: (name: string, value: string) =>
          SecureStore.setItemAsync(name, value),
        removeItem: (name: string) => SecureStore.deleteItemAsync(name),
      },
);

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      token: null,
      userId: null,
      user: null,
      isAuthenticated: () => get().token != null,
      setAuth: (token, user) => {
        setAuthToken(token);
        set({ token, userId: user.id, user });
      },
      clearUser: () => {
        setAuthToken(null);
        set({ token: null, userId: null, user: null });
      },
    }),
    {
      name: "tolev-auth",
      storage: secureStorage,
      partialize: (state) => ({
        token: state.token,
        userId: state.userId,
        user: state.user,
      }),
      onRehydrateStorage: () => (state) => {
        // Re-arm the axios interceptor with the persisted token on app start.
        // Note: must NOT reference `useAuthStore` here — on web this callback
        // runs synchronously during store creation (localStorage), before the
        // export is assigned (temporal dead zone). Hydration completion is
        // tracked in the component via `persist.onFinishHydration`.
        setAuthToken(state?.token ?? null);
      },
    },
  ),
);
