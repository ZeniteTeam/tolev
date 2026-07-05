import api from "../axios";
import type { DividaRequest, DividaResponse } from "../../types/divida";

/** Creates a new debt. POST /dividas */
export async function createDivida(payload: DividaRequest): Promise<DividaResponse> {
  const response = await api.post<DividaResponse>("/dividas", payload);
  return response.data;
}
