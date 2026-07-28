import { useNavigation } from "@react-navigation/native";
import { LinearGradient } from "expo-linear-gradient";
import {
  ArrowDownRight,
  ArrowUpRight,
  Award,
  CalendarClock,
  CheckCircle,
  ChevronRight,
  Eye,
  Layers,
  Lightbulb,
  PauseCircle,
  Thermometer,
  TrendingUp,
  type LucideIcon,
} from "lucide-react-native";
import { useState } from "react";
import { Pressable, Text, View } from "react-native";
import { BankFilter, Progress, Ring, Screen } from "../../../components";
import type { BankId } from "../../../components/BankFilter";
import { colors, shadows } from "../../../theme";
import { CategoriaGastosCompact } from "../components/CategoriaGastos";

export default function HomeScreen() {
  const navigation = useNavigation<any>();
  const [bank, setBank] = useState<BankId>("all");

  return (
    <Screen bottomPad={120}>
      <BankFilter active={bank} onChange={setBank} />

      <LinearGradient
        colors={[colors.primary[700], colors.primary[600]]}
        start={{ x: 0, y: 0 }}
        end={{ x: 1, y: 1 }}
        className="rounded-[18px] p-[22px] mb-[18px] mt-1"
        style={shadows.card}
      >
        <View className="flex-row justify-between items-center mb-3.5">
          <Text className="text-white/[0.85] text-sm font-regular">Balanço do mês</Text>
          <Eye size={18} color="#fff" strokeWidth={2} />
        </View>
        <Text className="text-white text-[32px] leading-9 font-bold">R$ 1.240,00</Text>
        <View className="flex-row items-center gap-1.5 mt-2">
          <TrendingUp size={14} color={colors.primary[300]} strokeWidth={2} />
          <Text className="text-white/[0.9] text-sm font-regular">+12% em relação ao mês anterior</Text>
        </View>

        <View className="flex-row mt-[18px] pt-4 border-t border-t-white/[0.18] gap-3.5">
          <View className="flex-1">
            <View className="flex-row items-center gap-1.5">
              <ArrowUpRight size={14} color={colors.primary[300]} strokeWidth={2} />
              <Text className="text-white/[0.82] text-[12px] font-regular">Receitas</Text>
            </View>
            <Text className="text-white text-[17px] font-bold mt-1">R$ 4.500</Text>
          </View>
          <View className="w-px bg-white/[0.18]" />
          <View className="flex-1">
            <View className="flex-row items-center gap-1.5">
              <ArrowDownRight size={14} color={colors.coral[300]} strokeWidth={2} />
              <Text className="text-white/[0.82] text-[12px] font-regular">Despesas</Text>
            </View>
            <Text className="text-white text-[17px] font-bold mt-1">R$ 3.260</Text>
          </View>
        </View>
      </LinearGradient>

      <SectionLink title="Onde seu dinheiro vai" sub="Despesas do mês" onPress={() => navigation.navigate("Financas")}>
        <CategoriaGastosCompact />
      </SectionLink>

      <SectionLink
        title="Visão geral das dívidas"
        sub="4 ativas · R$ 30.000"
        onPress={() => navigation.navigate("Dividas")}
      >
        <View className="flex-row flex-wrap gap-2.5">
          <DebtStat icon={Layers} tint="green" value="4" label="Dívidas ativas" />
          <DebtStat icon={CheckCircle} tint="green" value="3 em dia" label="Situação" sub="1 atrasada" subTint="coral" />
          <DebtStat icon={PauseCircle} tint="coral" value="1" label="Em negociação" />
          <DebtStat icon={CalendarClock} tint="green" value="7 meses" label="Para zerar" />
        </View>
      </SectionLink>

      <SectionLink title="Sua progressão" sub="720 pontos" onPress={() => navigation.navigate("Progresso")}>
        <View className="flex-row items-center gap-3.5">
          <Ring size="lg"><Award size={28} color={colors.primary[700]} strokeWidth={2} /></Ring>
          <View className="flex-1">
            <Text className="text-[12px] text-muted font-regular">Nível</Text>
            <Text className="font-bold text-[17px] text-primary-700 mt-0.5">Resiliente</Text>
            <View className="mt-2.5">
              <Progress pct={72} height={8} />
            </View>
            <Text className="text-[11px] text-muted mt-1.5 font-regular">280 pontos para o próximo nível</Text>
          </View>
        </View>
      </SectionLink>

      <SectionLink title="Termômetro da dívida" sub="40% do caminho" onPress={() => navigation.navigate("Dividas")}>
        <View className="flex-row items-center gap-3.5">
          <Ring size="lg"><Thermometer size={26} color={colors.primary[700]} strokeWidth={2} /></Ring>
          <View className="flex-1">
            <Text className="text-[12px] text-muted font-regular">Faltam para zerar</Text>
            <Text className="font-bold text-[22px] text-coral-500 mt-0.5">7 meses</Text>
            <View className="mt-2.5">
              <Progress pct={40} height={8} />
            </View>
          </View>
        </View>
      </SectionLink>

      <View className="bg-primary-25 rounded-[18px] p-[18px] flex-row gap-3.5 items-start">
        <Ring style={{ backgroundColor: "#fff" }}>
          <Lightbulb size={22} color={colors.primary[700]} strokeWidth={2} />
        </Ring>
        <View className="flex-1">
          <Text className="text-[11px] text-primary-700 font-bold tracking-[0.6px]">DICA TOLEV</Text>
          <Text className="text-[14px] text-ink leading-5 mt-1.5 font-regular">
            Dedicando mais <Text className="text-primary-700 font-bold">R$ 100/mês</Text>, você pode alcançar seus objetivos <Text className="text-coral-500 font-bold">10% mais rápido</Text>.
          </Text>
        </View>
      </View>
    </Screen>
  );
}

