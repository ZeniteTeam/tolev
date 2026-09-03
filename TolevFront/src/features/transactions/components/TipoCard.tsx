import { Check, type LucideIcon } from "lucide-react-native";
import { Pressable, Text, View } from "react-native";
import { colors, shadows } from "../../../theme";

type Props = {
  icon: LucideIcon;
  title: string;
  subtitle: string;
  /** Cor de destaque quando selecionado — verde para receita, coral para despesa. */
  accent: string;
  selected: boolean;
  onPress: () => void;
};

/** Um dos dois cartões de "entrou ou saiu dinheiro". */
export default function TipoCard({
  icon: Icon,
  title,
  subtitle,
  accent,
  selected,
  onPress,
}: Props) {
  return (
    <Pressable
      onPress={onPress}
      className="flex-1 bg-surface rounded-xl px-4 pt-5 pb-4 items-center active:scale-[0.98]"
      style={[
        shadows.card,
        {
          borderWidth: 2,
          borderColor: selected ? accent : "transparent",
        },
      ]}
    >
      <View
        className="w-6 h-6 rounded-full items-center justify-center self-end -mt-1 mb-1"
        style={{
          backgroundColor: selected ? accent : "transparent",
          borderWidth: selected ? 0 : 2,
          borderColor: colors.border.soft,
        }}
      >
        {selected && <Check size={15} color={colors.surface} strokeWidth={3} />}
      </View>

      <View
        className="w-14 h-14 rounded-full items-center justify-center"
        style={{ backgroundColor: selected ? accent : colors.primary[50] }}
      >
        <Icon
          size={26}
          color={selected ? colors.surface : colors.text.secondary}
          strokeWidth={2}
        />
      </View>

      <Text
        className="font-bold text-base mt-3 text-center"
        style={{ color: selected ? accent : colors.text.primary }}
      >
        {title}
      </Text>
      <Text className="text-xs text-muted mt-1 text-center font-regular leading-[16px]">
        {subtitle}
      </Text>
    </Pressable>
  );
}
