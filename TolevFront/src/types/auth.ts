/**
 * Espelha o módulo `users` do backend:
 *  - RegisterRequest / LoginRequest   (users.application.dto.request)
 *  - AuthResponse / UsuarioResponse   (users.application.dto.response)
 */

/** Principal objetivo do usuário — enum backend `ObjetivoPrincipal`. */
export const OBJETIVO_PRINCIPAL = [
  "QUITAR_DIVIDAS",
  "ORGANIZAR_ORCAMENTO",
  "CRIAR_RESERVA",
  "CONTROLAR_GASTOS",
  "INVESTIR",
  "EDUCACAO_FINANCEIRA",
] as const;
export type ObjetivoPrincipal = (typeof OBJETIVO_PRINCIPAL)[number];

/** Situação financeira atual — enum backend `SituacaoFinanceira`. */
export const SITUACAO_FINANCEIRA = [
  "ENDIVIDADO",
  "NO_LIMITE",
  "EQUILIBRADO",
  "INVESTINDO",
] as const;
export type SituacaoFinanceira = (typeof SITUACAO_FINANCEIRA)[number];

/** Ocupação / fonte de renda — enum backend `TipoEmprego`. */
export const TIPO_EMPREGO = [
  "CLT",
  "AUTONOMO",
  "EMPRESARIO",
  "SERVIDOR_PUBLICO",
  "ESTUDANTE",
  "APOSENTADO",
  "DESEMPREGADO",
  "OUTRO",
] as const;
export type TipoEmprego = (typeof TIPO_EMPREGO)[number];

/** POST /auth/register. */
export interface RegisterRequest {
  nome?: string | null;
  genero?: string | null;
  dataNascimento?: string | null; // ISO "yyyy-MM-dd"
  objetivoPrincipal?: ObjetivoPrincipal | null;
  situacaoFinanceira?: SituacaoFinanceira | null;
  ocupacao?: TipoEmprego | null;
  /** Obrigatória: o backend rejeita o registro sem ela. */
  rendaMensal: number;
  nomeUsuario: string;
  email: string;
  senha: string;
}

/** POST /auth/login. */
export interface LoginRequest {
  email: string;
  senha: string;
}

/** Usuário autenticado, como vem dentro de AuthResponse. */
export interface UsuarioResponse {
  id: number;
  nome: string | null;
  genero: string | null;
  dataNascimento: string | null;
  objetivoPrincipal: ObjetivoPrincipal | null;
  situacaoFinanceira: SituacaoFinanceira | null;
  ocupacao: TipoEmprego | null;
  nomeUsuario: string;
  email: string;
}

/** /auth endpoints. */
export interface AuthResponse {
  token: string;
  tipo: string; // "Bearer"
  expiraEmMs: number;
  usuario: UsuarioResponse;
}
