import { Easing } from "react-native-reanimated";

/**
 * Escala de movimento do app.
 *
 * Existe pelo mesmo motivo de colors.ts e spacing.ts: transição é linguagem
 * visual, não detalhe de tela. Com os números aqui, "leve" quer dizer a mesma
 * coisa no gráfico da Análise e na lista de dívidas.
 */
export const motion = {
  duration: {
    /** Micro-reação: um aviso aparecendo, um indicador deslizando. */
    fast: 160,
    /** Padrão de entrada de conteúdo. */
    base: 260,
    /** Blocos maiores, que entram depois do resto. */
    slow: 400,
    /** Desenho de um gráfico — o único que pode se fazer notar. */
    chart: 700,
  },

  /**
   * Quanto o conteúdo sobe ao entrar. Curto de propósito: a entrada sugere
   * direção, não empurra a tela.
   */
  distance: {
    sm: 6,
    md: 12,
  },

  stagger: {
    /** Intervalo entre dois itens vizinhos de uma lista. */
    step: 45,
    /**
     * Teto de passos. Sem ele, uma lista de 30 parcelas viraria uma cascata de
     * mais de um segundo — o último item chegaria depois do usuário.
     */
    maxSteps: 8,
  },

  scale: {
    /** De onde um gráfico cresce ao aparecer. */
    chartFrom: 0.96,
  },

  easing: {
    /** Entra rápido e assenta devagar: é o que faz parecer leve. */
    out: Easing.out(Easing.cubic),
    inOut: Easing.inOut(Easing.quad),
  },
} as const;
