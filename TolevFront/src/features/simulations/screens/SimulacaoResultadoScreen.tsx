import { useNavigation, useRoute } from "@react-navigation/native";
import {
  Activity,
  Award,
  Calendar,
  DollarSign,
  TrendingDown,
  TrendingUp,
  type LucideIcon,
} from "lucide-react-native";
import { useState } from "react";
import { Pressable, StyleSheet, Text, View } from "react-native";
import { Button, LineChart, PageTitle, Screen } from "../../../components";
import { colors, shadows } from "../../../theme";

type Cenario = "excelente" | "normal" | "baixo";

type ScenarioConfig = {
  label: string;
  color: string;
  months: number;
  saving: number;
  curve: number[];
  finalDate: string;
  delta: string;
  deltaPositive: boolean;
};

const SCENARIOS: Record<Cenario, ScenarioConfig> = {
  excelente: {
    label: "Excelente",
    color: colors.primary[500],
    months: 5,
    saving: 850,
    curve: [0, 18, 36, 55, 76, 100, 100, 100, 100, 100, 100, 100],
    finalDate: "Out/2026",
    delta: "+2 meses adiantada",
    deltaPositive: true,
  },
  normal: {
    label: "Normal",
    color: colors.teal[500],
    months: 7,
    saving: 500,
    curve: [0, 13, 26, 39, 52, 66, 80, 100, 100, 100, 100, 100],
    finalDate: "Dez/2026",
    delta: "Dentro do prazo",
    deltaPositive: true,
  },
  baixo: {
    label: "Baixo",
    color: colors.coral[500],
    months: 11,
    saving: 280,
    curve: [0, 8, 16, 22, 30, 36, 44, 52, 62, 72, 86, 100],
    finalDate: "Abr/2027",
    delta: "−4 meses atrasada",
    deltaPositive: false,
  },
};

export default function SimulacaoResultadoScreen() {
  const navigation = useNavigation<any>();
  const route = useRoute<any>();
  const filters = route.params?.filters ?? {
    categoria: "Dívidas",
    periodo: "6 meses",
    valor: "R$ 500/mês",
  };
  const [cenario, setCenario] = useState<Cenario>("normal");
  const c = SCENARIOS[cenario];

  return (
    <Screen bottomPad={120}>
      <PageTitle title="Resultado da simulação" sub="Veja como diferentes hábitos afetam seu prazo" />

      <View style={[styles.card, shadows.card]}>
        <Text style={styles.eyebrow}>SEUS FILTROS</Text>
        <View style={{ flexDirection: "row", flexWrap: "wrap", gap: 8 }}>
          <FilterTag>{filters.categoria}</FilterTag>
          <FilterTag>{filters.periodo}</FilterTag>
          <FilterTag>{filters.valor}</FilterTag>
        </View>
      </View>

      <View style={[styles.tabs, shadows.card]}>
        {(Object.keys(SCENARIOS) as Cenario[]).map((k) => {
          const isActive = cenario === k;
          const cfg = SCENARIOS[k];
          const Icon: LucideIcon = k === "excelente" ? TrendingUp : k === "normal" ? Activity : TrendingDown;
          return (
            <Pressable
              key={k}
              onPress={() => setCenario(k)}
              style={[
                styles.tabItem,
                isActive && { backgroundColor: cfg.color },
              ]}
            >
              <Icon size={18} color={isActive ? "#fff" : cfg.color} strokeWidth={2} />
              <Text style={[styles.tabLabel, isActive && { color: "#fff" }]}>{cfg.label}</Text>
            </Pressable>
          );
        })}
      </View>

      <View style={[styles.card, shadows.card]}>
        <View style={{ flexDirection: "row", justifyContent: "space-between", alignItems: "flex-start" }}>
          <View>
            <Text style={styles.muted}>Quitação em</Text>
            <Text style={styles.bigValue}>
              {c.months} <Text style={styles.bigValueUnit}>meses</Text>
            </Text>
            <View style={{ flexDirection: "row", alignItems: "center", gap: 4, marginTop: 6 }}>
              {c.deltaPositive ? (
                <TrendingUp size={14} color={colors.teal[500]} strokeWidth={2} />
              ) : (
                <TrendingDown size={14} color={colors.coral[500]} strokeWidth={2} />
              )}
              <Text style={[styles.delta, { color: c.deltaPositive ? colors.teal[500] : colors.coral[500] }]}>
                {c.delta}
              </Text>
            </View>
          </View>
          <View style={[styles.scenarioBadge, { backgroundColor: c.color }]}>
            <Text style={styles.scenarioBadgeText}>{c.label}</Text>
          </View>
        </View>

        <View style={{ marginTop: 12 }}>
          <LineChart values={c.curve} color={c.color} showGoalLine />
          <View style={{ flexDirection: "row", justifyContent: "space-between", paddingHorizontal: 4 }}>
            <Text style={styles.axis}>0m</Text>
            <Text style={styles.axis}>3m</Text>
            <Text style={styles.axis}>6m</Text>
            <Text style={styles.axis}>9m</Text>
            <Text style={styles.axis}>12m</Text>
          </View>
        </View>
      </View>

      <View style={[styles.card, shadows.card]}>
        <Text style={styles.eyebrow}>O QUE MUDA NESSE CENÁRIO</Text>
        <MetricRow icon={Calendar} label="Conclusão" value={c.finalDate} />
        <MetricRow icon={DollarSign} label="Economia mensal" value={`R$ ${c.saving},00`} />
        <MetricRow icon={Award} label="Pontos ganhos" value={cenario === "excelente" ? "+180" : cenario === "normal" ? "+90" : "+30"} last />
      </View>

      <View style={{ gap: 8 }}>
        <Button variant="primary">Aplicar este plano</Button>
        <Button variant="ghost" onPress={() => navigation.goBack()}>Refazer simulação</Button>
      </View>
    </Screen>
  );
}

