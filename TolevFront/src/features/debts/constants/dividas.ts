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
  id: number | string;
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

type SeedInput = Pick<
  DividaView,
  "id" | "nome" | "banco" | "saldo" | "juros" | "min" | "emocional" | "parcelas" | "icon" | "tipo"
>;

/**
 * Completa uma dívida de demonstração com um cronograma coerente (parcela fixa,
 * vencendo mês a mês), para que as telas leiam dela os mesmos campos que leem de
 * uma dívida real.
 */
function seedDivida(s: SeedInput): DividaView {
  const hoje = new Date();
  const cronograma: ParcelaView[] = Array.from({ length: s.parcelas }, (_, i) => {
    const venc = new Date(hoje.getFullYear(), hoje.getMonth() + i + 1, 10);
    const juros = Number(((s.saldo / s.parcelas) * (s.juros / 100)).toFixed(2));
    return {
      numero: i + 1,
      valor: s.min,
      principal: Number((s.min - juros).toFixed(2)),
      juros,
      status: "PENDENTE" as const,
      vencimento: venc.toISOString().slice(0, 10),
      pagamento: null,
    };
  });

  const totalAPagar = Number((s.min * s.parcelas).toFixed(2));
  return {
    ...s,
    bankColor: bankColor(s.banco),
    parcelasPagas: [],
    cronograma,
    totalAPagar,
    totalJuros: Number((totalAPagar - s.saldo).toFixed(2)),
    multaAtraso: 2,
    jurosMora: 1,
    sistema: "PRICE",
    regime: "COMPOSTO",
  };
}

/**
 * Semente usada até o /dividas existir. Também alimenta as prévias de ordenação
 * de Projeções e Planejamento. Total = R$ 30.000.
 */
export const DIVIDAS_SEED: DividaView[] = ([
  { id: "cartao", nome: "Cartão Nubank", banco: "Nubank", saldo: 8400, juros: 13.9, min: 620, emocional: 5, parcelas: 14, icon: CreditCard, tipo: "CARTAO" },
  { id: "consignado", nome: "Empréstimo pessoal", banco: "Itaú", saldo: 12500, juros: 4.2, min: 780, emocional: 3, parcelas: 18, icon: Landmark, tipo: "EMPRESTIMO" },
  { id: "carro", nome: "Financiamento carro", banco: "Bradesco", saldo: 6800, juros: 1.9, min: 540, emocional: 2, parcelas: 24, icon: Car, tipo: "FINANCIAMENTO" },
  { id: "cheque", nome: "Cheque especial", banco: "Santander", saldo: 2300, juros: 8.5, min: 210, emocional: 4, parcelas: 12, icon: AlertTriangle, tipo: "CHEQUE_ESPECIAL" },
] satisfies SeedInput[]).map(seedDivida);
