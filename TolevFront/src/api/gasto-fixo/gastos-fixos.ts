import type { GastoFixoRequest, GastoFixoResponse } from "../../types/gastoFixo";
import api from "../axios";

/** Os gastos fixos vivos do usuário, já na ordem em que caem no mês. */
export async function getGastosFixos(idUsuario: number): Promise<GastoFixoResponse[]> {
  try {
    const response = await api.get<GastoFixoResponse[]>("/gastos-fixos", {
      params: { idUsuario },
    });
    return response.data;
  } catch (error) {
    console.error("Erro ao buscar gastos fixos:", error);
    throw error;
  }
}

export async function createGastoFixo(payload: GastoFixoRequest): Promise<GastoFixoResponse> {
  try {
    const response = await api.post<GastoFixoResponse>("/gastos-fixos", payload);
    return response.data;
  } catch (error) {
    console.error("Erro ao criar gasto fixo:", error);
    throw error;
  }
}

export async function updateGastoFixo(
  id: number,
  payload: GastoFixoRequest,
): Promise<GastoFixoResponse> {
  try {
    const response = await api.put<GastoFixoResponse>(`/gastos-fixos/${id}`, payload, {
      params: { idUsuario: payload.idUsuario },
    });
    return response.data;
  } catch (error) {
    console.error("Erro ao atualizar gasto fixo:", error);
    throw error;
  }
}

/** Desativa. A linha fica no banco, para o histórico de valores sobreviver. */
export async function deleteGastoFixo(id: number, idUsuario: number): Promise<void> {
  try {
    await api.delete(`/gastos-fixos/${id}`, { params: { idUsuario } });
  } catch (error) {
    console.error("Erro ao remover gasto fixo:", error);
    throw error;
  }
}

/** "Continua tudo assim" — carimba todos os gastos vivos com a data de hoje. */
export async function confirmarGastosFixos(idUsuario: number): Promise<void> {
  try {
    await api.post("/gastos-fixos/confirmacao", null, { params: { idUsuario } });
  } catch (error) {
    console.error("Erro ao confirmar gastos fixos:", error);
    throw error;
  }
}
