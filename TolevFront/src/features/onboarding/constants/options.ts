import {
  AlertTriangle,
  Briefcase,
  Building2,
  Flame,
  GraduationCap,
  Landmark,
  LineChart,
  MoreHorizontal,
  PiggyBank,
  PieChart,
  Scale,
  Sofa,
  Sprout,
  TrendingUp,
  UserRound,
  Wallet,
  type LucideIcon,
} from "lucide-react-native";
import type {
  ObjetivoPrincipal,
  SituacaoFinanceira,
  TipoEmprego,
} from "../../../types/auth";

export type Option<T extends string> = {
  value: T;
  title: string;
  subtitle?: string;
  icon: LucideIcon;
};

export const OBJETIVO_OPTIONS: Option<ObjetivoPrincipal>[] = [
  { value: "QUITAR_DIVIDAS", title: "Sair das dívidas", subtitle: "Quitar o que devo o quanto antes", icon: Flame },
  { value: "ORGANIZAR_ORCAMENTO", title: "Organizar meu orçamento", subtitle: "Saber pra onde vai meu dinheiro", icon: PieChart },
  { value: "CRIAR_RESERVA", title: "Criar uma reserva", subtitle: "Guardar para emergências", icon: PiggyBank },
  { value: "CONTROLAR_GASTOS", title: "Controlar meus gastos", subtitle: "Parar de gastar demais", icon: Wallet },
  { value: "INVESTIR", title: "Começar a investir", subtitle: "Fazer meu dinheiro render", icon: TrendingUp },
  { value: "EDUCACAO_FINANCEIRA", title: "Aprender sobre finanças", subtitle: "Entender melhor o assunto", icon: GraduationCap },
];

export const SITUACAO_OPTIONS: Option<SituacaoFinanceira>[] = [
  { value: "ENDIVIDADO", title: "Estou endividado", subtitle: "As dívidas estão me sufocando", icon: AlertTriangle },
  { value: "NO_LIMITE", title: "Fecho o mês no zero", subtitle: "Ganho e gasto tudo", icon: Scale },
  { value: "EQUILIBRADO", title: "Consigo poupar um pouco", subtitle: "Sobra algo no fim do mês", icon: Sprout },
  { value: "INVESTINDO", title: "Sobra e eu invisto", subtitle: "Já faço meu dinheiro render", icon: LineChart },
];

export const OCUPACAO_OPTIONS: Option<TipoEmprego>[] = [
  { value: "CLT", title: "Carteira assinada (CLT)", icon: Briefcase },
  { value: "AUTONOMO", title: "Autônomo / Freelancer", icon: UserRound },
  { value: "EMPRESARIO", title: "Tenho meu negócio", icon: Building2 },
  { value: "SERVIDOR_PUBLICO", title: "Servidor público", icon: Landmark },
  { value: "ESTUDANTE", title: "Estudante", icon: GraduationCap },
  { value: "APOSENTADO", title: "Aposentado", icon: Sofa },
  { value: "DESEMPREGADO", title: "Sem renda no momento", icon: AlertTriangle },
  { value: "OUTRO", title: "Outro", icon: MoreHorizontal },
];
