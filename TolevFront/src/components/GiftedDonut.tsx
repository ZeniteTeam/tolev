import { ReactNode } from "react";
import { PieChart } from "react-native-gifted-charts";
import ChartReveal from "./motion/ChartReveal";

export type DonutSlice = { value: number; color: string };

type Props = {
  data: DonutSlice[];
  size?: number;
  stroke?: number;
  center?: ReactNode;
  /** Cor do furo do donut (por padrão, a superfície do card). */
  holeColor?: string;
  /** Atraso da entrada, para o gráfico chegar depois do card. */
  delay?: number;
};

export default function GiftedDonut({
  data,
  size = 140,
  stroke = 14,
  center,
  holeColor = "#FFFFFF",
  delay = 0,
}: Props) {
  const radius = size / 2;
  const innerRadius = radius - stroke;
  const total = data.reduce((s, d) => s + d.value, 0);
  const slices = total > 0 ? data : [{ value: 1, color: "#F1F5F3" }];

  /*
    A entrada é por escala, e não pelo `isAnimated` do gifted-charts: no PieChart
    da versão gratuita essa prop é lida e descartada, então ligá-la só prometeria
    um desenho progressivo que nunca acontece.
  */
  return (
    <ChartReveal delay={delay} style={{ width: size, height: size }}>
      <PieChart
        data={slices}
        radius={radius}
        innerRadius={innerRadius}
        innerCircleColor={holeColor}
        centerLabelComponent={center ? () => center : undefined}
      />
    </ChartReveal>
  );
}
