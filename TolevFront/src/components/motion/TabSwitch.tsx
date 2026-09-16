import { MotiView } from "moti";
import type { ReactNode } from "react";
import { motion } from "../../theme";

type Props = {
  /** Aba em exibição. Trocar esta chave é o que dispara a transição. */
  tabKey: string;
  children: ReactNode;
};

/**
 * Troca de aba dentro de uma tela.
 *
 * A `key` remonta o painel, então tudo que ele contém — gráficos e listas
 * inclusive — reaparece com a própria entrada. É o que faz a aba trocar em vez
 * de o conteúdo simplesmente ser substituído.
 */
export default function TabSwitch({ tabKey, children }: Props) {
  return (
    <MotiView
      key={tabKey}
      from={{ opacity: 0, translateY: motion.distance.md }}
      animate={{ opacity: 1, translateY: 0 }}
      transition={{
        type: "timing",
        duration: motion.duration.base,
        easing: motion.easing.out,
      }}
    >
      {children}
    </MotiView>
  );
}
