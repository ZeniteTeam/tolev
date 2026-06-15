import { ReactNode } from "react";
import { Text, View } from "react-native";
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
    <View className="rounded-[18px] py-5 pl-[50px] pr-6 relative" style={{ backgroundColor: bg }}>
      <Text className="absolute left-[18px] top-1.5 text-[56px] leading-[60px] font-bold" style={{ color: fg }}>
        &quot;
      </Text>
      <Text className="font-medium text-md" style={{ color: fg }}>
        {children}
      </Text>
    </View>
  );
}
