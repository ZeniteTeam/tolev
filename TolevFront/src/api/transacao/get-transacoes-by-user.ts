import api from "../axios";
import type { TransacaoResponse } from "../../types/transacao";

/** Lists the user's transactions, newest first. GET /transactions?idUsuario= */
export async function getTransacoesByUser(idUsuario: number): Promise<TransacaoResponse[]> {
  const response = await api.get<TransacaoResponse[]>("/transactions", {
    params: { idUsuario },
  });
  return response.data;
}
