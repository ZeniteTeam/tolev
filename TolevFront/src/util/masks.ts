/**
 * Dinheiro e porcentagem preenchem da **direita para a esquerda**, como em app
 * de banco: o form guarda só os dígitos digitados e os dois últimos são sempre
 * os decimais, então "512" vale 5,12. Data é o contrário — preenche da
 * **esquerda para a direita** em DD/MM/AAAA.
 *
 * Guardar os dígitos crus (e não o texto formatado) é o que faz o cursor se
 * comportar: não há o que reinterpretar nem posição para restaurar.
 */

export function onlyDigits(value: string): string {
  return value.replace(/\D/g, "");
}

export function digitsToDecimal(digits: string): number {
  const clean = onlyDigits(digits);
  if (!clean) return 0;
  return Number(clean) / 100;
}

export function decimalToDigits(value: number | null | undefined): string {
  if (value == null || !Number.isFinite(value) || value === 0) return "";
  return String(Math.round(value * 100));
}

function formatDecimal(digits: string): string {
  return digitsToDecimal(digits).toLocaleString("pt-BR", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  });
}

/** Entrada vazia continua vazia, para o placeholder aparecer. */
export function maskCurrency(digits: string): string {
  const clean = onlyDigits(digits);
  return clean ? `R$ ${formatDecimal(clean)}` : "";
}

export function maskPercent(digits: string): string {
  const clean = onlyDigits(digits);
  return clean ? `${formatDecimal(clean)}%` : "";
}

/** Descarta zeros à esquerda. */
export function maskInteger(value: string): string {
  const clean = onlyDigits(value).replace(/^0+(?=\d)/, "");
  return clean;
}

export function maskDate(value: string): string {
  const d = onlyDigits(value).slice(0, 8);
  if (d.length <= 2) return d;
  if (d.length <= 4) return `${d.slice(0, 2)}/${d.slice(2)}`;
  return `${d.slice(0, 2)}/${d.slice(2, 4)}/${d.slice(4)}`;
}
