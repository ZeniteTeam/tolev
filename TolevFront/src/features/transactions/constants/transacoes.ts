import {
  Banknote,
  Barcode,
  Book,
  Bus,
  CreditCard,
  Film,
  Heart,
  Home,
  Landmark,
  PiggyBank,
  Receipt,
  Repeat,
  ShoppingBag,
  ShoppingCart,
  Smartphone,
  Sparkles,
  Tag,
  Zap,
  type LucideIcon,
} from "lucide-react-native";
import type { MetodoPagamento } from "../../../types/transacao";

export const METODO_LABEL: Record<MetodoPagamento, string> = {
  PIX: "Pix",
  CARTAO_CREDITO: "Crédito",
  CARTAO_DEBITO: "Débito",
  BOLETO: "Boleto",
  TED: "TED",
};

export const METODO_ICON: Record<MetodoPagamento, LucideIcon> = {
  PIX: Zap,
  CARTAO_CREDITO: CreditCard,
  CARTAO_DEBITO: Smartphone,
  BOLETO: Barcode,
  TED: Landmark,
};

/**
 * Ícone por categoria. O backend devolve nome e cor, mas não ícone — as chaves
 * aqui são exatamente os nomes semeados em V4__seed_categorias_gasto_sistema.
 * Categoria criada pelo usuário não casa com nenhuma e cai no ícone neutro.
 */
const CATEGORIA_ICON: Record<string, LucideIcon> = {
  alimentação: ShoppingCart,
  transporte: Bus,
  moradia: Home,
  saúde: Heart,
  educação: Book,
  lazer: Film,
  compras: ShoppingBag,
  assinaturas: Repeat,
  "contas e serviços": Receipt,
  outros: Tag,
  salário: Banknote,
  "outras receitas": PiggyBank,
};

export function categoriaIcon(nome: string): LucideIcon {
  return CATEGORIA_ICON[nome.trim().toLowerCase()] ?? Sparkles;
}

/** Fallback para categorias sem cor definida no banco. */
export const CATEGORIA_COR_PADRAO = "#6B7280";
