import {
  AlertTriangle,
  Car,
  CreditCard,
  Landmark,
  MoreHorizontal,
  Smartphone,
  type LucideIcon,
} from "lucide-react-native";
import type {
  RegimeJuros,
  SistemaAmortizacao,
  StatusParcela,
  TipoDivida,
} from "../../../types/divida";

export type ParcelaView = {
  numero: number;
  valor: number;
  principal: number;
  juros: number;
  status: StatusParcela;
  vencimento: string | null; // ISO
  pagamento: string | null; // ISO
};

export type DividaView = {
  id: number;
  nome: string;
  banco: string;
  bankColor: string;
  saldo: number; // principal em aberto
  juros: number; // % a.m.
  min: number; // valor da 1ª parcela
  emocional: number; // 1..5
  parcelas: number; // número total de parcelas
  parcelasPagas: number[]; // números das parcelas já quitadas
  /** Tabela real vinda do back — fonte da verdade para valores e vencimentos. */
  cronograma: ParcelaView[];
  totalAPagar: number; // soma das parcelas
  totalJuros: number; // totalAPagar − valor contratado
  multaAtraso: number;
  jurosMora: number;
  sistema: SistemaAmortizacao;
  regime: RegimeJuros;
  icon: LucideIcon;
  tipo: TipoDivida;
};

/** Parcelas em aberto, na ordem de vencimento. */
export function parcelasEmAberto(d: DividaView): ParcelaView[] {
  return d.cronograma.filter((p) => p.status !== "PAGA" && p.status !== "CANCELADA");
}

/** Quanto ainda falta pagar, somando as parcelas em aberto. */
export function totalEmAberto(d: DividaView): number {
  return parcelasEmAberto(d).reduce((s, p) => s + p.valor, 0);
}

export const TIPO_ICON: Record<TipoDivida, LucideIcon> = {
  CARTAO: CreditCard,
  EMPRESTIMO: Landmark,
  FINANCIAMENTO: Car,
  CHEQUE_ESPECIAL: AlertTriangle,
  CARNE: Smartphone,
  OUTROS: MoreHorizontal,
};

/** Cor da marca por nome de banco em minúsculas, igual ao BankFilter. */
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

/** "R$ 2.058,90" — mostra centavos só quando existem, nunca "R$ 2.058,9". */
export const brl = (n: number) =>
  "R$ " +
  n.toLocaleString("pt-BR", {
    minimumFractionDigits: Number.isInteger(n) ? 0 : 2,
    maximumFractionDigits: 2,
  });

/** Percentual quitado (ascendente) com base nas parcelas pagas. 0..100. */
export function pctQuitado(d: DividaView): number {
  if (d.parcelas <= 0) return d.saldo <= 0 ? 100 : 0;
  return Math.min(100, Math.round((d.parcelasPagas.length / d.parcelas) * 100));
}

/** Uma dívida está quitada quando não há saldo ou todas as parcelas foram pagas. */
export function isQuitada(d: DividaView): boolean {
  return d.saldo <= 0 || (d.parcelas > 0 && d.parcelasPagas.length >= d.parcelas);
}
