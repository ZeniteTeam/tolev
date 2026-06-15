import { ReactNode } from "react";
import { Pressable, View, ViewStyle } from "react-native";
import { shadows } from "../theme";

type Props = {
  children: ReactNode;
  onPress?: () => void;
  style?: ViewStyle;
  flat?: boolean;
};

export default function Card({ children, onPress, style, flat }: Props) {
  const inner = (
    <View className="bg-surface rounded-[18px] p-[18px] mb-[14px]" style={[!flat && shadows.card, style]}>
      {children}
    </View>
  );
  return onPress ? (
    <Pressable onPress={onPress} className="active:opacity-96">
      {inner}
    </Pressable>
  ) : (
    inner
  );
}
