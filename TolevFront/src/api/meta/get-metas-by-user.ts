import api from "../axios";
import type { MetaResponse } from "../../types/meta";

export async function getMetasByUser(idUsuario: number): Promise<MetaResponse[]> {
  try {
    const response = await api.get<MetaResponse[]>("/metas", {
      params: { idUsuario },
    });
    return response.data;
  } catch (error) {
    console.error("Erro ao buscar metas:", error);
    throw error;
  }
}
