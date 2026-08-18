import Svg, { Circle, Line, Path, Rect } from "react-native-svg";
import { colors } from "../../../theme";

export type ArtKind = "price" | "sac" | "simples" | "composto";

type Props = {
  kind: ArtKind;
  active: boolean;
};

const W = 92;
const H = 62;

/**
 * Small drawings that show what each option does to your money — flat bars for
 * PRICE, shrinking bars for SAC, a straight line for juros simples and a curve
 * that takes off for compostos. Easier to read than any label.
 */
export default function ChoiceArt({ kind, active }: Props) {
  const strong = active ? colors.primary[700] : colors.text.secondary;
  const soft = active ? colors.primary[300] : colors.border.soft;

  if (kind === "price" || kind === "sac") {
    // Bar heights as a fraction of the drawing: PRICE stays flat, SAC steps down.
    const heights = kind === "price" ? [0.72, 0.72, 0.72, 0.72, 0.72] : [0.9, 0.75, 0.6, 0.45, 0.3];
    const barW = 12;
    const gap = 5;
    const baseline = H - 8;

    return (
      <Svg width={W} height={H}>
        {heights.map((f, i) => {
          const h = f * (H - 16);
          return (
            <Rect
              key={i}
              x={i * (barW + gap) + 8}
              y={baseline - h}
              width={barW}
              height={h}
              rx={3}
              fill={i === 0 ? strong : soft}
            />
          );
        })}
        <Line x1={4} y1={baseline + 2} x2={W - 4} y2={baseline + 2} stroke={soft} strokeWidth={2} strokeLinecap="round" />
      </Svg>
    );
  }

  // Both curves start and end at the same points; only the path between differs.
  const start = { x: 8, y: H - 10 };
  const end = { x: W - 8, y: 10 };
  const d =
    kind === "simples"
      ? `M ${start.x} ${start.y} L ${end.x} ${end.y}`
      : `M ${start.x} ${start.y} C ${W * 0.55} ${H - 12}, ${W * 0.78} ${H - 26}, ${end.x} ${end.y}`;

  return (
    <Svg width={W} height={H}>
      <Line x1={4} y1={H - 8} x2={W - 4} y2={H - 8} stroke={soft} strokeWidth={2} strokeLinecap="round" />
      <Path d={d} stroke={strong} strokeWidth={3} strokeLinecap="round" fill="none" />
      <Circle cx={end.x} cy={end.y} r={4.5} fill={strong} />
    </Svg>
  );
}
