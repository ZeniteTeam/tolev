import type { PeriodoGastos, SpendingByCategoryResponse } from "../../types/graphs";
import api from "../axios";

/**
 * @param periodo  `{ meses }` conta para trás a partir de hoje; `{ inicio, fim }`
 *                 diz a janela exata. Extrato importado precisa da segunda
 *                 forma: um PDF de julho aberto em setembro não cabe em nenhuma
 *                 contagem de meses sem arrastar junto dois meses vazios.
 * @param idBanco  recorta por procedência: só os gastos que vieram do extrato
 *                 daquele banco. Omitido = todos, inclusive os digitados à mão,
 *                 que não têm banco nenhum.
 */
export async function getSpendingByCategory(
  idUsuario: number,
  periodo: PeriodoGastos = { meses: 1 },
  idBanco?: number | null,
): Promise<SpendingByCategoryResponse> {
  try {
    const response = await api.get<SpendingByCategoryResponse>(
      "/graphs/spending-by-category",
      {
        params: {
          idUsuario,
          ...("meses" in periodo
            ? { meses: periodo.meses }
            : { inicio: periodo.inicio, fim: periodo.fim }),
          ...(idBanco != null ? { idBanco } : {}),
        },
      },
    );
    return response.data;
  } catch (error) {
    console.error("Erro ao buscar gastos por categoria:", error);
    throw error;
  }
}
