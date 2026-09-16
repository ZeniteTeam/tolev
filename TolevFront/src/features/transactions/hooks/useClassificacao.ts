import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { getTransacoesAClassificar } from "../../../api/transacao/get-transacoes-a-classificar";
import { updateTransacaoCategoria } from "../../../api/transacao/update-transacao-categoria";
import { useAuthStore } from "../../../store/authStore";
import type { PeriodoGastos } from "../../../types/graphs";
import type { CategoriaResponse } from "../../../types/transacao";
import { graphKeys } from "../../analysis/hooks/graphKeys";
import { transacaoKeys } from "./transacaoKeys";

/**
 * As despesas que pedem categoria na janela dada — sem categoria nenhuma, ou
 * em "Outros".
 *
 * @param periodo a mesma janela que o gráfico de gastos por categoria está
 *                mostrando — inclusive quando ela veio do extrato importado e
 *                não do período escolhido na tela
 */
export function useTransacoesAClassificar(
  periodo: PeriodoGastos,
  idBanco?: number | null,
  enabled = true,
) {
  const userId = useAuthStore((s) => s.userId);

  return useQuery({
    queryKey: transacaoKeys.aClassificar(userId ?? 0, periodo, idBanco),
    queryFn: () => getTransacoesAClassificar(userId as number, periodo, idBanco),
    enabled: enabled && userId != null,
    retry: false,
  });
}

/**
 * Classificar muda o percentual do donut e a fatia de cada categoria, então
 * invalida os gráficos junto — senão o card diria "80% classificado" logo acima
 * de uma lista que acabou de esvaziar.
 */
export function useClassificarTransacao() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, categoria }: { id: number; categoria: CategoriaResponse }) =>
      updateTransacaoCategoria(id, categoria),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: transacaoKeys.all });
      queryClient.invalidateQueries({ queryKey: graphKeys.all });
    },
  });
}
