import {
  Award,
  CreditCard,
  Gift,
  Info,
  Thermometer,
  TrendingUp,
  type LucideIcon,
} from "lucide-react-native";
import { StyleSheet, Text, View } from "react-native";
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
        <View key={g.label} style={{ marginBottom: 22 }}>
          <Text style={styles.groupLabel}>{g.label}</Text>
          <View style={[styles.group, shadows.card]}>
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
    <View style={[styles.row, !last && styles.rowDivider]}>
      <View style={[styles.icon, { backgroundColor: palette.bg }]}>
        <Icon size={18} color={palette.fg} strokeWidth={2} />
      </View>
      <View style={{ flex: 1 }}>
        <Text style={styles.title}>{title}</Text>
        <Text style={styles.sub}>{sub}</Text>
      </View>
      <Text style={styles.time}>{time}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  groupLabel: {
    fontSize: 11,
    color: colors.text.secondary,
    textTransform: "uppercase",
    letterSpacing: 0.6,
    fontFamily: "PlusJakartaSans_600SemiBold",
    marginBottom: 8,
    paddingLeft: 4,
  },
  group: { backgroundColor: "#fff", borderRadius: 16, overflow: "hidden" },
  row: { flexDirection: "row", alignItems: "flex-start", gap: 14, padding: 14 },
  rowDivider: { borderBottomWidth: 1, borderBottomColor: "#F1F5F3" },
  icon: {
    width: 36,
    height: 36,
    borderRadius: 18,
    alignItems: "center",
    justifyContent: "center",
  },
  title: {
    fontFamily: "PlusJakartaSans_600SemiBold",
    fontSize: 14,
    color: colors.text.primary,
    lineHeight: 18,
  },
  sub: {
    fontSize: 12,
    color: colors.text.secondary,
    marginTop: 3,
    lineHeight: 16,
    fontFamily: "PlusJakartaSans_400Regular",
  },
  time: {
    fontSize: 11,
    color: colors.text.secondary,
    paddingTop: 2,
    fontFamily: "PlusJakartaSans_400Regular",
  },
});
