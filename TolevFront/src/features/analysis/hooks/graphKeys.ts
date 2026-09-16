import type { PeriodoGastos } from "../../../types/graphs";

/** Uma janela vira sempre a mesma string, para a chave do cache não oscilar. */
export function periodoKey(periodo: PeriodoGastos): string {
  return "meses" in periodo
    ? `meses:${periodo.meses}`
    : `faixa:${periodo.inicio}..${periodo.fim}`;
}

export const graphKeys = {
  all: ["graphs"] as const,
  spendingByCategory: (
    userId: number,
    periodo: PeriodoGastos,
    idBanco?: number | null,
  ) =>
    [
      ...graphKeys.all,
      "spending-by-category",
      userId,
      periodoKey(periodo),
      idBanco ?? "todos",
    ] as const,
};
