/**
 * Shared Dívida (debt) domain types. Mirrors the backend `progression` debt
 * module (DividaRequest / DividaResponse).
 */

export const TIPO_DIVIDA = [
  "CARTAO",
  "EMPRESTIMO",
  "FINANCIAMENTO",
  "CHEQUE_ESPECIAL",
  "CARNE",
  "OUTROS",
] as const;
export type TipoDivida = (typeof TIPO_DIVIDA)[number];

export const STATUS_PARCELA = ["PENDENTE", "PAGA", "ATRASADA", "CANCELADA"] as const;
export type StatusParcela = (typeof STATUS_PARCELA)[number];

/** One installment of a debt, as returned inside DividaResponse. */
export interface ParcelaResponse {
  id: number;
  numeroParcela: number;
  valorTotal: number;
  status: StatusParcela;
  dataVencimento: string | null;
  dataPagamento: string | null;
}

/** POST /dividas/pagamento — pays specific installments (any order, many at once). */
export interface RegistrarPagamentoRequest {
  idDivida: number;
  parcelas: number[];
  valorPorParcela: number;
}

/** POST/PUT /dividas. */
export interface DividaRequest {
  idUsuario: number;
  nome: string;
  banco: string;
  tipo: TipoDivida;
  saldo: number;
  juros: number; // taxa mensal em %
  parcelaMinima: number;
  pesoEmocional: number; // 1..5, usado no método Tsunami
  quantidadeParcelas: number; // número total de parcelas
}

/** GET /dividas. */
export interface DividaResponse {
  id: number;
  idUsuario: number;
  nome: string;
  banco: string;
  tipo: TipoDivida | null;
  saldo: number;
  juros: number;
  parcelaMinima: number;
  pesoEmocional: number;
  quantidadeParcelas: number | null;
  parcelas: ParcelaResponse[] | null;
}
