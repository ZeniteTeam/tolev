import { useMutation, useQueryClient } from "@tanstack/react-query";
import { importExtrato } from "../../../api/extrato/import-extrato";
import { useAuthStore } from "../../../store/authStore";
import type { ArquivoExtrato } from "../../../types/extrato";
import { extratoKeys } from "./extratoKeys";

type Params = { idBanco: number; arquivo: ArquivoExtrato };

/**
 * Sobe o PDF. Retorna assim que o servidor aceita — a leitura continua lá.
 *
 * Não invalida transação nem gráfico aqui de propósito: nada foi gravado ainda.
 * Quem atualiza o app é a confirmação, depois que o extrato termina.
 */
export function useImportarExtrato() {
  const queryClient = useQueryClient();
  const userId = useAuthStore((s) => s.userId);

  return useMutation({
    mutationFn: ({ idBanco, arquivo }: Params) =>
      importExtrato(userId as number, idBanco, arquivo),
    onSuccess: () => {
      // A importação nova precisa entrar na lista para o acompanhamento
      // começar a perguntar por ela.
      queryClient.invalidateQueries({ queryKey: extratoKeys.importacoes(userId ?? 0) });
    },
  });
}
