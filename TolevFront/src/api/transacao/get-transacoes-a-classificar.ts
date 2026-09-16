import type { PeriodoGastos } from "../../types/graphs";
import type { TransacaoResponse } from "../../types/transacao";
import api from "../axios";

/**
 * As despesas da janela que pedem categoria: as sem categoria nenhuma e as que
 * caíram em "Outros" — que, com o formulário exigindo categoria para avançar,
 * é o que a pessoa escolhe quando quer decidir depois.
 *
 * Mesma forma de período do gráfico de gastos por categoria, porque os dois
 * aparecem no mesmo card e precisam contar a mesma história.
 */
export async function getTransacoesAClassificar(
  idUsuario: number,
  periodo: PeriodoGastos = { meses: 1 },
  idBanco?: number | null,
): Promise<TransacaoResponse[]> {
  const response = await api.get<TransacaoResponse[]>("/transactions/to-classify", {
    params: {
      idUsuario,
      ...("meses" in periodo
        ? { meses: periodo.meses }
        : { inicio: periodo.inicio, fim: periodo.fim }),
      ...(idBanco != null ? { idBanco } : {}),
    },
  });
  return response.data;
}
