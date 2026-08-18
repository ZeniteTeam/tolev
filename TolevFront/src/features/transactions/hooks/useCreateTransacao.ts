import { useMutation, useQueryClient } from "@tanstack/react-query";
import { createTransacao } from "../../../api/transacao/create-transacao";
import type { TransacaoRequest } from "../../../types/transacao";
import { contaKeys, transacaoKeys } from "./transacaoKeys";

/**
 * Creates a transaction and refreshes the list. Accounts are invalidated too:
 * lançar um gasto move o saldo da conta ligada a ele.
 */
export function useCreateTransacao() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: TransacaoRequest) => createTransacao(payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: transacaoKeys.lists() });
      queryClient.invalidateQueries({ queryKey: contaKeys.all });
    },
  });
}
