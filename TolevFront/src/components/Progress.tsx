import { StyleSheet, View } from "react-native";
import { colors } from "../theme";

type Props = {
  pct: number;
  height?: number;
  trackColor?: string;
  fillColor?: string;
};

export default function Progress({ pct, height = 8, trackColor, fillColor }: Props) {
  return (
    <View style={[styles.track, { height, backgroundColor: trackColor ?? colors.teal[300] + "55" }]}>
      <View
        style={{
          width: `${Math.max(0, Math.min(100, pct))}%`,
          height: "100%",
          backgroundColor: fillColor ?? colors.teal[500],
          borderRadius: 999,
        }}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  track: {
    width: "100%",
    borderRadius: 999,
    overflow: "hidden",
  },
});
