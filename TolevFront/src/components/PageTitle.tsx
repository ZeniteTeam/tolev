import { Text, View } from "react-native";

type Props = {
  title: string;
  sub?: string;
};

export default function PageTitle({ title, sub }: Props) {
  return (
    <View className="mb-[22px]">
      <Text className="font-bold text-[24px] leading-[28px] text-ink">{title}</Text>
      {sub && <Text className="font-regular text-sm text-muted mt-1">{sub}</Text>}
    </View>
  );
}
