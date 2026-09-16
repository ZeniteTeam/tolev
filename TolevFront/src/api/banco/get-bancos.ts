import type { BancoResponse } from "../../types/extrato";
import api from "../axios";

/** GET /banks — catálogo de bancos, para escolher a origem do extrato. */
export async function getBancos(): Promise<BancoResponse[]> {
  const response = await api.get<BancoResponse[]>("/banks");
  return response.data;
}
