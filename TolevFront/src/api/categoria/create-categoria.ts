import api from "../axios";
import type { CategoriaRequest, CategoriaResponse } from "../../types/transacao";

/** Cria uma categoria própria do usuário — o catálogo do sistema é só leitura. */
export async function createCategoria(payload: CategoriaRequest): Promise<CategoriaResponse> {
  const response = await api.post<CategoriaResponse>("/categories", payload);
  return response.data;
}
