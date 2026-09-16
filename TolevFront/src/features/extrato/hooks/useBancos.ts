import { useQuery } from "@tanstack/react-query";
import { getBancos } from "../../../api/banco/get-bancos";
import { bancoKeys } from "./extratoKeys";

/** O catálogo do seletor de banco. Muda uma vez por ano, então cacheia longe. */
export function useBancos() {
  const query = useQuery({
    queryKey: bancoKeys.all,
    queryFn: getBancos,
    staleTime: 60 * 60 * 1000,
    retry: false,
  });

  return { ...query, bancos: query.data ?? [] };
}
