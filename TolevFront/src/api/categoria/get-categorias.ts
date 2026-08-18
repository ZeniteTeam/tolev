import api from "../axios";
import type { CategoriaResponse } from "../../types/transacao";

/** System catalogue + the user's own categories. GET /categories?idUsuario= */
export async function getCategorias(idUsuario: number): Promise<CategoriaResponse[]> {
  const response = await api.get<CategoriaResponse[]>("/categories", {
    params: { idUsuario },
  });
  return response.data;
}
