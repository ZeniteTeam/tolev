import { useQuery } from "@tanstack/react-query";
import { getContas } from "../../../api/conta/get-contas";
import { useAuthStore } from "../../../store/authStore";
import { contaKeys } from "./transacaoKeys";

/**
 * The user's connected accounts. Hoje quase ninguém tem uma — o app ainda não
 * conecta bancos — por isso a tela sempre oferece "Dinheiro / carteira" e
 * trata a lista vazia como caso normal, não como erro.
 */
export function useContas() {
  const userId = useAuthStore((s) => s.userId);
  const query = useQuery({
    queryKey: contaKeys.list(userId ?? 0),
    queryFn: () => getContas(userId as number),
    enabled: userId != null,
    staleTime: 60 * 1000,
    retry: false,
  });

  return { ...query, contas: query.data ?? [] };
}
