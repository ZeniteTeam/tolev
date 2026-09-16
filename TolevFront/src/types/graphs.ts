/**
 * Espelha o módulo `graphs` do backend (graphs/application/dto/response/*).
 * LocalDate chega como "yyyy-MM-dd" e BigDecimal como número JSON.
 */

export interface CategoriaPonto {
  /**
   * Positivo = categoria do sistema, negativo = categoria do usuário, null =
   * sem categoria. O sinal importa: as duas tabelas do backend têm ids
   * independentes, então o id 3 existe nas duas. Use como identidade, nunca
   * como número.
   */
  idCategoria: number | null;
  nome: string;
  cor: string | null;
  valor: number;
  /** 0–100, já calculado pelo backend com duas casas. */
  percentual: number;
}

/**
 * A janela do gráfico, nas duas formas que o endpoint aceita.
 *
 * `meses` só sabe descrever período que termina hoje. Gasto importado de
 * extrato mora no passado, então a tela precisa poder pedir a faixa exata — e
 * nomeá-la corretamente ("jul/2026") em vez de "últimos 3 meses".
 */
export type PeriodoGastos =
  | { meses: number }
  | { inicio: string; fim: string };

/** GET /graphs/spending-by-category */
export interface SpendingByCategoryResponse {
  idUsuario: number;
  inicio: string;
  fim: string;
  totalDespesas: number;
  /** Despesas da janela — é o denominador do "classificado". */
  totalTransacoes: number;
  /** Quantas dessas não têm categoria nem do sistema nem do usuário. */
  transacoesSemCategoria: number;
  /** Já vem ordenado por `valor`, decrescente. Desenhe na ordem que chegar. */
  pontos: CategoriaPonto[];
}
