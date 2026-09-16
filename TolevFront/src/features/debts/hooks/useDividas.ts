import { useQuery } from "@tanstack/react-query";
import { getDividasByUser } from "../../../api/divida/get-dividas-by-user";
import { useAuthStore } from "../../../store/authStore";
import { type DividaView } from "../constants/dividas";
import { toDividaView } from "../utils/divida-view";
import { dividaKeys } from "./dividaKeys";

/** As dívidas do usuário, como o backend as devolve. */
export function useDividas() {
  const userId = useAuthStore((s) => s.userId);
  const query = useQuery({
    queryKey: dividaKeys.list(userId ?? 0),
    queryFn: () => getDividasByUser(userId as number),
    enabled: userId != null,
    retry: false,
  });

  const dividas: DividaView[] = (query.data ?? []).map(toDividaView);

  return { ...query, dividas };
}
