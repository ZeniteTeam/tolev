import { ReactNode } from "react";
import { Pressable, StyleSheet, View, ViewStyle } from "react-native";
import { colors, shadows } from "../theme";

type Props = {
  children: ReactNode;
  onPress?: () => void;
  style?: ViewStyle;
  flat?: boolean;
};

export default function Card({ children, onPress, style, flat }: Props) {
  const inner = (
    <View style={[styles.base, !flat && shadows.card, style]}>{children}</View>
  );
  return onPress ? (
    <Pressable onPress={onPress} style={({ pressed }) => pressed && { opacity: 0.96 }}>
      {inner}
    </Pressable>
  ) : (
    inner
  );
}

const styles = StyleSheet.create({
  base: {
    backgroundColor: colors.surface,
    borderRadius: 18,
    padding: 18,
    marginBottom: 14,
  },
});
