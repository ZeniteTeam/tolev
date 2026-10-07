import type { DebtProjectionResponse } from "../../types/graphs";
import api from "../axios";

/**
 * A projeção de quitação do usuário: data prevista, quanto falta e a descida
 * do saldo mês a mês. Sai do cronograma de parcelas, então acompanha cada
 * pagamento registrado — invalide o cache depois de registrar um.
 */
export async function getDebtProjection(
  idUsuario: number,
): Promise<DebtProjectionResponse> {
  try {
    const response = await api.get<DebtProjectionResponse>("/graphs/debt-projection", {
      params: { idUsuario },
    });
    return response.data;
  } catch (error) {
    console.error("Erro ao buscar projeção de quitação:", error);
    throw error;
  }
}
