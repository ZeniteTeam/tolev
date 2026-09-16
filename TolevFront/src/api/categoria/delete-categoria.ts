import api from "../axios";

/**
 * Tira a categoria do catálogo. É baixa lógica no backend: as transações já
 * classificadas nela continuam com o histórico intacto.
 */
export async function deleteCategoria(id: number, idUsuario: number): Promise<void> {
  await api.delete(`/categories/${id}`, { params: { idUsuario } });
}
