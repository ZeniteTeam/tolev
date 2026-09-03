import api from "../axios";
import type {
  PreferenciaFinanceiraRequest,
  PreferenciaFinanceiraResponse,
} from "../../types/preferencias";

/** Atualização parcial: campo omitido fica como está. */
export async function updatePreferencias(
  idUsuario: number,
  payload: PreferenciaFinanceiraRequest,
): Promise<PreferenciaFinanceiraResponse> {
  const response = await api.put<PreferenciaFinanceiraResponse>(
    `/users/${idUsuario}/preferencias`,
    payload,
  );
  return response.data;
}
