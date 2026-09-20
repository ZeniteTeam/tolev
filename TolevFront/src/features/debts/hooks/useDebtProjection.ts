import { useQuery } from "@tanstack/react-query";
import { getDebtProjection } from "../../../api/graphs/get-debt-projection";
import { useAuthStore } from "../../../store/authStore";
import { graphKeys } from "../../analysis/hooks/graphKeys";

/**
 * A projeção de quitação. Mora em `debts` porque é a aba de Projeções que a
 * consome, mas a chave fica no namespace `graphs` — assim qualquer mutação que
 * mexa em dívida invalida todos os gráficos de uma vez.
 */
export function useDebtProjection() {
  const userId = useAuthStore((s) => s.userId);

  return useQuery({
    queryKey: graphKeys.debtProjection(userId ?? 0),
    queryFn: () => getDebtProjection(userId as number),
    enabled: userId != null,
    retry: false,
  });
}
