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

// SecureStore não existe na web, então lá caímos no localStorage.
// (Passar `storage: undefined` NÃO resolve: no zustand v5 isso sobrescreve o
// storage padrão e faz o middleware persist não anexar a API `.persist`.)
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
        // Rearma o interceptor do axios com o token persistido na abertura.
        // NÃO referencie `useAuthStore` aqui: na web este callback roda de
        // forma síncrona durante a criação da store (localStorage), antes de o
        // export existir (temporal dead zone). O fim da hidratação é
        // acompanhado no componente via `persist.onFinishHydration`.
        setAuthToken(state?.token ?? null);
      },
    },
  ),
);
