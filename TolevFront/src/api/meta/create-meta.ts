import api from "../axios";
import type { MetaRequest, MetaResponse } from "../../types/meta";

export async function createMeta(request: MetaRequest): Promise<MetaResponse> {
  try {
    const response = await api.post<MetaResponse>("/metas", request);
    return response.data;
  } catch (error) {
    console.error("Erro ao criar meta:", error);
    throw error;
  }
}
