import { MotiView } from "moti";
import type { ReactNode } from "react";
import type { StyleProp, ViewStyle } from "react-native";
import { motion } from "../../theme";

type Props = {
  children: ReactNode;
  /** Atraso em ms. Numa lista, prefira `Stagger` — ele calcula o atraso. */
  delay?: number;
  /** Quanto o conteúdo sobe ao entrar. `0` deixa só a opacidade. */
  distance?: number;
  duration?: number;
  style?: StyleProp<ViewStyle>;
};

/**
 * Entrada padrão de conteúdo: aparece subindo um pouco.
 *
 * Anima na montagem e só nela. Como as telas ficam montadas enquanto o TanStack
 * revalida, uma resposta nova não faz o bloco piscar de novo — que é o que o
 * "só na primeira vez" quer dizer na prática.
 */
export default function FadeIn({
  children,
  delay = 0,
  distance = motion.distance.sm,
  duration = motion.duration.base,
  style,
}: Props) {
  return (
    <MotiView
      from={{ opacity: 0, translateY: distance }}
      animate={{ opacity: 1, translateY: 0 }}
      transition={{ type: "timing", duration, delay, easing: motion.easing.out }}
      style={style}
    >
      {children}
    </MotiView>
  );
}
