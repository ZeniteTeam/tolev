import { MotiView } from "moti";
import type { ReactNode } from "react";
import type { StyleProp, ViewStyle } from "react-native";
import { motion } from "../../theme";

type Props = {
  children: ReactNode;
  delay?: number;
  style?: StyleProp<ViewStyle>;
};

/**
 * Entrada de gráfico: cresce de leve em vez de subir.
 *
 * Escala em vez de deslocamento porque um gráfico não tem para onde vir — ele
 * se forma no lugar. O card em volta usa `FadeIn`; a diferença entre os dois é
 * o que separa "o card chegou" de "o dado apareceu".
 */
export default function ChartReveal({ children, delay = 0, style }: Props) {
  return (
    <MotiView
      from={{ opacity: 0, scale: motion.scale.chartFrom }}
      animate={{ opacity: 1, scale: 1 }}
      transition={{
        type: "timing",
        duration: motion.duration.slow,
        delay,
        easing: motion.easing.out,
      }}
      style={style}
    >
      {children}
    </MotiView>
  );
}
