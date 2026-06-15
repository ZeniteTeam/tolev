import { LinearGradient } from "expo-linear-gradient";
import { Award, CheckCircle, ChevronRight, Gift, Zap } from "lucide-react-native";
import { Text, View } from "react-native";
import { PageTitle, Progress, Ring, Screen } from "../../../components";
import { colors, shadows } from "../../../theme";

export default function ProgressaoScreen() {
  return (
    <Screen bottomPad={120}>
      <PageTitle title="Sua progressão" sub="Acompanhe missões, recompensas e conquistas" />

      <View className="flex-row gap-3 mb-4">
        <StatCard value="2" title="Missões" sub="disponíveis" />
        <StatCard value="3" title="Pagamentos" sub="pendentes" />
      </View>

      <LinearGradient
        colors={[colors.primary[25], colors.primary[100]]}
        start={{ x: 0, y: 0 }}
        end={{ x: 1, y: 1 }}
        className="rounded-[18px] p-5 flex-row items-center gap-3.5 mb-3.5"
      >
        <Ring size="lg" style={{ backgroundColor: "#fff" }}>
          <CheckCircle size={28} color={colors.primary[700]} strokeWidth={2} />
        </Ring>
        <View className="flex-1">
          <Text className="font-bold text-[17px] text-primary-700">Tudo em dia!</Text>
          <Text className="text-sm text-ink mt-0.5 font-regular">Suas contas desse mês estão organizadas</Text>
        </View>
      </LinearGradient>

      <View className="bg-white rounded-[18px] p-5 mb-3.5" style={shadows.card}>
        <View className="flex-row justify-between items-start">
          <View>
            <Text className="text-[12px] text-muted font-regular">Pontuação Tolev</Text>
            <Text className="font-bold text-[32px] text-primary-700 mt-1">720</Text>
            <Text className="text-[12px] text-teal-500 font-semibold mt-1">+45 pontos esse mês</Text>
          </View>
          <Ring size="lg"><Award size={28} color={colors.primary[700]} strokeWidth={2} /></Ring>
        </View>
        <View className="mt-[18px]">
          <Progress pct={72} height={10} />
        </View>
        <View className="flex-row justify-between mt-2">
          <Text className="text-[12px] text-muted font-regular">Iniciante</Text>
          <Text className="text-[12px] text-teal-500 font-semibold">Resiliente</Text>
          <Text className="text-[12px] text-muted font-regular">Mestre</Text>
        </View>
      </View>

      <View className="bg-white rounded-[18px] p-5 mb-3.5" style={shadows.card}>
        <View className="flex-row items-center gap-3.5 mb-3.5">
          <Ring><Gift size={22} color={colors.primary[700]} strokeWidth={2} /></Ring>
          <View className="flex-1">
            <Text className="font-bold text-[15px] text-ink">Próximo desbloqueio</Text>
            <Text className="text-[12px] text-muted font-regular">Miniatura de Banco Central</Text>
          </View>
        </View>
        <Progress pct={78} height={8} />
        <Text className="text-[12px] text-muted font-regular mt-2">Faltam 2 contas pagas em dia</Text>
      </View>

      <View className="bg-white rounded-[18px] p-5 mb-3.5 flex-row items-center gap-3.5" style={shadows.card}>
        <Ring><Zap size={22} color={colors.primary[700]} strokeWidth={2} /></Ring>
        <View className="flex-1">
          <Text className="font-bold text-[15px] text-ink">Próximos passos</Text>
          <Text className="text-[12px] text-muted font-regular">Revise seu planejamento mensal · +1 construção especial</Text>
        </View>
        <ChevronRight size={20} color={colors.text.secondary} strokeWidth={2} />
      </View>
    </Screen>
  );
}

function StatCard({ value, title, sub }: { value: string; title: string; sub: string }) {
  return (
    <View className="flex-1 bg-white rounded-[18px] p-[18px]" style={shadows.card}>
      <Text className="font-bold text-[32px] leading-9 text-primary-700">{value}</Text>
      <Text className="text-sm text-ink font-semibold mt-2">{title}</Text>
      <Text className="text-[12px] text-muted font-regular">{sub}</Text>
    </View>
  );
}
