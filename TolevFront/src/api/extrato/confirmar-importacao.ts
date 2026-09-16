import type { ImportacaoExtratoResponse } from "../../types/extrato";
import api from "../axios";

/**
 * Marca no servidor que o usuário viu o resultado.
 *
 * É o que faz o aviso de "extrato pronto" não voltar na próxima abertura do
 * app — sem isso ele viveria só na memória da sessão e se perderia justamente
 * para quem saiu do app durante o processamento.
 */
export async function confirmarImportacao(
  idUsuario: number,
  idImportacao: number,
): Promise<ImportacaoExtratoResponse> {
  const response = await api.post<ImportacaoExtratoResponse>(
    `/transactions/extrato/${idImportacao}/confirmacao`,
    null,
    { params: { idUsuario } },
  );
  return response.data;
}
