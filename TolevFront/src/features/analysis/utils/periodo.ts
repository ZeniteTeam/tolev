/** As opções do seletor, e quantos meses cada uma pede ao backend. */
export const OPCOES_PERIODO = ["1m", "3m", "6m", "1a"] as const;

export type OpcaoPeriodo = (typeof OPCOES_PERIODO)[number];

const MESES_POR_OPCAO: Record<OpcaoPeriodo, number> = {
  "1m": 1,
  "3m": 3,
  "6m": 6,
  "1a": 12,
};

export function mesesDe(opcao: OpcaoPeriodo): number {
  return MESES_POR_OPCAO[opcao];
}

export function rotuloPeriodo(meses: number): string {
  if (meses === 1) return "último mês";
  if (meses === 12) return "último ano";
  return `últimos ${meses} meses`;
}

const MES_CURTO = [
  "jan", "fev", "mar", "abr", "mai", "jun",
  "jul", "ago", "set", "out", "nov", "dez",
];

/** "2026-07-02" → "jul/2026". Fatia a string ISO: sem Date, sem fuso. */
function mesAno(iso: string): string {
  const [ano, mes] = iso.split("-");
  return `${MES_CURTO[Number(mes) - 1]}/${ano}`;
}

/**
 * Nomeia a faixa do extrato pelos meses que ela cobre, não pelos dias: quem
 * baixou o extrato sabe de que mês ele é, e "jul/2026" responde isso mais rápido
 * do que "02/07/2026 a 30/07/2026".
 */
export function rotuloFaixa(inicio: string, fim: string): string {
  const de = mesAno(inicio);
  const ate = mesAno(fim);
  return de === ate ? de : `${de} a ${ate}`;
}
