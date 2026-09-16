import api from "../axios";
import type { CategoriaRequest, CategoriaResponse } from "../../types/transacao";

/** Renomeia ou recolore. O tipo não muda: ver `UpdateUserCategoryService`. */
export async function updateCategoria(
  id: number,
  payload: CategoriaRequest,
): Promise<CategoriaResponse> {
  const response = await api.put<CategoriaResponse>(`/categories/${id}`, payload);
  return response.data;
}
