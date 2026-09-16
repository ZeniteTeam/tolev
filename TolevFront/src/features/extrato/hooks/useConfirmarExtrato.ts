import { useMutation, useQueryClient } from "@tanstack/react-query";
import { confirmarImportacao } from "../../../api/extrato/confirmar-importacao";
import { useAuthStore } from "../../../store/authStore";
import { graphKeys } from "../../analysis/hooks/graphKeys";
import { contaKeys, transacaoKeys } from "../../transactions/hooks/transacaoKeys";
import { extratoKeys } from "./extratoKeys";

/**
 * O "Atualizar" que o usuário toca quando o extrato fica pronto.
 *
 * Faz duas coisas de uma vez, e é por isso que ele existe como passo separado:
 * avisa o servidor que o resultado foi visto (para o aviso não voltar na próxima
 * abertura do app) e recarrega tudo que o extrato mudou. Recarregar sozinho, sem
 * o toque, trocaria os números embaixo de alguém que talvez esteja no meio de
 * outra coisa.
 */
export function useConfirmarExtrato() {
  const queryClient = useQueryClient();
  const userId = useAuthStore((s) => s.userId);

  return useMutation({
    mutationFn: (idImportacao: number) =>
      confirmarImportacao(userId as number, idImportacao),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: extratoKeys.all });
      // `all` e não `lists()`: a fila de classificação tem chave própria
      // debaixo de `all`, e invalidar só as listagens a deixava velha — era o
      // extrato entrar e a tela de classificar continuar mostrando a fila antiga.
      queryClient.invalidateQueries({ queryKey: transacaoKeys.all });
      queryClient.invalidateQueries({ queryKey: contaKeys.all });
      // A análise roda async no servidor, depois do commit da importação. Como
      // o usuário só chega aqui depois de o extrato terminar, o recálculo já
      // teve tempo de acontecer — diferente do lançamento manual, onde o
      // refetch imediato viria vazio.
      queryClient.invalidateQueries({ queryKey: graphKeys.all });
    },
  });
}
