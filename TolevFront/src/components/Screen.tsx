import { ReactNode } from "react";
import { ScrollView, ViewStyle } from "react-native";

type Props = {
  children: ReactNode;
  contentStyle?: ViewStyle;
  bottomPad?: number;
};

export default function Screen({ children, contentStyle, bottomPad = 32 }: Props) {
  return (
    <ScrollView
      className="flex-1 bg-bg"
      contentContainerClassName="px-5 pt-[18px]"
      contentContainerStyle={[{ paddingBottom: bottomPad }, contentStyle]}
      showsVerticalScrollIndicator={false}
    >
      {children}
    </ScrollView>
  );
}
