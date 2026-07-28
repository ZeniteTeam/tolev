import api from "../axios";
import type { PreferenciaFinanceiraResponse } from "../../types/preferencias";

/** Busca as preferências financeiras do usuário. GET /users/{id}/preferencias */
export async function getPreferencias(
  idUsuario: number,
): Promise<PreferenciaFinanceiraResponse> {
  const response = await api.get<PreferenciaFinanceiraResponse>(
    `/users/${idUsuario}/preferencias`,
  );
  return response.data;
}
