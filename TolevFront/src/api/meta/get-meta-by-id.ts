import api from "../axios";
import type { MetaResponse } from "../../types/meta";

export async function getMetaById(id: number): Promise<MetaResponse> {
  try {
    const response = await api.get<MetaResponse>(`/metas/${id}`);
    return response.data;
  } catch (error) {
    console.error("Erro ao buscar meta:", error);
    throw error;
  }
}
