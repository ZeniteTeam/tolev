import {
  Car,
  Gift,
  GraduationCap,
  Heart,
  Home,
  Monitor,
  Plane,
  Target,
  type LucideIcon,
} from "lucide-react-native";
import type { CategoriaMeta } from "../../../types/meta";

/** Maps each backend CategoriaMeta to a label + icon for the UI. */
export const CATEGORIAS: { id: CategoriaMeta; name: string; icon: LucideIcon }[] = [
  { id: "GERAL", name: "Geral", icon: Target },
  { id: "VEICULO", name: "Veículo", icon: Car },
  { id: "CASA", name: "Casa", icon: Home },
  { id: "VIAGEM", name: "Viagem", icon: Plane },
  { id: "TECNOLOGIA", name: "Tecnologia", icon: Monitor },
  { id: "EDUCACAO", name: "Educação", icon: GraduationCap },
  { id: "SAUDE", name: "Saúde", icon: Heart },
  { id: "OUTROS", name: "Outros", icon: Gift },
];

export function categoriaIcon(categoria: CategoriaMeta | null | undefined): LucideIcon {
  return CATEGORIAS.find((c) => c.id === categoria)?.icon ?? Target;
}

export function categoriaLabel(categoria: CategoriaMeta | null | undefined): string {
  return CATEGORIAS.find((c) => c.id === categoria)?.name ?? "Geral";
}
