import type { CategoriaResponse, TransacaoResponse } from "../../types/transacao";
import api from "../axios";

/**
 * Classifica uma transação já gravada. A origem da categoria decide em qual das
 * duas colunas o id é gravado — o backend recusa as duas preenchidas.
 */
export async function updateTransacaoCategoria(
  id: number,
  categoria: CategoriaResponse,
): Promise<TransacaoResponse> {
  const daSistema = categoria.origem === "SISTEMA";
  const response = await api.patch<TransacaoResponse>(`/transactions/${id}/category`, {
    idCategoriaGastoSistema: daSistema ? categoria.id : null,
    idCategoriaGastoUsuario: daSistema ? null : categoria.id,
  });
  return response.data;
}
