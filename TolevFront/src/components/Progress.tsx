import { View } from "react-native";
import { colors } from "../theme";

type Props = {
  pct: number;
  height?: number;
  trackColor?: string;
  fillColor?: string;
};

export default function Progress({ pct, height = 8, trackColor, fillColor }: Props) {
  return (
    <View className="w-full rounded-pill overflow-hidden" style={{ height, backgroundColor: trackColor ?? colors.teal[300] + "55" }}>
      <View
        className="h-full rounded-pill"
        style={{
          width: `${Math.max(0, Math.min(100, pct))}%`,
          backgroundColor: fillColor ?? colors.teal[500],
        }}
      />
    </View>
  );
}
