import { Star } from "lucide-react-native";
import { Pressable, View } from "react-native";
import { colors } from "../theme";

type Props = {
  count?: number;
  filled?: number;
  size?: number;
  onChange?: (n: number) => void;
};

export default function Stars({ count = 5, filled = 5, size = 22, onChange }: Props) {
  return (
    <View style={{ flexDirection: "row", gap: 6 }}>
      {Array.from({ length: count }).map((_, i) => {
        const isFilled = i < filled;
        const node = (
          <Star
            size={size}
            color={colors.coral[500]}
            fill={isFilled ? colors.coral[500] : "transparent"}
            strokeWidth={2}
          />
        );
        return onChange ? (
          <Pressable key={i} onPress={() => onChange(i + 1)}>
            {node}
          </Pressable>
        ) : (
          <View key={i}>{node}</View>
        );
      })}
    </View>
  );
}
