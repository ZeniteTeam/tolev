/**
 * Input masks for the debt form.
 *
 * Money and percentage fields are filled **right to left**, the way banking
 * apps do it: the form stores only the digits the user typed and the last two
 * are always the decimals, so "512" reads as 5,12. Date fields are the
 * opposite — they fill **left to right** into DD/MM/AAAA.
 *
 * Keeping the raw digits in state (instead of the formatted text) is what makes
 * the caret behave: there is nothing to parse back and no cursor to restore.
 */

export function onlyDigits(value: string): string {
  return value.replace(/\D/g, "");
}

/** Digits typed → the number they represent, with the last two as decimals. */
export function digitsToDecimal(digits: string): number {
  const clean = onlyDigits(digits);
  if (!clean) return 0;
  return Number(clean) / 100;
}

/** Number → the digit string that renders it (used to seed an edit form). */
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

/** "512" → "R$ 5,12". Empty input stays empty so the placeholder shows. */
export function maskCurrency(digits: string): string {
  const clean = onlyDigits(digits);
  return clean ? `R$ ${formatDecimal(clean)}` : "";
}

/** "250" → "2,50%". */
export function maskPercent(digits: string): string {
  const clean = onlyDigits(digits);
  return clean ? `${formatDecimal(clean)}%` : "";
}

/** Whole numbers, no separators — installment counts. Drops leading zeros. */
export function maskInteger(value: string): string {
  const clean = onlyDigits(value).replace(/^0+(?=\d)/, "");
  return clean;
}

/** Fills DD/MM/AAAA left to right, inserting the slashes as the user types. */
export function maskDate(value: string): string {
  const d = onlyDigits(value).slice(0, 8);
  if (d.length <= 2) return d;
  if (d.length <= 4) return `${d.slice(0, 2)}/${d.slice(2)}`;
  return `${d.slice(0, 2)}/${d.slice(2, 4)}/${d.slice(4)}`;
}
