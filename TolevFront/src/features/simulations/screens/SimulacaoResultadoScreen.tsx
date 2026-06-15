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
import { Pressable, Text, View } from "react-native";
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

      <View className="bg-white rounded-[18px] p-[18px] mb-3.5" style={shadows.card}>
        <Text className="text-[11px] text-muted font-bold tracking-[0.5px] mb-2.5">SEUS FILTROS</Text>
        <View className="flex-row flex-wrap gap-2">
          <FilterTag>{filters.categoria}</FilterTag>
          <FilterTag>{filters.periodo}</FilterTag>
          <FilterTag>{filters.valor}</FilterTag>
        </View>
      </View>

      <View className="flex-row bg-white p-1.5 rounded-lg mb-3.5 gap-2" style={shadows.card}>
        {(Object.keys(SCENARIOS) as Cenario[]).map((k) => {
          const isActive = cenario === k;
          const cfg = SCENARIOS[k];
          const Icon: LucideIcon = k === "excelente" ? TrendingUp : k === "normal" ? Activity : TrendingDown;
          return (
            <Pressable
              key={k}
              onPress={() => setCenario(k)}
              className="flex-1 py-2.5 rounded-md items-center gap-1"
              style={isActive ? { backgroundColor: cfg.color } : undefined}
            >
              <Icon size={18} color={isActive ? "#fff" : cfg.color} strokeWidth={2} />
              <Text className="text-[12px] font-bold text-muted" style={isActive ? { color: "#fff" } : undefined}>
                {cfg.label}
              </Text>
            </Pressable>
          );
        })}
      </View>

      <View className="bg-white rounded-[18px] p-[18px] mb-3.5" style={shadows.card}>
        <View className="flex-row justify-between items-start">
          <View>
            <Text className="text-[13px] text-muted font-regular">Quitação em</Text>
            <Text className="font-bold text-[26px] text-ink mt-1">
              {c.months} <Text className="text-[15px] text-muted font-medium">meses</Text>
            </Text>
            <View className="flex-row items-center gap-1 mt-1.5">
              {c.deltaPositive ? (
                <TrendingUp size={14} color={colors.teal[500]} strokeWidth={2} />
              ) : (
                <TrendingDown size={14} color={colors.coral[500]} strokeWidth={2} />
              )}
              <Text className="text-[12px] font-semibold" style={{ color: c.deltaPositive ? colors.teal[500] : colors.coral[500] }}>
                {c.delta}
              </Text>
            </View>
          </View>
          <View className="px-3.5 py-2 rounded-pill" style={{ backgroundColor: c.color }}>
            <Text className="text-white text-[13px] font-bold">{c.label}</Text>
          </View>
        </View>

        <View className="mt-3">
          <LineChart values={c.curve} color={c.color} showGoalLine />
          <View className="flex-row justify-between px-1">
            <Text className="text-[11px] text-muted font-regular">0m</Text>
            <Text className="text-[11px] text-muted font-regular">3m</Text>
            <Text className="text-[11px] text-muted font-regular">6m</Text>
            <Text className="text-[11px] text-muted font-regular">9m</Text>
            <Text className="text-[11px] text-muted font-regular">12m</Text>
          </View>
        </View>
      </View>

      <View className="bg-white rounded-[18px] p-[18px] mb-3.5" style={shadows.card}>
        <Text className="text-[11px] text-muted font-bold tracking-[0.5px] mb-2.5">O QUE MUDA NESSE CENÁRIO</Text>
        <MetricRow icon={Calendar} label="Conclusão" value={c.finalDate} />
        <MetricRow icon={DollarSign} label="Economia mensal" value={`R$ ${c.saving},00`} />
        <MetricRow icon={Award} label="Pontos ganhos" value={cenario === "excelente" ? "+180" : cenario === "normal" ? "+90" : "+30"} last />
      </View>

      <View className="gap-2">
        <Button variant="primary">Aplicar este plano</Button>
        <Button variant="ghost" onPress={() => navigation.goBack()}>Refazer simulação</Button>
      </View>
    </Screen>
  );
}

function FilterTag({ children }: { children: React.ReactNode }) {
  return (
    <View className="bg-primary-100 px-3 py-1.5 rounded-pill">
      <Text className="text-primary-700 font-semibold text-[12px]">{children}</Text>
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
    <View className={`flex-row items-center gap-3 py-2.5 ${!last ? "border-b border-b-[#F1F5F3]" : ""}`}>
      <View className="w-8 h-8 rounded-sm bg-primary-100 items-center justify-center">
        <Icon size={16} color={colors.primary[700]} strokeWidth={2} />
      </View>
      <Text className="flex-1 text-[13px] text-muted font-regular">{label}</Text>
      <Text className="text-[14px] font-bold text-ink">{value}</Text>
    </View>
  );
}
