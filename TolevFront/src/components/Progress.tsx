import { MotiView } from "moti";
import { useState } from "react";
import { View } from "react-native";
import { colors, motion, radius } from "../theme";

type Props = {
  pct: number;
  height?: number;
  trackColor?: string;
  fillColor?: string;
  /** `false` deixa a barra já preenchida, sem crescer. */
  animate?: boolean;
};

export default function Progress({
  pct,
  height = 8,
  trackColor,
  fillColor,
  animate = true,
}: Props) {
  // A barra cresce em pixels, não em porcentagem: medir o trilho custa um
  // render a mais e evita depender de animação de valor percentual.
  const [trilho, setTrilho] = useState(0);
  const preenchido = (trilho * Math.max(0, Math.min(100, pct))) / 100;

  return (
    <View
      className="w-full rounded-pill overflow-hidden"
      style={{ height, backgroundColor: trackColor ?? colors.teal[300] + "55" }}
      onLayout={(e) => setTrilho(e.nativeEvent.layout.width)}
    >
      {trilho > 0 && (
        <MotiView
          from={animate ? { width: 0 } : undefined}
          animate={{ width: preenchido }}
          transition={{
            type: "timing",
            duration: motion.duration.chart,
            easing: motion.easing.out,
          }}
          style={{
            height: "100%",
            borderRadius: radius.pill,
            backgroundColor: fillColor ?? colors.teal[500],
          }}
        />
      )}
    </View>
  );
}
