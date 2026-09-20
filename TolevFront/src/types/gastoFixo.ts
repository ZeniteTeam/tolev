/** Espelha os DTOs `GastoFixo` do módulo `finance` no backend. */

export interface GastoFixoResponse {
  id: number;
  idUsuario: number;
  nome: string;
  valor: number;
  /** 1–31, ou null: nem todo gasto fixo tem dia certo. */
  diaVencimento: number | null;
  /**
   * Última vez que a pessoa disse que este valor continua valendo, "yyyy-MM-dd".
   * `null` = nunca. É o que separa um total conferido de um que envelheceu.
   */
  confirmadoEm: string | null;
}

/** POST /gastos-fixos e PUT /gastos-fixos/{id}. */
export interface GastoFixoRequest {
  idUsuario: number;
  nome: string;
  valor: number;
  diaVencimento: number | null;
}