function SectionLink({
  title,
  sub,
  onPress,
  children,
}: {
  title: string;
  sub?: string;
  onPress?: () => void;
  children: React.ReactNode;
}) {
  return (
    <Pressable onPress={onPress} className="bg-surface rounded-[18px] p-[18px] mb-3.5" style={shadows.card}>
      <View className="flex-row justify-between items-center mb-4">
        <View>
          <Text className="font-bold text-[16px] text-ink">{title}</Text>
          {sub && <Text className="text-[12px] text-muted mt-0.5 font-regular">{sub}</Text>}
        </View>
        <ChevronRight size={20} color={colors.text.secondary} strokeWidth={2} />
      </View>
      {children}
    </Pressable>
  );
}

function DebtStat({
  icon: Icon,
  tint = "green",
  value,
  label,
  sub,
  subTint = "muted",
}: {
  icon: LucideIcon;
  tint?: "green" | "coral";
  value: string;
  label: string;
  sub?: string;
  subTint?: "coral" | "muted";
}) {
  const iconColor = tint === "coral" ? colors.coral[500] : colors.primary[700];
  const iconBg = tint === "coral" ? "rgba(254,111,80,0.12)" : colors.primary[100];
  const subColor = subTint === "coral" ? colors.coral[500] : colors.text.secondary;
  return (
    <View className="bg-primary-50 rounded-[14px] px-[15px] py-3.5" style={{ width: "48%" }}>
      <View className="w-[30px] h-[30px] rounded-[9px] items-center justify-center mb-2.5" style={{ backgroundColor: iconBg }}>
        <Icon size={16} color={iconColor} strokeWidth={2} />
      </View>
      <Text className="font-bold text-[18px] text-ink leading-5">{value}</Text>
      <Text className="text-[12px] text-muted mt-1 font-regular">{label}</Text>
      {sub && (
        <Text className="text-[11px] font-semibold mt-0.5" style={{ color: subColor }}>
          {sub}
        </Text>
      )}
    </View>
  );
}
