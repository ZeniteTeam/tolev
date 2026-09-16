import { useState } from "react";
import { View } from "react-native";
import { BarChart } from "react-native-gifted-charts";
import { colors, motion } from "../theme";
import ChartReveal from "./motion/ChartReveal";

export type ProjectionMonth = { label: string; divida: number; pagto: number };

type Props = {
  months: ProjectionMonth[];
  height?: number;
  maxValue?: number;
  dividaColor?: string;
  pagtoColor?: string;
  /** Atraso da entrada, para o gráfico chegar depois do card que o contém. */
  delay?: number;
};

/**
 * Barras empilhadas: dívida restante (verde) embaixo, pagamento do mês
 * (laranja) em cima. Mede a própria largura.
 */
export default function ProjectionBarChart({
  months,
  height = 140,
  maxValue = 30,
  dividaColor = colors.primary[500],
  pagtoColor = colors.coral[500],
  delay = 0,
}: Props) {
  const [width, setWidth] = useState(0);
  const count = months.length || 1;
  const barWidth = Math.max(10, Math.round((width / count) * 0.42));
  const spacing = Math.max(6, (width - barWidth * count) / count);
  const sideSpacing = spacing / 2;

  const stackData = months.map((m) => ({
    label: m.label,
    stacks: [
      { value: m.divida, color: dividaColor },
      { value: m.pagto, color: pagtoColor, borderTopLeftRadius: 6, borderTopRightRadius: 6 },
    ],
  }));

  return (
    <ChartReveal delay={delay}>
      <View onLayout={(e) => setWidth(Math.round(e.nativeEvent.layout.width))}>
        {width > 0 && (
          <BarChart
            stackData={stackData}
            width={width}
            height={height}
            maxValue={maxValue}
            barWidth={barWidth}
            spacing={spacing}
            initialSpacing={sideSpacing}
            endSpacing={sideSpacing}
            hideRules
            hideYAxisText
            yAxisThickness={0}
            xAxisThickness={0}
            yAxisLabelWidth={0}
            disableScroll
            xAxisLabelTextStyle={{ color: colors.text.secondary, fontSize: 11, fontFamily: "PlusJakartaSans_400Regular" }}
            /* As barras sobem do eixo — a leitura acompanha a altura crescendo
               em vez de já encontrar o resultado pronto. */
            isAnimated
            animationDuration={motion.duration.chart}
          />
        )}
      </View>
    </ChartReveal>
  );
}
