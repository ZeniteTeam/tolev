import { useState } from "react";
import { View } from "react-native";
import { LineChart } from "react-native-gifted-charts";
import { motion } from "../theme";
import ChartReveal from "./motion/ChartReveal";

type Props = {
  values: number[];
  height?: number;
  color?: string;
  /** Cor do topo do gradiente da área. */
  fillColor?: string;
  /** Atraso da entrada, para o gráfico chegar depois do card que o contém. */
  delay?: number;
};

/** Mede o próprio container, então sempre ocupa a largura disponível. */
export default function ProjectionAreaChart({
  values,
  height = 64,
  color = "#7DCDA8",
  fillColor = "#7DCDA8",
  delay = 0,
}: Props) {
  const [width, setWidth] = useState(0);
  const data = values.map((value) => ({ value }));

  return (
    <ChartReveal delay={delay}>
      <View
        style={{ height }}
        onLayout={(e) => setWidth(Math.round(e.nativeEvent.layout.width))}
      >
        {width > 0 && (
          <LineChart
            data={data}
            width={width}
            height={height}
            adjustToWidth
            initialSpacing={0}
            endSpacing={0}
            yAxisLabelWidth={0}
            thickness={2.5}
            color={color}
            curved
            areaChart
            startFillColor={fillColor}
            endFillColor={fillColor}
            startOpacity={0.6}
            endOpacity={0}
            hideDataPoints
            hideRules
            hideYAxisText
            yAxisThickness={0}
            xAxisThickness={0}
            disableScroll
            /* A linha se desenha da esquerda para a direita — é a projeção
               avançando no tempo, que é exatamente o que o card afirma. */
            isAnimated
            animationDuration={motion.duration.chart}
          />
        )}
      </View>
    </ChartReveal>
  );
}
