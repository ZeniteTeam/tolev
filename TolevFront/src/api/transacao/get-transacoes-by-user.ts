import api from "../axios";
import type { TransacaoResponse } from "../../types/transacao";

/** Mais recentes primeiro. */
export async function getTransacoesByUser(idUsuario: number): Promise<TransacaoResponse[]> {
  const response = await api.get<TransacaoResponse[]>("/transactions", {
    params: { idUsuario },
  });
  return response.data;
}
