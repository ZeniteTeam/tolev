import { LinearGradient } from "expo-linear-gradient";
import { AlertCircle, Smile, Thermometer, Zap } from "lucide-react-native";
import { Text, View } from "react-native";
import { LineChart, Progress, Ring } from "../../../components";
import { colors, shadows } from "../../../theme";

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
            <Text className="font-bold">Passa voando!{" "}</Text>
            Faltam 7 meses
          </Text>
        </View>

        <View className="mt-3.5">
          <LineChart
            values={[5, 13, 22, 30, 40, 52, 65, 78, 92, 100]}
            height={120}
            color={colors.primary[300]}
            showFill
          />
        </View>

        <View className="flex-row flex-wrap gap-3 mt-3.5 pt-4 border-t border-t-white/[0.18]">
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

      <View className="bg-white rounded-[18px] p-5 mb-3.5" style={shadows.card}>
        <View className="flex-row items-start gap-3.5 mb-3.5">
          <Ring>
            <Thermometer
              size={22}
              color={colors.primary[700]}
              strokeWidth={2}
            />
          </Ring>
          <View className="flex-1">
            <Text className="font-bold text-[16px] text-ink">Termômetro da dívida</Text>
            <Text className="text-[12px] text-muted mt-0.5 font-regular">
              Você já percorreu{" "}
              <Text className="text-ink font-bold">40%</Text>
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
        <View className="flex-1 bg-white rounded-[18px] p-[18px]" style={shadows.card}>
          <View className="flex-row items-center gap-2 mb-3">
            <Ring style={{ width: 32, height: 32, borderRadius: 16 }}>
              <Smile size={16} color={colors.teal[500]} strokeWidth={2} />
            </Ring>
            <Text className="text-[12px] text-muted font-regular">Livre hoje</Text>
          </View>
          <Text className="font-bold text-[22px] text-teal-500">R$ 84</Text>
          <Text className="text-[11px] text-muted mt-1 font-regular">sem comprometer dívidas</Text>
        </View>
        <View className="flex-1 bg-white rounded-[18px] p-[18px]" style={shadows.card}>
          <View className="flex-row items-center gap-2 mb-3">
            <View className="w-8 h-8 rounded-full items-center justify-center bg-coral-500/[0.12]">
              <AlertCircle
                size={16}
                color={colors.coral[500]}
                strokeWidth={2}
              />
            </View>
            <Text className="text-[12px] text-muted font-regular">Comprometido</Text>
          </View>
          <Text className="font-bold text-[22px] text-coral-500">72%</Text>
          <Text className="text-[11px] text-muted mt-1 font-regular">da renda mensal</Text>
        </View>
      </View>

      <View className="bg-white rounded-[18px] p-5 mb-3.5" style={shadows.card}>
        <Text className="font-bold text-[16px] text-ink">Próximos 6 meses</Text>
        <Text className="text-[12px] text-muted mt-0.5 font-regular mb-[18px]">
          Dívida projetada vs. pagamentos
        </Text>
        <ProjectionBars />
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
    <View className="w-[48%]">
      <Text className="text-[11px] text-white/[0.78] mb-1 font-regular">{label}</Text>
      <Text className="text-white font-bold text-[16px]" style={valueColor ? { color: valueColor } : undefined}>
        {value}
      </Text>
      <Text className="text-[11px] text-white/[0.7] mt-0.5 font-regular">{sub}</Text>
    </View>
  );
}

function ProjectionBars() {
  const months = [
    { m: "Jun", divida: 26, pagto: 4 },
    { m: "Jul", divida: 22, pagto: 4 },
    { m: "Ago", divida: 18, pagto: 4 },
    { m: "Set", divida: 14, pagto: 4 },
    { m: "Out", divida: 10, pagto: 4 },
    { m: "Nov", divida: 6, pagto: 4 },
  ];
  const max = 30;
  return (
    <View>
      <View className="flex-row items-end gap-2.5 h-[140px]">
        {months.map(({ m, divida, pagto }) => (
          <View key={m} className="flex-1 items-center justify-end h-full">
            <View className="w-6 h-full justify-end">
              <View
                className="rounded-t-md"
                style={{
                  height: `${(divida / max) * 100}%`,
                  backgroundColor: colors.primary[500],
                }}
              />
              <View
                style={{
                  height: `${(pagto / max) * 100}%`,
                  backgroundColor: colors.coral[500],
                }}
              />
            </View>
          </View>
        ))}
      </View>
      <View className="flex-row gap-2.5 mt-2">
        {months.map(({ m }) => (
          <Text key={m} className="flex-1 text-center text-[11px] text-muted font-regular">
            {m}
          </Text>
        ))}
      </View>
      <View className="flex-row gap-4 justify-center mt-3.5">
        <Legend color={colors.primary[500]} label="Dívida restante" />
        <Legend color={colors.coral[500]} label="Pagamento" />
      </View>
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
