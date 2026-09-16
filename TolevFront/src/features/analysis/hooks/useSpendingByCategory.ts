import { useQuery } from "@tanstack/react-query";
import { getSpendingByCategory } from "../../../api/graphs/get-spending-by-category";
import { useAuthStore } from "../../../store/authStore";
import type { PeriodoGastos } from "../../../types/graphs";
import { graphKeys } from "./graphKeys";

/**
 * @param periodo  janela do gráfico; ver {@link PeriodoGastos}
 * @param idBanco  recorte por banco; omitido ou null = todas as despesas
 * @param enabled  desliga a busca sem quebrar a ordem dos hooks — usado por
 *                 {@link useGastosPorCategoria}, que só consulta o período do
 *                 extrato depois de saber que o período escolhido veio vazio
 */
export function useSpendingByCategory(
  periodo: PeriodoGastos = { meses: 1 },
  idBanco?: number | null,
  enabled = true,
) {
  const userId = useAuthStore((s) => s.userId);

  return useQuery({
    queryKey: graphKeys.spendingByCategory(userId ?? 0, periodo, idBanco),
    queryFn: () => getSpendingByCategory(userId as number, periodo, idBanco),
    enabled: enabled && userId != null,
    retry: false,
  });
}