function FilterTag({ children }: { children: React.ReactNode }) {
  return (
    <View style={styles.filterTag}>
      <Text style={styles.filterTagText}>{children}</Text>
    </View>
  );
}

function MetricRow({
  icon: Icon,
  label,
  value,
  last,
}: {
  icon: LucideIcon;
  label: string;
  value: string;
  last?: boolean;
}) {
  return (
    <View style={[styles.metricRow, !last && styles.metricRowDivider]}>
      <View style={styles.metricIcon}>
        <Icon size={16} color={colors.primary[700]} strokeWidth={2} />
      </View>
      <Text style={styles.metricLabel}>{label}</Text>
      <Text style={styles.metricValue}>{value}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  card: { backgroundColor: "#fff", borderRadius: 18, padding: 18, marginBottom: 14 },
  eyebrow: {
    fontSize: 11,
    color: colors.text.secondary,
    fontFamily: "PlusJakartaSans_700Bold",
    letterSpacing: 0.5,
    marginBottom: 10,
  },
  filterTag: {
    backgroundColor: colors.primary[100],
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 999,
  },
  filterTagText: {
    color: colors.primary[700],
    fontFamily: "PlusJakartaSans_600SemiBold",
    fontSize: 12,
  },
  tabs: {
    flexDirection: "row",
    backgroundColor: "#fff",
    padding: 6,
    borderRadius: 16,
    marginBottom: 14,
    gap: 8,
  },
  tabItem: {
    flex: 1,
    paddingVertical: 10,
    borderRadius: 12,
    alignItems: "center",
    gap: 4,
  },
  tabLabel: {
    fontSize: 12,
    fontFamily: "PlusJakartaSans_700Bold",
    color: colors.text.secondary,
  },
  muted: { fontSize: 13, color: colors.text.secondary, fontFamily: "PlusJakartaSans_400Regular" },
  bigValue: {
    fontFamily: "PlusJakartaSans_700Bold",
    fontSize: 26,
    color: colors.text.primary,
    marginTop: 4,
  },
  bigValueUnit: { fontSize: 15, color: colors.text.secondary, fontFamily: "PlusJakartaSans_500Medium" },
  delta: { fontSize: 12, fontFamily: "PlusJakartaSans_600SemiBold" },
  scenarioBadge: {
    paddingHorizontal: 14,
    paddingVertical: 8,
    borderRadius: 999,
  },
  scenarioBadgeText: { color: "#fff", fontSize: 13, fontFamily: "PlusJakartaSans_700Bold" },
  axis: { fontSize: 11, color: colors.text.secondary, fontFamily: "PlusJakartaSans_400Regular" },
  metricRow: { flexDirection: "row", alignItems: "center", gap: 12, paddingVertical: 10 },
  metricRowDivider: { borderBottomWidth: 1, borderBottomColor: "#F1F5F3" },
  metricIcon: {
    width: 32,
    height: 32,
    borderRadius: 8,
    backgroundColor: colors.primary[100],
    alignItems: "center",
    justifyContent: "center",
  },
  metricLabel: { flex: 1, fontSize: 13, color: colors.text.secondary, fontFamily: "PlusJakartaSans_400Regular" },
  metricValue: { fontSize: 14, fontFamily: "PlusJakartaSans_700Bold", color: colors.text.primary },
});
