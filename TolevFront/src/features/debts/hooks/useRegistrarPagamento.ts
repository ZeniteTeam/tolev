import { useMutation, useQueryClient } from "@tanstack/react-query";
import { registrarPagamento } from "../../../api/divida/registrar-pagamento";
import type { RegistrarPagamentoRequest } from "../../../types/divida";
import { dividaKeys } from "./dividaKeys";

/** Registers an installment payment for a debt and refreshes the debt data. */
export function useRegistrarPagamento() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: RegistrarPagamentoRequest) => registrarPagamento(payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: dividaKeys.all });
    },
  });
}
