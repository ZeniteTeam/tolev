import api from "../axios";
import type { ContaResponse } from "../../types/transacao";

/** The user's connected bank accounts. GET /accounts?idUsuario= */
export async function getContas(idUsuario: number): Promise<ContaResponse[]> {
  const response = await api.get<ContaResponse[]>("/accounts", {
    params: { idUsuario },
  });
  return response.data;
}
