import api from "../axios";

export async function deleteMeta(id: number): Promise<void> {
  try {
    await api.delete(`/metas/${id}`);
  } catch (error) {
    console.error("Erro ao excluir meta:", error);
    throw error;
  }
}
