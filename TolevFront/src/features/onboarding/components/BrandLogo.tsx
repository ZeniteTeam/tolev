import { Leaf } from "lucide-react-native";
import { Text, View } from "react-native";
import { colors, shadows } from "../../../theme";

type Props = {
  /** Diameter of the logo mark in px. */
  size?: number;
  /** `mark` renders only the badge; `full` adds the "Tolev" wordmark. */
  variant?: "mark" | "full";
  /** Use on dark/gradient backgrounds. */
  onDark?: boolean;
};

/**
 * Placeholder Tolev logo. Swap the inner mark for the final brand asset
 * (e.g. an <Image source={require("../../../assets/logo.png")} />) once it
 * exists — the surrounding layout stays the same.
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
