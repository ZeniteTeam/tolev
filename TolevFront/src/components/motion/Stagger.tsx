import { Children, isValidElement, type ReactNode } from "react";
import { View, type StyleProp, type ViewStyle } from "react-native";
import { motion } from "../../theme";
import FadeIn from "./FadeIn";

type Props = {
  children: ReactNode;
  /** Atraso do primeiro item. Os seguintes entram em cima dele. */
  delay?: number;
  /** Classes do container: `Stagger` substitui a `View` que envolvia a lista. */
  className?: string;
  style?: StyleProp<ViewStyle>;
};

/**
 * Lista que entra em cascata curta, um item depois do outro.
 *
 * `Children.toArray` achata o `.map()` de quem chama, então uma lista vinda da
 * API entra item a item sem precisar de invólucro em cada elemento — e a `key`
 * original sobrevive, para remover uma dívida do meio não reanimar o resto.
 */
export default function Stagger({ children, delay = 0, className, style }: Props) {
  const items = Children.toArray(children);

  return (
    <View className={className} style={style}>
      {items.map((child, i) => (
        <FadeIn
          key={isValidElement(child) && child.key != null ? child.key : i}
          delay={delay + Math.min(i, motion.stagger.maxSteps) * motion.stagger.step}
        >
          {child}
        </FadeIn>
      ))}
    </View>
  );
}
