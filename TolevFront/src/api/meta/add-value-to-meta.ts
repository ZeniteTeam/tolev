import api from "../axios";

export interface addValueToMetaRequest {
  id: number;
  value: number;
}

export async function addValueToMeta(request: addValueToMetaRequest) {
  try {
    const response = await api.post(`/metas/add`, request);
    return response.data;
  } catch (error) {
    console.error("Erro ao adicionar valor à meta:", error);
    throw error;
  }
}
