import { ReactNode } from "react";
import { Pressable, Text } from "react-native";
import { colors } from "../theme";

type Props = {
  active?: boolean;
  color?: "teal" | "coral";
  children: ReactNode;
  onPress?: () => void;
};

export default function Chip({ active, color = "teal", children, onPress }: Props) {
  const bgClass = active ? (color === "coral" ? "bg-coral-500" : "bg-teal-500") : "bg-white";
  const fgClass = active ? "text-white" : "text-teal-500";

  return (
    <Pressable
      onPress={onPress}
      className={`py-2 px-[18px] rounded-pill ${bgClass}`}
      style={
        active && color === "coral"
          ? {
              shadowColor: colors.coral[500],
              shadowOpacity: 0.3,
              shadowOffset: { width: 0, height: 4 },
              shadowRadius: 8,
              elevation: 3,
            }
          : undefined
      }
    >
      <Text className={`font-bold text-sm ${fgClass}`}>{children}</Text>
    </Pressable>
  );
}
