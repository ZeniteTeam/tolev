import { Check, type LucideIcon } from "lucide-react-native";
import { Pressable, Text, View } from "react-native";
import { colors, shadows } from "../../../theme";

type Props = {
  icon: LucideIcon;
  title: string;
  subtitle?: string;
  selected: boolean;
  onPress: () => void;
  /** `compact` is denser — use it for long lists (e.g. occupation). */
  compact?: boolean;
};

/** A single selectable option, Duolingo-style: soft card, green when picked. */
export default function OptionCard({
  icon: Icon,
  title,
  subtitle,
  selected,
  onPress,
  compact = false,
}: Props) {
  return (
    <Pressable
      onPress={onPress}
      className="bg-surface flex-row items-center active:scale-[0.99]"
      style={[
        shadows.card,
        {
          borderRadius: 18,
          paddingVertical: compact ? 13 : 16,
          paddingHorizontal: 16,
          gap: 14,
          borderWidth: 2,
          borderColor: selected ? colors.primary[700] : "transparent",
        },
      ]}
    >
      <View
        className="items-center justify-center"
        style={{
          width: compact ? 40 : 46,
          height: compact ? 40 : 46,
          borderRadius: 13,
          backgroundColor: selected ? colors.primary[700] : colors.primary[100],
        }}
      >
        <Icon
          size={compact ? 20 : 23}
          color={selected ? colors.surface : colors.primary[700]}
          strokeWidth={2}
        />
      </View>

      <View className="flex-1">
        <Text
          className="font-bold text-ink"
          style={{ fontSize: compact ? 15 : 16 }}
        >
          {title}
        </Text>
        {subtitle && (
          <Text className="text-[12.5px] text-muted mt-0.5 font-regular">{subtitle}</Text>
        )}
      </View>

      <View
        className="items-center justify-center"
        style={{
          width: 24,
          height: 24,
          borderRadius: 12,
          backgroundColor: selected ? colors.primary[700] : "transparent",
          borderWidth: selected ? 0 : 2,
          borderColor: colors.border.soft,
        }}
      >
        {selected && <Check size={15} color={colors.surface} strokeWidth={3} />}
      </View>
    </Pressable>
  );
}
