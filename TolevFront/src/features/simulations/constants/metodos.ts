import { CircleDot, TrendingDown, Waves, type LucideIcon } from "lucide-react-native";
import type { DividaView } from "../../debts/constants/dividas";

export type MetodoId = "avalanche" | "snowball" | "tsunami";
export type Criterio = "juros" | "saldo" | "emocional";

export type Metodo = {
  id: MetodoId;
  nome: string;
  icon: LucideIcon;
  color: string;
  tagline: string;
  criterio: Criterio;
  overview: string;
  ordenar: (ds: DividaView[]) => DividaView[];
  pros: string[];
  contras: string[];
  meses: number;
  economia: string;
};

export const METODOS: Metodo[] = [
  {
    id: "avalanche",
    nome: "Avalanche",
    icon: TrendingDown,
    color: "#03643F",
    tagline: "Maiores juros primeiro",
    criterio: "juros",
    overview:
      "Você ataca primeiro a dívida com a maior taxa de juros e paga o mínimo nas outras. É o método que economiza mais dinheiro no total.",
    ordenar: (ds) => [...ds].sort((a, b) => b.juros - a.juros),
    pros: ["Menor custo total em juros", "Quitação matematicamente mais rápida"],
    contras: ["As primeiras vitórias podem demorar", "Exige disciplina e paciência"],
    meses: 11,
    economia: "R$ 1.840",
  },
  {
    id: "snowball",
    nome: "Bola de neve",
    icon: CircleDot,
    color: "#1CA474",
    tagline: "Menores saldos primeiro",
    criterio: "saldo",
    overview:
      "Você quita primeiro a dívida de menor saldo, ganhando vitórias rápidas que mantêm a motivação. Ao quitar uma, o valor rola para a próxima.",
    ordenar: (ds) => [...ds].sort((a, b) => a.saldo - b.saldo),
    pros: ["Vitórias rápidas e motivação alta", "Menos contas para administrar"],
    contras: ["Pode pagar mais juros no total", "Não prioriza as taxas mais altas"],
    meses: 13,
    economia: "R$ 980",
  },
  {
    id: "tsunami",
    nome: "Tsunami",
    icon: Waves,
    color: "#30BCB3",
    tagline: "Peso emocional primeiro",
    criterio: "emocional",
    overview:
      "Você prioriza as dívidas que mais pesam na sua cabeça — as de maior impacto emocional — para aliviar o estresse antes de tudo.",
    ordenar: (ds) => [...ds].sort((a, b) => b.emocional - a.emocional),
    pros: ["Alívio psicológico imediato", "Reduz a ansiedade financeira"],
    contras: ["Nem sempre é o mais econômico", "Ignora juros e saldo"],
    meses: 12,
    economia: "R$ 1.120",
  },
];

export function metodoById(id: MetodoId | string | null | undefined): Metodo {
  return METODOS.find((m) => m.id === id) ?? METODOS[0];
}
