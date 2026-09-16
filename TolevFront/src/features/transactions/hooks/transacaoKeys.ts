import type { PeriodoGastos } from "../../../types/graphs";
import { periodoKey } from "../../analysis/hooks/graphKeys";

export const transacaoKeys = {
  all: ["transacoes"] as const,
  lists: () => [...transacaoKeys.all, "list"] as const,
  list: (userId: number) => [...transacaoKeys.lists(), userId] as const,
  /** A fila de classificação, por janela e por banco. */
  aClassificar: (userId: number, periodo: PeriodoGastos, idBanco?: number | null) =>
    [...transacaoKeys.all, "a-classificar", userId, periodoKey(periodo), idBanco ?? "todos"] as const,
};

/** Categorias e contas alimentam o formulário, por isso têm chave própria. */
export const categoriaKeys = {
  all: ["categorias"] as const,
  list: (userId: number) => [...categoriaKeys.all, "list", userId] as const,
};

export const contaKeys = {
  all: ["contas"] as const,
  list: (userId: number) => [...contaKeys.all, "list", userId] as const,
};
