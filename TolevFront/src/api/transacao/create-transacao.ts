import api from "../axios";
import type { TransacaoRequest, TransacaoResponse } from "../../types/transacao";

/** Creates a new transaction. POST /transactions */
export async function createTransacao(payload: TransacaoRequest): Promise<TransacaoResponse> {
  const response = await api.post<TransacaoResponse>("/transactions", payload);
  return response.data;
}
