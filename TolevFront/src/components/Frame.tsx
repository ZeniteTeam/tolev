import { ReactNode } from "react";
import { View, ViewStyle } from "react-native";

type Props = {
  children: ReactNode;
  style?: ViewStyle;
};

export default function Frame({ children, style }: Props) {
  return <View className="flex-1 bg-bg" style={style}>{children}</View>;
}
