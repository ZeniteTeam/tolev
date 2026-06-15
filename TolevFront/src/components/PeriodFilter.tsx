import { Pressable, Text, View } from "react-native";

type Props = {
  active: string;
  onChange: (k: string) => void;
  options?: string[];
};

export default function PeriodFilter({ active, onChange, options = ["1s", "1m", "3m", "6m", "1a", "3a"] }: Props) {
  return (
    <View className="flex-row bg-[#F1F5F3] rounded-pill p-1 gap-0.5">
      {options.map((k) => {
        const isActive = active === k;
        return (
          <Pressable
            key={k}
            onPress={() => onChange(k)}
            className={`flex-1 py-2 rounded-pill items-center ${isActive ? "bg-coral-500" : ""}`}
          >
            <Text className={`text-sm font-semibold ${isActive ? "text-white" : "text-muted"}`}>{k}</Text>
          </Pressable>
        );
      })}
    </View>
  );
}
