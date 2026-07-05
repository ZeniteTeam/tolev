import api from "../axios";
import type { DividaResponse, RegistrarPagamentoRequest } from "../../types/divida";

/**
 * Registers a payment for one or more installments of a debt. Installments may
 * be paid in any order and several at once. POST /dividas/pagamento
 */
export async function registrarPagamento(
  payload: RegistrarPagamentoRequest,
): Promise<DividaResponse> {
  const response = await api.post<DividaResponse>("/dividas/pagamento", payload);
  return response.data;
}
