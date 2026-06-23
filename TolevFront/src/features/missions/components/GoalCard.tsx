import {
  Award,
  CheckCircle,
  Gift,
  MoreVertical,
  type LucideIcon,
} from "lucide-react-native";
import { Pressable, Text, View } from "react-native";
import { Progress } from "../../../components";
import { colors, shadows } from "../../../theme";

type Props = {
  title: string;
  pct: number;
  valorAtual: string;
  valorFinal: string;
  icon: LucideIcon;
  onPress?: () => void;
  onMore?: () => void;
};

export function GoalCard({
  title,
  pct,
  valorAtual,
  valorFinal,
  icon: Icon,
  onPress,
  onMore,
}: Props) {
  return (
    <Pressable
      onPress={onPress}
      className="bg-white rounded-[18px] p-5 mb-3.5 gap-5"
      style={shadows.card}
    >
      <View className="flex-row items-center gap-3">
        <View className="w-9 h-9 rounded-md bg-primary-100 items-center justify-center">
          <Icon size={18} color={colors.primary[700]} strokeWidth={2} />
        </View>
        <Text className="flex-1 font-semibold text-[16px] text-ink">
          {title}
        </Text>
        <Pressable onPress={onMore} hitSlop={10}>
          <MoreVertical
            size={18}
            color={colors.text.secondary}
            strokeWidth={2}
          />
        </Pressable>
      </View>

      <View>
        <View className="flex-row justify-between mb-2">
          <Text className="text-[12px] text-muted font-regular">Progresso</Text>
          <Text className="text-sm text-teal-500 font-bold">{pct}%</Text>
        </View>
        <Progress pct={pct} height={6} />
      </View>

      <View className="flex-row justify-between">
        <View>
          <Text className="text-[11px] text-muted mb-1 font-regular">
            Valor atual
          </Text>
          <Text className="font-semibold text-[14px] text-ink">
            {valorAtual}
          </Text>
        </View>
        <View className="items-end">
          <Text className="text-[11px] text-muted mb-1 font-regular">Meta</Text>
          <Text className="font-semibold text-[14px] text-ink">
            {valorFinal}
          </Text>
        </View>
      </View>
    </Pressable>
  );
}

export function GoalCardCompleted({
  title,
  date,
}: {
  title: string;
  date: string;
}) {
  return (
    <View
      className="bg-white rounded-[18px] p-5 mb-3.5 gap-5"
      style={shadows.card}
    >
      <View className="flex-row items-center gap-3">
        <View className="w-9 h-9 rounded-md bg-primary-700 items-center justify-center">
          <Award size={18} color="#fff" strokeWidth={2} />
        </View>
        <Text className="flex-1 font-semibold text-[16px] text-ink">
          {title}
        </Text>
        <MoreVertical size={18} color={colors.text.secondary} strokeWidth={2} />
      </View>

      <View className="flex-row justify-between items-center gap-3">
        <View className="flex-row items-center gap-1.5 bg-primary-100 px-3 py-1.5 rounded-pill">
          <CheckCircle size={14} color={colors.primary[700]} strokeWidth={2} />
          <Text className="font-semibold text-[12px] text-primary-700">
            Alcançada em {date}
          </Text>
        </View>
        <Pressable className="flex-row items-center gap-1.5 h-9 px-3.5 bg-coral-500 rounded-pill">
          <Gift size={14} color="#fff" strokeWidth={2} />
          <Text className="text-white font-bold text-sm">Resgatar</Text>
        </Pressable>
      </View>
    </View>
  );
}
