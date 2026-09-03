import { useQuery } from "@tanstack/react-query";
import { getDividasByUser } from "../../../api/divida/get-dividas-by-user";
import { useAuthStore } from "../../../store/authStore";
import { DIVIDAS_SEED, type DividaView } from "../constants/dividas";
import { toDividaView } from "../utils/divida-view";
import { dividaKeys } from "./dividaKeys";

/**
 * As dívidas de semente ficam no topo e o que vem do backend entra depois
 * delas — some quando a semente for removida.
 */
export function useDividas() {
  const userId = useAuthStore((s) => s.userId);
  const query = useQuery({
    queryKey: dividaKeys.list(userId ?? 0),
    queryFn: () => getDividasByUser(userId as number),
    enabled: userId != null,
    retry: false,
  });

  const fetched: DividaView[] = (query.data ?? []).map(toDividaView);
  const dividas: DividaView[] = [...DIVIDAS_SEED, ...fetched];

  return { ...query, dividas };
}
