import { useMutation } from "@tanstack/react-query";
import { login } from "../../../api/auth/login";
import { useAuthStore } from "../../../store/authStore";
import type { LoginRequest } from "../../../types/auth";

/** Authenticates the user and stores the returned token + profile. */
export function useLogin() {
  const setAuth = useAuthStore((s) => s.setAuth);
  return useMutation({
    mutationFn: (request: LoginRequest) => login(request),
    onSuccess: (data) => {
      setAuth(data.token, data.usuario);
    },
  });
}
