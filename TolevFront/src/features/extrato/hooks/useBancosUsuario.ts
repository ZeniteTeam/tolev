import { useQuery } from "@tanstack/react-query";
import { getBancosUsuario } from "../../../api/extrato/get-bancos-usuario";
import { useAuthStore } from "../../../store/authStore";
import { extratoKeys } from "./extratoKeys";

/** Os bancos deste usuário: só os que já trouxeram transação de extrato. */
export function useBancosUsuario() {
  const userId = useAuthStore((s) => s.userId);

  const query = useQuery({
    queryKey: extratoKeys.bancosUsuario(userId ?? 0),
    queryFn: () => getBancosUsuario(userId as number),
    enabled: userId != null,
    retry: false,
  });

  return { ...query, bancos: query.data ?? [] };
}
