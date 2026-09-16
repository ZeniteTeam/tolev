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

export function percentualClassificado(
  totalTransacoes: number,
  transacoesSemCategoria: number,
): number | null {
  if (totalTransacoes === 0) return null;
  return Math.round(100 - (transacoesSemCategoria / totalTransacoes) * 100);
}
