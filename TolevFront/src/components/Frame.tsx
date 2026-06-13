import { ReactNode } from "react";
import { StyleSheet, View, ViewStyle } from "react-native";
import { colors } from "../theme";

type Props = {
  children: ReactNode;
  style?: ViewStyle;
};

export default function Frame({ children, style }: Props) {
  return <View style={[styles.frame, style]}>{children}</View>;
}

const styles = StyleSheet.create({
  frame: {
    flex: 1,
    backgroundColor: colors.background,
  },
});
