import { useMutation, useQueryClient } from "@tanstack/react-query";
import { updatePreferencias } from "../../../api/preferencias/update-preferencias";
import { useAuthStore } from "../../../store/authStore";
import type {
  PreferenciaFinanceiraRequest,
  PreferenciaFinanceiraResponse,
} from "../../../types/preferencias";
import { preferenciasKeys } from "./preferenciasKeys";

/**
 * Atualiza o cache de forma otimista para a escolha aparecer na hora, e desfaz
 * se a requisição falhar.
 */
export function useUpdatePreferencias() {
  const queryClient = useQueryClient();
  const userId = useAuthStore((s) => s.userId);
  const key = preferenciasKeys.detail(userId ?? 0);

  return useMutation({
    mutationFn: (payload: PreferenciaFinanceiraRequest) =>
      updatePreferencias(userId as number, payload),
    onMutate: async (payload) => {
      await queryClient.cancelQueries({ queryKey: key });
      const previous =
        queryClient.getQueryData<PreferenciaFinanceiraResponse>(key);
      if (previous) {
        queryClient.setQueryData<PreferenciaFinanceiraResponse>(key, {
          ...previous,
          ...payload,
        });
      }
      return { previous };
    },
    onError: (_err, _payload, context) => {
      if (context?.previous) {
        queryClient.setQueryData(key, context.previous);
      }
    },
    onSuccess: (data) => {
      queryClient.setQueryData(key, data);
    },
    onSettled: () => {
      queryClient.invalidateQueries({ queryKey: preferenciasKeys.all });
    },
  });
}
