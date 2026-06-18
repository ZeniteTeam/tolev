const BR_DATE = /^(\d{2})\/(\d{2})\/(\d{4})$/;

export function isValidBrDate(value: string): boolean {
  const match = BR_DATE.exec(value);
  if (!match) return false;
  const [, dd, mm, yyyy] = match;
  const day = Number(dd);
  const month = Number(mm);
  const year = Number(yyyy);
  const date = new Date(year, month - 1, day);
  return (
    date.getFullYear() === year &&
    date.getMonth() === month - 1 &&
    date.getDate() === day
  );
}

export function brDateToIso(value: string): string | null {
  const match = BR_DATE.exec(value);
  if (!match) return null;
  const [, dd, mm, yyyy] = match;
  return `${yyyy}-${mm}-${dd}`;
}

export function isoToBrDate(value: string | null | undefined): string {
  if (!value) return "";
  const [yyyy, mm, dd] = value.split("-");
  if (!yyyy || !mm || !dd) return "";
  return `${dd}/${mm}/${yyyy}`;
}

const MONTHS_PT = [
  "Jan",
  "Fev",
  "Mar",
  "Abr",
  "Mai",
  "Jun",
  "Jul",
  "Ago",
  "Set",
  "Out",
  "Nov",
  "Dez",
];

export function isoToMonthYear(value: string | null | undefined): string {
  if (!value) return "";
  const [yyyy, mm] = value.split("-");
  const month = Number(mm);
  if (!yyyy || !month || month < 1 || month > 12) return "";
  return `${MONTHS_PT[month - 1]}/${yyyy}`;
}
