import Svg, { Circle, Defs, LinearGradient, Line, Path, Stop, Text as SvgText } from "react-native-svg";

type Props = {
  values: number[];
  width?: number;
  height?: number;
  color?: string;
  showGoalLine?: boolean;
  showFill?: boolean;
  dashed?: boolean;
};

export default function LineChart({
  values,
  width = 320,
  height = 180,
  color = "#30BCB3",
  showGoalLine = false,
  showFill = true,
  dashed = false,
}: Props) {
  const padL = 20, padR = 16, padT = 20, padB = 24;
  const innerW = width - padL - padR;
  const innerH = height - padT - padB;
  const maxV = Math.max(1, ...values);

  const toX = (i: number) => padL + (i / (values.length - 1)) * innerW;
  const toY = (v: number) => padT + (1 - v / maxV) * innerH;

  const linePath = values.map((v, i) =>
    `${i === 0 ? "M" : "L"} ${toX(i).toFixed(1)} ${toY(v).toFixed(1)}`
  ).join(" ");

  const areaPath =
    linePath +
    ` L ${toX(values.length - 1)} ${height - padB}` +
    ` L ${toX(0)} ${height - padB} Z`;

  const gradId = `grad-${color.replace("#", "")}`;

  return (
    <Svg width="100%" height={height} viewBox={`0 0 ${width} ${height}`}>
      <Defs>
        <LinearGradient id={gradId} x1="0" y1="0" x2="0" y2="1">
          <Stop offset="0%" stopColor={color} stopOpacity={0.32} />
          <Stop offset="100%" stopColor={color} stopOpacity={0} />
        </LinearGradient>
      </Defs>

      {[0.25, 0.5, 0.75].map((g) => (
        <Line
          key={g}
          x1={padL}
          x2={width - padR}
          y1={padT + (1 - g) * innerH}
          y2={padT + (1 - g) * innerH}
          stroke="#EAEFEC"
          strokeWidth={1}
          strokeDasharray="3 4"
        />
      ))}

      {showGoalLine && (
        <>
          <Line
            x1={padL}
            x2={width - padR}
            y1={toY(maxV)}
            y2={toY(maxV)}
            stroke="#FE6F50"
            strokeWidth={1.5}
            strokeDasharray="4 4"
            opacity={0.5}
          />
          <SvgText
            x={width - padR}
            y={toY(maxV) - 4}
            fill="#FE6F50"
            fontSize={10}
            fontFamily="PlusJakartaSans_700Bold"
            textAnchor="end"
          >
            Meta
          </SvgText>
        </>
      )}

      {showFill && <Path d={areaPath} fill={`url(#${gradId})`} />}
      <Path
        d={linePath}
        fill="none"
        stroke={color}
        strokeWidth={3}
        strokeLinecap="round"
        strokeLinejoin="round"
        strokeDasharray={dashed ? "6 6" : undefined}
      />
      {values.map((v, i) => (
        <Circle
          key={i}
          cx={toX(i)}
          cy={toY(v)}
          r={i === values.length - 1 ? 5 : 0}
          fill="#fff"
          stroke={color}
          strokeWidth={3}
        />
      ))}
    </Svg>
  );
}
