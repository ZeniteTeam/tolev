import api from "../axios";
import type { DividaResponse, RegistrarPagamentoRequest } from "../../types/divida";

/** As parcelas podem ser pagas fora de ordem e várias de uma vez. */
export async function registrarPagamento(
  payload: RegistrarPagamentoRequest,
): Promise<DividaResponse> {
  const response = await api.post<DividaResponse>("/dividas/pagamento", payload);
  return response.data;
}
