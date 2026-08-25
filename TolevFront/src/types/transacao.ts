/**
 * Espelha o módulo `finance` do backend
 * (TransactionRequest / TransactionResponse / CategoryResponse).
 */

/**
 * TRANSFERENCIA existe no backend mas não é lançável à mão: a entidade não tem
 * conta de destino, então não há como representar o outro lado.
 */
export const TIPO_TRANSACAO = ["RECEITA", "DESPESA"] as const;
export type TipoTransacao = (typeof TIPO_TRANSACAO)[number];

export const METODO_PAGAMENTO = [
  "PIX",
  "CARTAO_CREDITO",
  "CARTAO_DEBITO",
  "BOLETO",
  "TED",
] as const;
export type MetodoPagamento = (typeof METODO_PAGAMENTO)[number];

/** De qual tabela a categoria veio — decide em qual FK ela é gravada. */
export const ORIGEM_CATEGORIA = ["SISTEMA", "USUARIO"] as const;
export type OrigemCategoria = (typeof ORIGEM_CATEGORIA)[number];

export type TipoCategoriaGasto = "RECEITA" | "DESPESA";

/** GET /categories?idUsuario= — catálogo do sistema + categorias do usuário. */
export interface CategoriaResponse {
  id: number;
  origem: OrigemCategoria;
  nome: string;
  cor: string | null;
  tipo: TipoCategoriaGasto | null;
}

/** GET /accounts?idUsuario= — só o que o seletor de conta precisa. */
export interface ContaResponse {
  id: number;
  idUsuario: number;
  idBanco: number | null;
  tituloBanco: string | null;
  numeroConta: string | null;
  nomeConta: string | null;
  tipoConta: "CORRENTE" | "POUPANCA" | "SALARIO" | "INVESTIMENTO" | null;
  moeda: "BRL" | "USD" | "EUR" | null;
  saldoAtual: number | null;
  saldoDisponivel: number | null;
  limiteCredito: number | null;
  statusConta: "ATIVA" | "BLOQUEADA" | "ENCERRADA" | null;
  dataAbertura: string | null;
}

/** POST /transactions. */
export interface TransacaoRequest {
  idUsuario: number;
  /** Opcional: null = dinheiro / carteira, sem conta conectada. */
  idContaBancaria: number | null;
  /** Texto livre do estabelecimento; o backend resolve ou cria o vendedor. */
  nomeVendedor: string | null;
  valor: number;
  dataTransacao: string | null; // ISO yyyy-MM-dd
  tipo: TipoTransacao;
  descricao: string | null;
  parcelado: boolean;
  totalParcelas: number | null;
  numeroParcela: number | null;
  metodoPagamento: MetodoPagamento | null;
  idCategoriaGastoSistema: number | null;
  idCategoriaGastoUsuario: number | null;
}

/** GET /transactions. */
export interface TransacaoResponse {
  id: number;
  idUsuario: number;
  idContaBancaria: number | null;
  idVendedor: number | null;
  nomeVendedor: string | null;
  valor: number;
  dataTransacao: string | null;
  tipo: TipoTransacao | null;
  descricao: string | null;
  descricaoNormalizada: string | null;
  parcelado: boolean | null;
  totalParcelas: number | null;
  numeroParcela: number | null;
  metodoPagamento: MetodoPagamento | null;
  idCategoriaGastoSistema: number | null;
  idCategoriaGastoUsuario: number | null;
  nomeCategoria: string | null;
  corCategoria: string | null;
}
