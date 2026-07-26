/**
 * Preferências financeiras do usuário. Espelha o contrato do backend
 * (`PreferenciaFinanceiraRequest` / `PreferenciaFinanceiraResponse` no domínio
 * users) e faz a ponte com os ids usados nas telas de simulação.
 */

import type { MetodoId } from "../features/simulations/constants/metodos";

/** Estratégia de quitação de dívidas — valores do enum backend `MetodoQuitacao`. */
export const METODO_QUITACAO = ["AVALANCHE", "SNOWBALL", "TSUNAMI"] as const;
export type MetodoQuitacao = (typeof METODO_QUITACAO)[number];

/** Método de orçamento — valores do enum backend `MetodoOrcamento`. */
export const METODO_ORCAMENTO = ["REGRA_50_30_20", "BASE_ZERO", "ENVELOPES"] as const;
export type MetodoOrcamento = (typeof METODO_ORCAMENTO)[number];

/** Ids de orçamento usados na UI (PlanejamentoTab). */
export type OrcamentoId = "503020" | "zero" | "envelope";

/** GET /users/{id}/preferencias */
export interface PreferenciaFinanceiraResponse {
  idUsuario: number;
  metodoQuitacao: MetodoQuitacao;
  aporteExtraMensal: number;
  metodoOrcamento: MetodoOrcamento;
  rendaMensal: number;
  percFixos: number;
  percDividas: number;
  percLazer: number;
  reservaEmergenciaMeta: number;
}

/** PUT /users/{id}/preferencias — atualização parcial (campos omitidos são preservados). */
export type PreferenciaFinanceiraRequest = Partial<{
  metodoQuitacao: MetodoQuitacao;
  aporteExtraMensal: number;
  metodoOrcamento: MetodoOrcamento;
  rendaMensal: number;
  percFixos: number;
  percDividas: number;
  percLazer: number;
  reservaEmergenciaMeta: number;
}>;

// ----- Ponte entre os ids da UI e os enums do backend -----

const QUITACAO_TO_METODO: Record<MetodoQuitacao, MetodoId> = {
  AVALANCHE: "avalanche",
  SNOWBALL: "snowball",
  TSUNAMI: "tsunami",
};

const METODO_TO_QUITACAO: Record<MetodoId, MetodoQuitacao> = {
  avalanche: "AVALANCHE",
  snowball: "SNOWBALL",
  tsunami: "TSUNAMI",
};

const ORCAMENTO_TO_ID: Record<MetodoOrcamento, OrcamentoId> = {
  REGRA_50_30_20: "503020",
  BASE_ZERO: "zero",
  ENVELOPES: "envelope",
};

const ID_TO_ORCAMENTO: Record<OrcamentoId, MetodoOrcamento> = {
  "503020": "REGRA_50_30_20",
  zero: "BASE_ZERO",
  envelope: "ENVELOPES",
};

export const metodoIdFromQuitacao = (q: MetodoQuitacao): MetodoId => QUITACAO_TO_METODO[q];
export const quitacaoFromMetodoId = (id: MetodoId): MetodoQuitacao => METODO_TO_QUITACAO[id];
export const orcamentoIdFromMetodo = (m: MetodoOrcamento): OrcamentoId => ORCAMENTO_TO_ID[m];
export const metodoOrcamentoFromId = (id: OrcamentoId): MetodoOrcamento => ID_TO_ORCAMENTO[id];
