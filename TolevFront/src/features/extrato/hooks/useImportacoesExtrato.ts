import { useQuery } from "@tanstack/react-query";
import { getImportacoes } from "../../../api/extrato/get-importacoes";
import { useAuthStore } from "../../../store/authStore";
import type { ImportacaoExtratoResponse } from "../../../types/extrato";
import { extratoKeys } from "./extratoKeys";

/** De quanto em quanto tempo perguntamos se o extrato terminou. */
const INTERVALO_MS = 4000;

/**
 * O histórico de importações, relido sozinho enquanto houver uma em andamento.
 *
 * Perguntar de tempos em tempos, e não ser avisado, é escolha consciente: o app
 * não tem push nem websocket, e um extrato leva dezenas de segundos — não horas.
 * Como o estado mora no servidor, isso também funciona depois de o app ter sido
 * fechado no meio do processamento, que é justamente o caso que a espera em
 * segundo plano existe para atender.
 *
 * A pergunta só se repete enquanto vale a pena: assim que nada está processando,
 * o intervalo é desligado e a query volta a ser comum.
 */
export function useImportacoesExtrato() {
  const userId = useAuthStore((s) => s.userId);

  const query = useQuery({
    queryKey: extratoKeys.importacoes(userId ?? 0),
    queryFn: () => getImportacoes(userId as number),
    enabled: userId != null,
    retry: false,
    refetchInterval: (q) =>
      (q.state.data ?? []).some((i) => i.status === "PROCESSANDO") ? INTERVALO_MS : false,
    // Um extrato que terminou enquanto o app estava em segundo plano precisa
    // aparecer assim que ele volta ao primeiro plano.
    refetchIntervalInBackground: false,
  });

  const importacoes: ImportacaoExtratoResponse[] = query.data ?? [];

  return {
    ...query,
    importacoes,
    /** O extrato que está sendo lido agora, se houver. */
    emAndamento: importacoes.find((i) => i.status === "PROCESSANDO") ?? null,
    /**
     * O resultado que o usuário ainda não viu — pronto ou falho. É o que faz o
     * aviso aparecer, e some quando ele confirma.
     */
    aguardandoUsuario:
      importacoes.find((i) => i.status !== "PROCESSANDO" && !i.confirmada) ?? null,
  };
}
