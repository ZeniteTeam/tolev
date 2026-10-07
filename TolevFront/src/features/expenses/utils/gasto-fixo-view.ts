import {
  Car,
  CreditCard,
  Droplet,
  Dumbbell,
  GraduationCap,
  Home,
  Lightbulb,
  MoreHorizontal,
  Phone,
  ShieldCheck,
  ShoppingCart,
  Tv,
  Wifi,
  type LucideIcon,
} from "lucide-react-native";
import type { GastoFixoResponse } from "../../../types/gastoFixo";

/**
 * Ícone a partir do nome que a pessoa digitou.
 *
 * O cadastro pede três campos e nenhum deles é categoria — pedir uma quarta
 * escolha para cada gasto é o que transforma "cadastre seus fixos" numa tarefa
 * de quinze minutos. Adivinhar pelo nome acerta na maioria e erra para um
 * ícone genérico, que não atrapalha ninguém.
 */
const PALAVRAS: { icon: LucideIcon; termos: string[] }[] = [
  { icon: Home, termos: ["aluguel", "casa", "condominio", "condomínio", "moradia", "iptu"] },
  { icon: Wifi, termos: ["internet", "wifi", "wi-fi", "banda larga", "fibra"] },
  { icon: Phone, termos: ["celular", "telefone", "plano", "vivo", "claro", "tim", "oi"] },
  { icon: Tv, termos: ["streaming", "netflix", "spotify", "disney", "hbo", "prime", "tv"] },
  { icon: Dumbbell, termos: ["academia", "gym", "crossfit", "pilates", "musculacao", "musculação"] },
  { icon: ShieldCheck, termos: ["seguro", "plano de saude", "plano de saúde", "saude", "saúde"] },
  { icon: Car, termos: ["carro", "auto", "ipva", "estacionamento", "transporte", "uber"] },
  { icon: Lightbulb, termos: ["luz", "energia", "eletrica", "elétrica", "gas", "gás"] },
  { icon: Droplet, termos: ["agua", "água", "saneamento"] },
  { icon: GraduationCap, termos: ["escola", "faculdade", "curso", "mensalidade", "creche"] },
  { icon: ShoppingCart, termos: ["mercado", "supermercado", "feira"] },
  { icon: CreditCard, termos: ["anuidade", "assinatura", "cartao", "cartão"] },
];

export function iconePara(nome: string): LucideIcon {
  const alvo = nome.trim().toLowerCase();
  for (const { icon, termos } of PALAVRAS) {
    if (termos.some((t) => alvo.includes(t))) return icon;
  }
  return MoreHorizontal;
}

/** "todo dia 10" — vazio quando não há dia, sem inventar um. */
export function rotuloVencimento(dia: number | null): string {
  return dia == null ? "" : `todo dia ${dia}`;
}

/**
 * Confirmado neste mês?
 *
 * A janela é o mês corrente, não "30 dias": gasto fixo é uma coisa mensal, e
 * quem conferiu no dia 2 não deve ser cobrado de novo no dia 31.
 */
export function confirmadoNesteMes(gasto: GastoFixoResponse): boolean {
  if (!gasto.confirmadoEm) return false;
  const hoje = new Date();
  const [ano, mes] = gasto.confirmadoEm.split("-").map(Number);
  return ano === hoje.getFullYear() && mes === hoje.getMonth() + 1;
}

/** Quantos gastos ainda não foram conferidos neste mês. */
export function pendentesDeConfirmacao(gastos: GastoFixoResponse[]): number {
  return gastos.filter((g) => !confirmadoNesteMes(g)).length;
}
