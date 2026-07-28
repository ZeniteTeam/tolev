import {
  AlertTriangle,
  Car,
  CreditCard,
  Landmark,
  MoreHorizontal,
  Smartphone,
  type LucideIcon,
} from "lucide-react-native";
import type { TipoDivida } from "../../../types/divida";

/** View-model for a debt as consumed by the Dívidas screens. */
export type DividaView = {
  id: number | string;
  nome: string;
  banco: string;
  bankColor: string;
  saldo: number;
  juros: number; // % a.m.
  min: number;
  emocional: number; // 1..5
  parcelas: number; // número total de parcelas
  parcelasPagas: number[]; // números das parcelas já quitadas
  icon: LucideIcon;
  tipo: TipoDivida;
};

export const TIPO_ICON: Record<TipoDivida, LucideIcon> = {
  CARTAO: CreditCard,
  EMPRESTIMO: Landmark,
  FINANCIAMENTO: Car,
  CHEQUE_ESPECIAL: AlertTriangle,
  CARNE: Smartphone,
  OUTROS: MoreHorizontal,
};

/** Brand colors keyed by (lowercased) bank name, matching BankFilter. */
export const BANK_COLOR: Record<string, string> = {
  nubank: "#820AD1",
  itau: "#EC7000",
  "itaú": "#EC7000",
  bradesco: "#CC092F",
  santander: "#EC0000",
  bb: "#FFEF38",
  caixa: "#0070AF",
  inter: "#FF7A00",
  c6: "#111111",
  picpay: "#11C76F",
};

export function bankColor(banco: string): string {
  return BANK_COLOR[banco.trim().toLowerCase()] ?? "#03643F";
}

export const brl = (n: number) => "R$ " + n.toLocaleString("pt-BR");

/** Percentual quitado (ascendente) com base nas parcelas pagas. 0..100. */
export function pctQuitado(d: DividaView): number {
  if (d.parcelas <= 0) return d.saldo <= 0 ? 100 : 0;
  return Math.min(100, Math.round((d.parcelasPagas.length / d.parcelas) * 100));
}

/** Uma dívida está quitada quando não há saldo ou todas as parcelas foram pagas. */
export function isQuitada(d: DividaView): boolean {
  return d.saldo <= 0 || (d.parcelas > 0 && d.parcelasPagas.length >= d.parcelas);
}

/**
 * Seed used until the /dividas backend exists. Also feeds the Projeções and
 * Planejamento method-ordering previews. Total = R$ 30.000.
 */
export const DIVIDAS_SEED: DividaView[] = [
  { id: "cartao", nome: "Cartão Nubank", banco: "Nubank", bankColor: "#820AD1", saldo: 8400, juros: 13.9, min: 620, emocional: 5, parcelas: 14, parcelasPagas: [], icon: CreditCard, tipo: "CARTAO" },
  { id: "consignado", nome: "Empréstimo pessoal", banco: "Itaú", bankColor: "#EC7000", saldo: 12500, juros: 4.2, min: 780, emocional: 3, parcelas: 18, parcelasPagas: [], icon: Landmark, tipo: "EMPRESTIMO" },
  { id: "carro", nome: "Financiamento carro", banco: "Bradesco", bankColor: "#CC092F", saldo: 6800, juros: 1.9, min: 540, emocional: 2, parcelas: 24, parcelasPagas: [], icon: Car, tipo: "FINANCIAMENTO" },
  { id: "cheque", nome: "Cheque especial", banco: "Santander", bankColor: "#EC0000", saldo: 2300, juros: 8.5, min: 210, emocional: 4, parcelas: 12, parcelasPagas: [], icon: AlertTriangle, tipo: "CHEQUE_ESPECIAL" },
];
