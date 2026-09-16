import type { ImportacaoExtratoResponse } from "../../types/extrato";
import api from "../axios";

/** GET /transactions/extrato — histórico do usuário, mais recentes primeiro. */
export async function getImportacoes(
  idUsuario: number,
): Promise<ImportacaoExtratoResponse[]> {
  const response = await api.get<ImportacaoExtratoResponse[]>(
    "/transactions/extrato",
    { params: { idUsuario } },
  );
  return response.data;
}
