import { ReactNode } from "react";
import { View } from "react-native";
import { PieChart } from "react-native-gifted-charts";

export type DonutSlice = { value: number; color: string };

type Props = {
  data: DonutSlice[];
  size?: number;
  stroke?: number;
  center?: ReactNode;
  /** Color of the donut hole (defaults to the card surface). */
  holeColor?: string;
};

/**
 * Donut chart built on react-native-gifted-charts' PieChart. Recreates the
 * SVG donuts from the design (spending distribution, classification, etc.).
 */
export default function GiftedDonut({ data, size = 140, stroke = 14, center, holeColor = "#FFFFFF" }: Props) {
  const radius = size / 2;
  const innerRadius = radius - stroke;
  const total = data.reduce((s, d) => s + d.value, 0);
  const slices = total > 0 ? data : [{ value: 1, color: "#F1F5F3" }];

  return (
    <View style={{ width: size, height: size }}>
      <PieChart
        data={slices}
        radius={radius}
        innerRadius={innerRadius}
        innerCircleColor={holeColor}
        centerLabelComponent={center ? () => center : undefined}
      />
    </View>
  );
}
