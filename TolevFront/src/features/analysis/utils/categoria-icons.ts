import {
  Book,
  CreditCard,
  Film,
  Heart,
  Home,
  MoreHorizontal,
  Receipt,
  ShoppingBag,
  ShoppingCart,
  Truck,
  type LucideIcon,
} from "lucide-react-native";

function chave(nome: string): string {
  return nome
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .trim()
    .toLowerCase();
}

const ICONES: Record<string, LucideIcon> = {
  moradia: Home,
  alimentacao: ShoppingCart,
  transporte: Truck,
  saude: Heart,
  educacao: Book,
  lazer: Film,
  compras: ShoppingBag,
  assinaturas: CreditCard,
  "contas e servicos": Receipt,
  outros: MoreHorizontal,
};

/** Categoria criada pelo usuário não está no mapa e cai no ícone neutro. */
export function iconeDaCategoria(nome: string): LucideIcon {
  return ICONES[chave(nome)] ?? MoreHorizontal;
}

const CORES_FALLBACK = [
  "#03643F",
  "#1CA474",
  "#30BCB3",
  "#FE6F50",
  "#FEAC96",
  "#9B6BDF",
  "#3E7BFA",
  "#6B7280",
] as const;

export function corDaCategoria(cor: string | null, index: number): string {
  return cor ?? CORES_FALLBACK[index % CORES_FALLBACK.length];
}
