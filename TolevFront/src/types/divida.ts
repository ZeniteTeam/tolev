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

/** PRICE = parcela fixa; SAC = amortização fixa, parcela decrescente. */
export const SISTEMA_AMORTIZACAO = ["PRICE", "SAC"] as const;
export type SistemaAmortizacao = (typeof SISTEMA_AMORTIZACAO)[number];

/** SIMPLES = juros sobre o valor original; COMPOSTO = juros sobre o saldo devedor. */
export const REGIME_JUROS = ["SIMPLES", "COMPOSTO"] as const;
export type RegimeJuros = (typeof REGIME_JUROS)[number];

/** One installment of a debt, as returned inside DividaResponse. */
export interface ParcelaResponse {
  id: number;
  numeroParcela: number;
  valorTotal: number;
  /** Parte que abate o saldo devedor. */
  valorPrincipal: number | null;
  /** Parte que é juros. */
  valorJuros: number | null;
  status: StatusParcela;
  dataVencimento: string | null;
  dataPagamento: string | null;
}

/**
 * POST /dividas/pagamento — pays specific installments (any order, many at once).
 * Cada parcela leva o próprio valor: num SAC elas têm valores diferentes, e no
 * PRICE a última absorve o arredondamento.
 */
export interface RegistrarPagamentoRequest {
  idDivida: number;
  parcelas: { numero: number; valorPago: number }[];
}

/** POST/PUT /dividas. */
export interface DividaRequest {
  idUsuario: number;
  nome: string;
  banco: string;
  tipo: TipoDivida;
  saldo: number;
  juros: number; // taxa mensal em %
  multaAtraso: number; // % sobre a parcela atrasada
  jurosMora: number; // % a.m. de mora
  pesoEmocional: number; // 1..5, usado no método Tsunami
  quantidadeParcelas: number; // número total de parcelas
  dataLiberacao: string | null; // ISO yyyy-MM-dd
  dataPrimeiroVencimento: string | null; // ISO yyyy-MM-dd
  sistemaAmortizacao: SistemaAmortizacao;
  regimeJuros: RegimeJuros;
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
  multaAtraso: number | null;
  jurosMora: number | null;
  parcelaMinima: number;
  pesoEmocional: number;
  quantidadeParcelas: number | null;
  dataLiberacao: string | null;
  dataPrimeiroVencimento: string | null;
  sistemaAmortizacao: SistemaAmortizacao | null;
  regimeJuros: RegimeJuros | null;
  /** Juros somados de todas as parcelas. */
  totalJuros: number | null;
  /** Saldo + totalJuros. */
  totalAPagar: number | null;
  parcelas: ParcelaResponse[] | null;
}
