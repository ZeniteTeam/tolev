import { Pressable, View } from "react-native";

type Props = {
  count: number;
  active: number;
  onChange?: (i: number) => void;
};

export default function DotPagination({ count, active, onChange }: Props) {
  return (
    <View className="flex-row justify-center gap-2 py-2.5">
      {Array.from({ length: count }).map((_, i) => (
        <Pressable
          key={i}
          onPress={() => onChange?.(i)}
          className={`h-2 rounded-pill ${active === i ? "w-6 bg-coral-500" : "w-2 bg-[#D0D2D1]"}`}
        />
      ))}
    </View>
  );
}
