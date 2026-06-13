import { ReactNode } from "react";
import { StyleSheet, View, ViewStyle } from "react-native";
import { colors } from "../theme";

type Props = {
  children?: ReactNode;
  size?: "md" | "lg" | "xl";
  style?: ViewStyle;
};

export default function Ring({ children, size = "md", style }: Props) {
  const dim = size === "xl" ? 64 : size === "lg" ? 48 : 40;
  return (
    <View style={[styles.base, { width: dim, height: dim, borderRadius: dim / 2 }, style]}>
      {children}
    </View>
  );
}

const styles = StyleSheet.create({
  base: {
    backgroundColor: colors.primary[100],
    alignItems: "center",
    justifyContent: "center",
  },
});
