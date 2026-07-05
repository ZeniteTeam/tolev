import { ChevronRight } from "lucide-react-native";
import { Pressable, Text, View } from "react-native";
import { Progress } from "../../../components";
import { colors, shadows } from "../../../theme";
import { brl, type DividaView } from "../constants/dividas";

type Props = {
  divida: DividaView;
  totalDivida: number;
  onPress?: () => void;
};

export default function DebtCard({ divida: d, totalDivida, onPress }: Props) {
  const share = totalDivida > 0 ? Math.round((d.saldo / totalDivida) * 100) : 0;
  const Icon = d.icon;

  return (
    <Pressable
      onPress={onPress}
      className="bg-surface rounded-[18px] p-[18px] mb-3.5 gap-4 active:opacity-90"
      style={shadows.card}
    >
      <View className="flex-row items-center gap-3">
        <View
          className="w-10 h-10 rounded-[11px] items-center justify-center"
          style={{ backgroundColor: d.bankColor }}
        >
          <Icon size={19} color="#fff" strokeWidth={2} />
        </View>
        <View className="flex-1">
          <Text className="font-semibold text-[15px] text-ink">{d.nome}</Text>
          <Text className="text-[12px] text-muted mt-0.5 font-regular">{d.banco}</Text>
        </View>
        <ChevronRight size={20} color={colors.text.secondary} strokeWidth={2} />
      </View>

      <View className="flex-row justify-between items-end">
        <View>
          <Text className="text-[11px] text-muted mb-0.5 font-regular">Saldo devedor</Text>
          <Text className="font-bold text-[20px] text-ink">{brl(d.saldo)}</Text>
        </View>
        <View className="flex-row gap-[18px]">
          <View className="items-end">
            <Text className="text-[11px] text-muted mb-0.5 font-regular">Juros a.m.</Text>
            <Text
              className="font-bold text-[14px]"
              style={{ color: d.juros >= 8 ? colors.coral[500] : colors.text.primary }}
            >
              {d.juros.toFixed(1).replace(".", ",")}%
            </Text>
          </View>
          <View className="items-end">
            <Text className="text-[11px] text-muted mb-0.5 font-regular">Mínimo</Text>
            <Text className="font-bold text-[14px] text-ink">{brl(d.min)}</Text>
          </View>
        </View>
      </View>

      <View>
        <View className="flex-row justify-between mb-1.5">
          <Text className="text-[11px] text-muted font-regular">Peso na dívida total</Text>
          <Text className="text-[12px] text-teal-500 font-bold">{share}%</Text>
        </View>
        <Progress pct={share} height={6} />
      </View>
    </Pressable>
  );
}
