import * as SecureStore from "expo-secure-store";
import { Platform } from "react-native";
import { create } from "zustand";
import { createJSONStorage, persist } from "zustand/middleware";

interface AuthState {
  userId: number | null;
  setUser: (userId: number) => void;
  clearUser: () => void;
}

const secureStorage = createJSONStorage(() => ({
  getItem: (name: string) => SecureStore.getItemAsync(name),
  setItem: (name: string, value: string) =>
    SecureStore.setItemAsync(name, value),
  removeItem: (name: string) => SecureStore.deleteItemAsync(name),
}));

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      userId: 1,
      setUser: (userId) => set({ userId }),
      clearUser: () => set({ userId: null }),
    }),
    {
      name: "tolev-auth",
      storage: Platform.OS === "web" ? undefined : secureStorage,
    },
  ),
);
