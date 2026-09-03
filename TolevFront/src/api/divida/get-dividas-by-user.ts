import api from "../axios";
import type { DividaResponse } from "../../types/divida";

export async function getDividasByUser(idUsuario: number): Promise<DividaResponse[]> {
  try {
    const response = await api.get<DividaResponse[]>("/dividas", {
      params: { idUsuario },
    });
    return response.data;
  } catch (error) {
    console.error("Erro ao buscar dívidas:", error);
    throw error;
  }
}
