import { useMutation, useQueryClient } from "@tanstack/react-query";
import { desfazerImportacao } from "../../../api/extrato/desfazer-importacao";
import { useAuthStore } from "../../../store/authStore";
import { graphKeys } from "../../analysis/hooks/graphKeys";
import { contaKeys, transacaoKeys } from "../../transactions/hooks/transacaoKeys";
import { extratoKeys } from "./extratoKeys";

/**
 * Desfaz uma importação inteira — a saída para quando o Gemini leu o PDF errado.
 *
 * Apaga as transações daquele upload de uma vez, o que não daria para fazer à
 * mão: não existe exclusão de transação avulsa no app.
 */
export function useDesfazerExtrato() {
  const queryClient = useQueryClient();
  const userId = useAuthStore((s) => s.userId);

  return useMutation({
    mutationFn: (idImportacao: number) =>
      desfazerImportacao(userId as number, idImportacao),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: extratoKeys.all });
      // `all` e não `lists()`: a fila de classificação tem chave própria
      // debaixo de `all`, e invalidar só as listagens a deixava velha — era o
      // extrato entrar e a tela de classificar continuar mostrando a fila antiga.
      queryClient.invalidateQueries({ queryKey: transacaoKeys.all });
      queryClient.invalidateQueries({ queryKey: contaKeys.all });
      queryClient.invalidateQueries({ queryKey: graphKeys.all });
    },
  });
}
