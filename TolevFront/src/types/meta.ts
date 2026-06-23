/**
 * Shared Meta (goal) domain types, mirroring the backend `progression` module:
 *  - MetaRequest / MetaResponse           (progression.application.dto)
 *  - StatusMeta / TipoMeta / CategoriaMeta (progression.internal.enums)
 */

export const STATUS_META = ["ATIVA", "CONCLUIDA", "CANCELADA"] as const;
export type StatusMeta = (typeof STATUS_META)[number];

export const TIPO_META = [
  "ECONOMIA",
  "BEM_ESTAR",
  "INVESTIMENTO",
  "DIVIDA",
  "RESERVA",
  "EDUCACAO",
] as const;
export type TipoMeta = (typeof TIPO_META)[number];

export const CATEGORIA_META = [
  "GERAL",
  "VEICULO",
  "CASA",
  "VIAGEM",
  "TECNOLOGIA",
  "EDUCACAO",
  "SAUDE",
  "OUTROS",
] as const;
export type CategoriaMeta = (typeof CATEGORIA_META)[number];

/** POST/PUT /metas. */
export interface MetaRequest {
  idUsuario: number;
  nomeMeta: string;
  valorMeta: number;
  valorDedicado: number;
  status?: StatusMeta;
  tipo?: TipoMeta;
  categoria?: CategoriaMeta;
  dataLimite?: Date; // ISO date "yyyy-MM-dd"
  recompensa?: string | null;
  motivacaoMeta?: string | null;
}

/** /metas endpoints. */
export interface MetaResponse {
  id: number;
  idUsuario: number;
  nomeMeta: string;
  valorMeta: number;
  valorDedicado: number;
  status: StatusMeta;
  tipo: TipoMeta | null;
  categoria: CategoriaMeta | null;
  dataLimite: string;
  recompensa: string | null;
  motivacaoMeta: string | null;
  progresso: number | null;
  percentualQuitado: number | null;
}
