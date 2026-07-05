import { LinearGradient } from "expo-linear-gradient";
import { AlertCircle, Smile, Thermometer, Zap } from "lucide-react-native";
import { Text, View } from "react-native";
import {
  Progress,
  ProjectionAreaChart,
  ProjectionBarChart,
  Ring,
} from "../../../components";
import type { ProjectionMonth } from "../../../components/ProjectionBarChart";
import { colors, shadows } from "../../../theme";

const PROJ_VALUES = [5, 13, 22, 30, 40, 52, 65, 78, 92, 100];
const MONTHS: ProjectionMonth[] = [
  { label: "Jun", divida: 26, pagto: 4 },
  { label: "Jul", divida: 22, pagto: 4 },
  { label: "Ago", divida: 18, pagto: 4 },
  { label: "Set", divida: 14, pagto: 4 },
  { label: "Out", divida: 10, pagto: 4 },
  { label: "Nov", divida: 6, pagto: 4 },
];

export default function ProjecoesTab() {
  return (
    <View className="pt-[22px]">
      <LinearGradient
        colors={[colors.primary[700], colors.primary[600]]}
        start={{ x: 0, y: 0 }}
        end={{ x: 1, y: 1 }}
        className="rounded-[18px] p-[22px] mb-3.5"
        style={shadows.card}
      >
        <Text className="text-white/[0.85] text-sm font-semibold">Quitação prevista</Text>
        <Text className="text-white text-[30px] font-bold mt-1.5">Dez/2026</Text>
        <View className="flex-row items-center gap-1.5 mt-1.5">
          <Zap size={14} color={colors.primary[300]} strokeWidth={2} />
          <Text className="text-white/[0.9] text-sm font-regular">
            <Text className="font-bold">Passa voando! </Text>
            Faltam 7 meses
          </Text>
        </View>

        <View className="mt-3.5">
          <ProjectionAreaChart values={PROJ_VALUES} height={64} />
        </View>

        <View className="flex-row flex-wrap gap-y-3.5 mt-3.5 pt-4 border-t border-t-white/[0.18]">
          <Stat label="Dívida total" value="R$ 30.000" sub="a quitar" />
          <Stat label="Pagamento mensal" value="R$ 4.285" sub="previsto" />
          <Stat
            label="Juros economizados"
            value="R$ 1.840"
            sub="vs. plano original"
            valueColor={colors.primary[300]}
          />
          <Stat label="Confiança" value="87%" sub="na previsão" />
        </View>
      </LinearGradient>

      <View className="bg-surface rounded-[18px] p-5 mb-3.5" style={shadows.card}>
        <View className="flex-row items-start gap-3.5 mb-3.5">
          <Ring>
            <Thermometer size={22} color={colors.primary[700]} strokeWidth={2} />
          </Ring>
          <View className="flex-1">
            <Text className="font-bold text-[16px] text-ink">Termômetro da dívida</Text>
            <Text className="text-[12px] text-muted mt-0.5 font-regular">
              Você já percorreu <Text className="text-ink font-bold">40%</Text>
            </Text>
          </View>
        </View>
        <Progress pct={40} height={12} />
        <View className="flex-row justify-between mt-2.5">
          <Text className="text-[12px] text-primary-700 font-semibold">R$ 20.000 pagos</Text>
          <Text className="text-[12px] text-primary-700 font-semibold">R$ 30.000 restantes</Text>
        </View>
      </View>

      <View className="flex-row gap-3 mb-4">
        <View className="flex-1 bg-surface rounded-[18px] p-[18px]" style={shadows.card}>
          <View className="flex-row items-center gap-2 mb-3">
            <Ring style={{ width: 32, height: 32, borderRadius: 16 }}>
              <Smile size={16} color={colors.teal[500]} strokeWidth={2} />
            </Ring>
            <Text className="text-[12px] text-muted font-regular">Livre hoje</Text>
          </View>
          <Text className="font-bold text-[22px] text-teal-500">R$ 84</Text>
          <Text className="text-[11px] text-muted mt-1 font-regular">sem comprometer dívidas</Text>
        </View>
        <View className="flex-1 bg-surface rounded-[18px] p-[18px]" style={shadows.card}>
          <View className="flex-row items-center gap-2 mb-3">
            <View className="w-8 h-8 rounded-full items-center justify-center bg-coral-500/[0.12]">
              <AlertCircle size={16} color={colors.coral[500]} strokeWidth={2} />
            </View>
            <Text className="text-[12px] text-muted font-regular">Comprometido</Text>
          </View>
          <Text className="font-bold text-[22px] text-coral-500">72%</Text>
          <Text className="text-[11px] text-muted mt-1 font-regular">da renda mensal</Text>
        </View>
      </View>

      <View className="bg-surface rounded-[18px] p-5 mb-3.5" style={shadows.card}>
        <Text className="font-bold text-[16px] text-ink">Próximos 6 meses</Text>
        <Text className="text-[12px] text-muted mt-0.5 font-regular mb-[18px]">
          Dívida projetada vs. pagamentos
        </Text>
        <ProjectionBarChart months={MONTHS} height={140} />
        <View className="flex-row gap-4 justify-center mt-3.5">
          <Legend color={colors.primary[500]} label="Dívida restante" />
          <Legend color={colors.coral[500]} label="Pagamento" />
        </View>
      </View>
    </View>
  );
}

function Stat({
  label,
  value,
  sub,
  valueColor,
}: {
  label: string;
  value: string;
  sub: string;
  valueColor?: string;
}) {
  return (
    <View className="w-1/2 pr-3">
      <Text className="text-[11px] text-white/[0.78] mb-1 font-regular">{label}</Text>
      <Text
        className="text-white font-bold text-[16px]"
        style={valueColor ? { color: valueColor } : undefined}
      >
        {value}
      </Text>
      <Text className="text-[11px] text-white/[0.7] mt-0.5 font-regular">{sub}</Text>
    </View>
  );
}

function Legend({ color, label }: { color: string; label: string }) {
  return (
    <View className="flex-row items-center gap-1.5">
      <View className="w-2.5 h-2.5 rounded-[3px]" style={{ backgroundColor: color }} />
      <Text className="text-[12px] text-muted font-regular">{label}</Text>
    </View>
  );
}
