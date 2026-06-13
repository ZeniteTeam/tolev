import { ReactNode } from "react";
import { Pressable, StyleSheet, Text } from "react-native";
import { colors } from "../theme";

type Props = {
  active?: boolean;
  color?: "teal" | "coral";
  children: ReactNode;
  onPress?: () => void;
};

export default function Chip({ active, color = "teal", children, onPress }: Props) {
  const bg = active ? (color === "coral" ? colors.coral[500] : colors.teal[500]) : "#fff";
  const fg = active ? "#fff" : colors.teal[500];
  return (
    <Pressable
      onPress={onPress}
      style={[
        styles.base,
        { backgroundColor: bg },
        active && color === "coral" && {
          shadowColor: colors.coral[500],
          shadowOpacity: 0.3,
          shadowOffset: { width: 0, height: 4 },
          shadowRadius: 8,
          elevation: 3,
        },
      ]}
    >
      <Text style={[styles.label, { color: fg }]}>{children}</Text>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  base: {
    paddingVertical: 8,
    paddingHorizontal: 18,
    borderRadius: 999,
  },
  label: {
    fontFamily: "PlusJakartaSans_700Bold",
    fontSize: 13,
  },
});
