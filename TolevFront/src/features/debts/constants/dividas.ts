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
  StatusDivida,
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
  /** O que o backend diz. Nulo em dívida gravada antes do status existir. */
  status: StatusDivida | null;
};

/** Parcelas em aberto, na ordem de vencimento. */
export function parcelasEmAberto(d: DividaView): ParcelaView[] {
  return d.cronograma.filter((p) => p.status !== "PAGA" && p.status !== "CANCELADA");
}

/**
 * Quanto ainda falta pagar, somando as parcelas em aberto — juros inclusos.
 *
 * É este o número que sai da conta do usuário, não o `saldo`, que é só o
 * principal. R$ 4.000 a 8% a.m. em 12x custam quase R$ 6.000 até o fim, e
 * anunciar os 4.000 faz o app parecer otimista sobre dinheiro que ainda vai
 * ser desembolsado.
 *
 * Dívida sem cronograma gerado cai no principal — é tudo que se sabe dela.
 */
export function totalEmAberto(d: DividaView): number {
  if (d.cronograma.length === 0) return d.saldo;
  return parcelasEmAberto(d).reduce((s, p) => s + p.valor, 0);
}

/**
 * A quebra de {@link totalEmAberto}: quanto ainda é principal e quanto é juros.
 *
 * Sai das parcelas, não de `saldo` e `totalJuros`, para as duas partes somarem
 * exatamente o total mostrado ao lado — leitor que confere não encontra R$ 3
 * de diferença e conclui que o app erra a conta.
 */
export function quebraEmAberto(d: DividaView): { principal: number; juros: number } {
  if (d.cronograma.length === 0) return { principal: d.saldo, juros: 0 };
  return parcelasEmAberto(d).reduce(
    (acc, p) => ({ principal: acc.principal + p.principal, juros: acc.juros + p.juros }),
    { principal: 0, juros: 0 },
  );
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

/**
 * Uma dívida está quitada.
 *
 * O backend marca `PAGA` ao quitar a última parcela, e essa é a resposta que
 * vale. A inferência pelo cronograma fica como retaguarda para a dívida antiga,
 * gravada antes do status existir — sem ela essas linhas ficariam para sempre
 * na seção de abertas.
 */
export function isQuitada(d: DividaView): boolean {
  if (d.status === "PAGA") return true;
  if (d.status != null) return false;
  return d.saldo <= 0 || (d.parcelas > 0 && d.parcelasPagas.length >= d.parcelas);
}
