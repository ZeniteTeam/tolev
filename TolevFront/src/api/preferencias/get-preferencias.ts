import api from "../axios";
import type { PreferenciaFinanceiraResponse } from "../../types/preferencias";

export async function getPreferencias(
  idUsuario: number,
): Promise<PreferenciaFinanceiraResponse> {
  const response = await api.get<PreferenciaFinanceiraResponse>(
    `/users/${idUsuario}/preferencias`,
  );
  return response.data;
}
