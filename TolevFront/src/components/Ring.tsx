import { ReactNode } from "react";
import { View, ViewStyle } from "react-native";

type Props = {
  children?: ReactNode;
  size?: "md" | "lg" | "xl";
  style?: ViewStyle;
};

const sizeClasses: Record<NonNullable<Props["size"]>, string> = {
  md: "w-10 h-10",
  lg: "w-12 h-12",
  xl: "w-16 h-16",
};

export default function Ring({ children, size = "md", style }: Props) {
  return (
    <View className={`bg-primary-100 items-center justify-center rounded-full ${sizeClasses[size]}`} style={style}>
      {children}
    </View>
  );
}
