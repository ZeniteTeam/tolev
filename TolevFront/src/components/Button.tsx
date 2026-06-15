import { ReactNode } from "react";
import { Pressable, Text, ViewStyle } from "react-native";
import { shadows } from "../theme";

type Variant = "primary" | "outline" | "ghost";

type Props = {
  children: ReactNode;
  variant?: Variant;
  onPress?: () => void;
  style?: ViewStyle;
};

const containerClasses: Record<Variant, string> = {
  primary: "bg-coral-500",
  outline: "bg-transparent border-2 border-teal-500",
  ghost: "bg-transparent",
};

const labelClasses: Record<Variant, string> = {
  primary: "text-white",
  outline: "text-teal-500",
  ghost: "text-muted",
};

export default function Button({ children, variant = "primary", onPress, style }: Props) {
  return (
    <Pressable
      onPress={onPress}
      className={`h-12 rounded-[36px] items-center justify-center px-7 active:opacity-85 active:scale-[0.98] ${containerClasses[variant]}`}
      style={[variant === "primary" && shadows.deep, style]}
    >
      <Text className={`font-bold text-[17px] leading-[20px] ${labelClasses[variant]}`}>
        {children}
      </Text>
    </Pressable>
  );
}
