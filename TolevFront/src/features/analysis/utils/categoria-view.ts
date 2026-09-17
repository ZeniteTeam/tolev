import type { CategoriaPonto } from "../../../types/graphs";
import type { Categoria } from "../../menu/components/CategoriaGastos";
import { corDaCategoria, iconeDaCategoria } from "./categoria-icons";

export function toCategoriaView(pontos: CategoriaPonto[]): Categoria[] {
  return pontos.map((p, i) => ({
    label: p.nome,
    value: p.valor,
    color: corDaCategoria(p.cor, i),
    icon: iconeDaCategoria(p.nome),
  }));
}

/**
 * Quanto das despesas da janela já está classificado, em % de transações.
 *
 * Não-classificado é sem categoria OU em "Outros" (o backend já soma os dois em
 * `transacoesAClassificar`) — igual à lista de pendentes logo abaixo do donut.
 */
export function percentualClassificado(
  totalTransacoes: number,
  transacoesAClassificar: number,
): number | null {
  if (totalTransacoes === 0) return null;
  return Math.round(100 - (transacoesAClassificar / totalTransacoes) * 100);
}
