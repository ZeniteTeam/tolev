import Svg, { Circle, G, Text as SvgText } from "react-native-svg";

export type DonutSegment = { value: number; color: string };

type Props = {
  segments: DonutSegment[];
  size?: number;
  stroke?: number;
  centerLabel?: string;
  centerValue?: string;
};

export default function Donut({ segments, size = 100, stroke = 12, centerLabel, centerValue }: Props) {
  const R = (size - stroke) / 2;
  const CX = size / 2;
  const CY = size / 2;
  const C = 2 * Math.PI * R;
  const total = segments.reduce((s, x) => s + x.value, 0);
  let offset = 0;
  const arcs = segments.map((seg, i) => {
    const len = (seg.value / total) * C;
    const node = {
      ...seg,
      dasharray: `${len} ${C - len}`,
      dashoffset: -offset,
    };
    offset += len;
    return node;
  });

  return (
    <Svg width={size} height={size} viewBox={`0 0 ${size} ${size}`}>
      <G rotation={-90} origin={`${CX}, ${CY}`}>
        <Circle cx={CX} cy={CY} r={R} fill="none" stroke="#F1F5F3" strokeWidth={stroke} />
        {arcs.map((a, i) => (
          <Circle
            key={i}
            cx={CX}
            cy={CY}
            r={R}
            fill="none"
            stroke={a.color}
            strokeWidth={stroke}
            strokeDasharray={a.dasharray}
            strokeDashoffset={a.dashoffset}
          />
        ))}
      </G>
      {centerLabel && (
        <SvgText
          x={CX}
          y={CY - 4}
          textAnchor="middle"
          fontSize={11}
          fill="#6B7D75"
          fontFamily="PlusJakartaSans_500Medium"
        >
          {centerLabel}
        </SvgText>
      )}
      {centerValue && (
        <SvgText
          x={CX}
          y={centerLabel ? CY + 14 : CY + 5}
          textAnchor="middle"
          fontSize={15}
          fill="#1E2A25"
          fontFamily="PlusJakartaSans_700Bold"
        >
          {centerValue}
        </SvgText>
      )}
    </Svg>
  );
}
