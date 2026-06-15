import { Calendar, Gift, MoreVertical, Target } from "lucide-react-native";
import { Text, View } from "react-native";
import { Button, LineChart, PageTitle, QuoteCard, Ring, Screen, Stars } from "../../../components";
import { colors, shadows } from "../../../theme";

export default function MetaExpandidaScreen() {
  const months = ["Jun/25", "Set/25", "Dez/25", "Mar/26", "Ago/26"];
  return (
    <Screen bottomPad={140}>
      <View className="flex-row items-center gap-3 mb-[18px]">
        <Ring style={{ width: 48, height: 48, borderRadius: 24 }}>
          <Target size={26} color={colors.primary[700]} strokeWidth={2} />
        </Ring>
        <View className="flex-1">
          <Text className="text-[11px] text-muted tracking-[0.5px] font-semibold">META EM PROGRESSO</Text>
          <Text className="font-bold text-[22px] text-ink mt-0.5">Comprar um carro</Text>
        </View>
        <MoreVertical size={20} color={colors.text.secondary} strokeWidth={2} />
      </View>

      <View className="gap-3 mb-[18px]">
        <QuoteCard variant="primary">
          Quero um carro para proporcionar mais conforto para a minha família
        </QuoteCard>
        <QuoteCard variant="accent">
          Quando eu conseguir, vou levar meus filhos para conhecer o mar
        </QuoteCard>
      </View>

      <View className="bg-white rounded-[18px] p-5 mb-3.5" style={shadows.card}>
        <View className="flex-row justify-between items-start">
          <View>
            <Text className="text-sm text-muted font-regular">Acumulado</Text>
            <Text className="font-bold text-[28px] text-primary-700 mt-1">R$ 40.000</Text>
            <Text className="text-sm text-muted font-regular">de R$ 70.000</Text>
          </View>
          <View className="bg-primary-100 px-3.5 py-2 rounded-pill">
            <Text className="font-bold text-[16px] text-primary-700">60%</Text>
          </View>
        </View>

        <View className="mt-4">
          <LineChart
            values={[5, 18, 30, 45, 60]}
            color={colors.teal[500]}
            showGoalLine
          />
          <View className="flex-row justify-between px-1 mt-1">
            {months.map((m) => (
              <Text key={m} className="text-[11px] text-muted font-regular">{m}</Text>
            ))}
          </View>
        </View>
      </View>

      <View className="bg-white rounded-[18px] p-5 mb-3.5 flex-row items-center gap-4" style={shadows.card}>
        <Ring><Calendar size={22} color={colors.primary[700]} strokeWidth={2} /></Ring>
        <View className="flex-1">
          <Text className="text-sm text-muted font-regular">Período da meta</Text>
          <View className="flex-row items-baseline gap-2 mt-1">
            <Text className="font-bold text-[15px] text-ink">01/06/2025</Text>
            <Text className="text-sm text-muted font-regular">—</Text>
            <Text className="font-bold text-[15px] text-ink">01/08/2026</Text>
          </View>
        </View>
      </View>

      <View className="flex-row gap-3 mb-4">
        <View className="flex-1 bg-white rounded-[18px] p-[18px] items-center" style={shadows.card}>
          <Text className="text-[12px] text-muted font-regular text-center">Nível de{"\n"}comprometimento</Text>
          <View className="items-center my-2.5"><Stars /></View>
          <Text className="text-coral-500 font-bold text-[14px] text-center">Excelente</Text>
        </View>
        <View className="flex-1 bg-white rounded-[18px] p-[18px] items-center" style={shadows.card}>
          <Text className="text-[12px] text-muted font-regular text-center">Recompensa ao{"\n"}concluir</Text>
          <View className="items-center my-2.5">
            <Ring><Gift size={22} color={colors.primary[700]} strokeWidth={2} /></Ring>
          </View>
          <Text className="text-teal-500 font-bold text-sm text-center">+2 pontos de{"\n"}resiliência</Text>
        </View>
      </View>

      <Button variant="primary">Adicionar valor</Button>
    </Screen>
  );
}
