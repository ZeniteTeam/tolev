import { Leaf } from "lucide-react-native";
import { Text, View } from "react-native";
import { colors, shadows } from "../../../theme";

type Props = {
  /** Diâmetro da marca em px. */
  size?: number;
  /** `mark` desenha só o símbolo; `full` acrescenta o texto "Tolev". */
  variant?: "mark" | "full";
  /** Para fundos escuros ou com gradiente. */
  onDark?: boolean;
};

/**
 * Logo provisória. Quando a arte final existir, troque só a marca de dentro
 * (ex.: <Image source={require("../../../assets/logo.png")} />) — o layout em
 * volta continua igual.
 */
export default function BrandLogo({ size = 64, variant = "full", onDark = false }: Props) {
  return (
    <View className="items-center gap-3">
      <View
        style={[
          {
            width: size,
            height: size,
            borderRadius: size * 0.32,
            backgroundColor: onDark ? colors.surface : colors.primary[700],
          },
          shadows.card,
        ]}
        className="items-center justify-center"
      >
        <Leaf
          size={size * 0.5}
          color={onDark ? colors.primary[700] : colors.surface}
          strokeWidth={2.4}
        />
      </View>
      {variant === "full" && (
        <Text
          style={{ color: onDark ? colors.surface : colors.primary[700] }}
          className="font-bold text-[26px] tracking-[-0.5px]"
        >
          Tolev
        </Text>
      )}
    </View>
  );
}
