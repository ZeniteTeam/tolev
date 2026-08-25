import { useMutation, useQueryClient } from "@tanstack/react-query";
import { createTransacao } from "../../../api/transacao/create-transacao";
import type { TransacaoRequest } from "../../../types/transacao";
import { graphKeys } from "../../analysis/hooks/graphKeys";
import { contaKeys, transacaoKeys } from "./transacaoKeys";

/** Conta também é invalidada: lançar um gasto move o saldo da conta ligada. */
export function useCreateTransacao() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: TransacaoRequest) => createTransacao(payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: transacaoKeys.lists() });
      queryClient.invalidateQueries({ queryKey: contaKeys.all });
      // spending-by-category lê as transações direto, sem passar pelo motor de
      // análise, então invalidar aqui basta. Os cards de nota (score-*) são
      // outra história: lá a análise roda async, depois do commit, e o refetch
      // imediato viria vazio.
      queryClient.invalidateQueries({ queryKey: graphKeys.all });
    },
  });
}
