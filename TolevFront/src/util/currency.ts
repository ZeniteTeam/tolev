/** Parses a BRL-formatted string ("R$ 40.000,50") into a number (40000.5). */
export function parseCurrencyToNumber(value: string): number {
  if (!value) return 0;
  const cleaned = value
    .replace(/[^\d,.-]/g, "")
    .replace(/\./g, "")
    .replace(",", ".");
  const parsed = Number.parseFloat(cleaned);
  return Number.isFinite(parsed) ? parsed : 0;
}

export function formatCurrencyBRL(
  value: number | null | undefined,
  withCents = false,
): string {
  const amount = value ?? 0;
  return amount.toLocaleString("pt-BR", {
    style: "currency",
    currency: "BRL",
    minimumFractionDigits: withCents ? 2 : 0,
    maximumFractionDigits: withCents ? 2 : 0,
  });
}
