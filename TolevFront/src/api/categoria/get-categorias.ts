import api from "../axios";
import type { CategoriaResponse } from "../../types/transacao";

/** Catálogo do sistema + as categorias criadas pelo próprio usuário. */
export async function getCategorias(idUsuario: number): Promise<CategoriaResponse[]> {
  const response = await api.get<CategoriaResponse[]>("/categories", {
    params: { idUsuario },
  });
  return response.data;
}
