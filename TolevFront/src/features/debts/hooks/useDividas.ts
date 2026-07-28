import { useQuery } from "@tanstack/react-query";
import { getDividasByUser } from "../../../api/divida/get-dividas-by-user";
import { useAuthStore } from "../../../store/authStore";
import { DIVIDAS_SEED, type DividaView } from "../constants/dividas";
import { toDividaView } from "../utils/divida-view";
import { dividaKeys } from "./dividaKeys";

/**
 * Lists the debts of the authenticated user. The hard-coded seed debts are
 * kept at the top of the list and any debts returned by the backend are
 * appended after them (to be removed once the seed is dropped).
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
