import { useMutation } from "@tanstack/react-query";
import { register } from "../../../api/auth/register";
import { useAuthStore } from "../../../store/authStore";
import type { RegisterRequest } from "../../../types/auth";

export function useRegister() {
  const setAuth = useAuthStore((s) => s.setAuth);
  return useMutation({
    mutationFn: (request: RegisterRequest) => register(request),
    onSuccess: (data) => {
      setAuth(data.token, data.usuario);
    },
  });
}
