import api from "../axios";
import type { MetaRequest, MetaResponse } from "../../types/meta";

export async function updateMeta(id: number, request: MetaRequest): Promise<MetaResponse> {
  try {
    const response = await api.put<MetaResponse>(`/metas/${id}`, request);
    return response.data;
  } catch (error) {
    console.error("Erro ao atualizar meta:", error);
    throw error;
  }
}
