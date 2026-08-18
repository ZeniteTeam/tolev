import { Check } from "lucide-react-native";
import { Pressable, Text, View } from "react-native";
import { colors, shadows } from "../../../theme";
import ChoiceArt, { type ArtKind } from "./ChoiceArt";

type Props = {
  art: ArtKind;
  title: string;
  subtitle: string;
  selected: boolean;
  onPress: () => void;
};

/** One of the two big cards used to pick PRICE/SAC and juros simples/composto. */
export default function ChoiceCard({ art, title, subtitle, selected, onPress }: Props) {
  return (
    <Pressable
      onPress={onPress}
      className="flex-1 bg-surface rounded-xl px-4 pt-5 pb-4 items-center active:scale-[0.98]"
      style={[
        shadows.card,
        {
          borderWidth: 2,
          borderColor: selected ? colors.primary[700] : "transparent",
          backgroundColor: selected ? colors.primary[25] : colors.surface,
        },
      ]}
    >
      <View
        className="w-6 h-6 rounded-full items-center justify-center self-end -mt-1 mb-1"
        style={{
          backgroundColor: selected ? colors.primary[700] : "transparent",
          borderWidth: selected ? 0 : 2,
          borderColor: colors.border.soft,
        }}
      >
        {selected && <Check size={15} color={colors.surface} strokeWidth={3} />}
      </View>

      <ChoiceArt kind={art} active={selected} />

      <Text
        className="font-bold text-base mt-3 text-center"
        style={{ color: selected ? colors.primary[700] : colors.text.primary }}
      >
        {title}
      </Text>
      <Text className="text-xs text-muted mt-1 text-center font-regular leading-[16px]">
        {subtitle}
      </Text>
    </Pressable>
  );
}
