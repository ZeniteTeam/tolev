import { ReactNode } from "react";
import { Pressable, StyleSheet, Text, ViewStyle } from "react-native";
import { colors, shadows } from "../theme";

type Variant = "primary" | "outline" | "ghost";

type Props = {
  children: ReactNode;
  variant?: Variant;
  onPress?: () => void;
  style?: ViewStyle;
};

export default function Button({ children, variant = "primary", onPress, style }: Props) {
  return (
    <Pressable
      onPress={onPress}
      style={({ pressed }) => [
        styles.base,
        variant === "primary" && [styles.primary, shadows.deep],
        variant === "outline" && styles.outline,
        variant === "ghost" && styles.ghost,
        pressed && { opacity: 0.85, transform: [{ scale: 0.98 }] },
        style,
      ]}
    >
      <Text
        style={[
          styles.label,
          variant === "primary" && { color: "#fff" },
          variant === "outline" && { color: colors.teal[500] },
          variant === "ghost" && { color: colors.text.secondary },
        ]}
      >
        {children}
      </Text>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  base: {
    height: 48,
    borderRadius: 36,
    alignItems: "center",
    justifyContent: "center",
    paddingHorizontal: 28,
  },
  primary: {
    backgroundColor: colors.coral[500],
  },
  outline: {
    backgroundColor: "transparent",
    borderWidth: 2,
    borderColor: colors.teal[500],
  },
  ghost: {
    backgroundColor: "transparent",
  },
  label: {
    fontFamily: "PlusJakartaSans_700Bold",
    fontSize: 17,
    lineHeight: 20,
  },
});
