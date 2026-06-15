import {
  Award,
  CreditCard,
  Gift,
  Info,
  Thermometer,
  TrendingUp,
  type LucideIcon,
} from "lucide-react-native";
import { Text, View } from "react-native";
import { PageTitle, Screen } from "../../../components";
import { colors, shadows } from "../../../theme";

type Tint = "green" | "coral" | "muted";

type NotifItem = {
  icon: LucideIcon;
  tint: Tint;
  title: string;
  sub: string;
  time: string;
};

const GROUPS: { label: string; items: NotifItem[] }[] = [
  {
    label: "Hoje",
    items: [
      { icon: TrendingUp, tint: "green", title: "Você está em dia com suas metas!", sub: "Continue assim para alcançar 70% até o final do mês.", time: "09:14" },
      { icon: Award, tint: "coral", title: "Nova missão disponível", sub: "Pague duas contas em dia esta semana e ganhe +50 pontos.", time: "08:02" },
    ],
  },
  {
    label: "Esta semana",
    items: [
      { icon: CreditCard, tint: "green", title: "Fatura Nubank vence em 3 dias", sub: "R$ 420,00 — para manter seu plano em dia.", time: "Qua" },
      { icon: Gift, tint: "coral", title: "Recompensa desbloqueada", sub: "Você ganhou +2 pontos de resiliência.", time: "Ter" },
      { icon: Thermometer, tint: "green", title: "Sua dívida diminuiu 4% essa semana", sub: "R$ 800,00 a menos para quitar.", time: "Seg" },
    ],
  },
  {
    label: "Anteriores",
    items: [
      { icon: Info, tint: "muted", title: "Nova versão do Tolev disponível", sub: "Toque para atualizar.", time: "14/05" },
    ],
  },
];

export default function NotificacoesScreen() {
  return (
    <Screen bottomPad={64}>
      <PageTitle title="Notificações" sub="Atualizações sobre suas metas e finanças" />

      {GROUPS.map((g) => (
        <View key={g.label} className="mb-[22px]">
          <Text className="text-[11px] text-muted uppercase tracking-[0.6px] font-semibold mb-2 pl-1">{g.label}</Text>
          <View className="bg-white rounded-lg overflow-hidden" style={shadows.card}>
            {g.items.map((n, i) => (
              <NotifRow key={i} {...n} last={i === g.items.length - 1} />
            ))}
          </View>
        </View>
      ))}
    </Screen>
  );
}

function NotifRow({ icon: Icon, tint, title, sub, time, last }: NotifItem & { last: boolean }) {
  const palette =
    tint === "coral"
      ? { bg: "rgba(254,111,80,0.12)", fg: colors.coral[500] }
      : tint === "muted"
      ? { bg: "#F1F5F3", fg: colors.text.secondary }
      : { bg: colors.primary[100], fg: colors.primary[700] };
  return (
    <View className={`flex-row items-start gap-3.5 p-3.5 ${!last ? "border-b border-b-[#F1F5F3]" : ""}`}>
      <View className="w-9 h-9 rounded-full items-center justify-center" style={{ backgroundColor: palette.bg }}>
        <Icon size={18} color={palette.fg} strokeWidth={2} />
      </View>
      <View className="flex-1">
        <Text className="font-semibold text-[14px] text-ink leading-[18px]">{title}</Text>
        <Text className="text-[12px] text-muted mt-[3px] leading-4 font-regular">{sub}</Text>
      </View>
      <Text className="text-[11px] text-muted pt-0.5 font-regular">{time}</Text>
    </View>
  );
}
