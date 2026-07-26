import api from "../axios";
import type {
  PreferenciaFinanceiraRequest,
  PreferenciaFinanceiraResponse,
} from "../../types/preferencias";

/** Atualiza (parcialmente) as preferências financeiras. PUT /users/{id}/preferencias */
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
