import { ReactNode } from "react";
import { StyleSheet, Text, View } from "react-native";
import { colors } from "../theme";

type Props = {
  variant?: "primary" | "accent";
  children: ReactNode;
};

export default function QuoteCard({ variant = "primary", children }: Props) {
  const isAccent = variant === "accent";
  const bg = isAccent ? "rgba(254,111,80,0.10)" : colors.primary[25];
  const fg = isAccent ? colors.coral[500] : colors.primary[700];
  return (
    <View style={[styles.card, { backgroundColor: bg }]}>
      <Text style={[styles.quote, { color: fg }]}>"</Text>
      <Text style={[styles.body, { color: fg }]}>{children}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    borderRadius: 18,
    paddingVertical: 20,
    paddingLeft: 50,
    paddingRight: 24,
    position: "relative",
  },
  quote: {
    position: "absolute",
    left: 18,
    top: 6,
    fontSize: 56,
    lineHeight: 60,
    fontFamily: "PlusJakartaSans_700Bold",
  },
  body: {
    fontFamily: "PlusJakartaSans_500Medium",
    fontSize: 15,
    lineHeight: 22,
  },
});
