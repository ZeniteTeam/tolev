import { Pressable, Text, View } from "react-native";

type Item = { key: string; label: string };

type Props = {
  items: Item[];
  active: string;
  onChange: (k: string) => void;
};

export default function TabsUnderline({ items, active, onChange }: Props) {
  return (
    <View className="flex-row border-b border-b-[#F1F5F3]">
      {items.map((it) => {
        const isActive = active === it.key;
        return (
          <Pressable key={it.key} onPress={() => onChange(it.key)} className="flex-1 items-center py-3 gap-2">
            <Text className={`font-semibold text-md ${isActive ? "text-primary-700" : "text-muted"}`}>
              {it.label}
            </Text>
            <View className={`h-[3px] w-9 rounded-pill ${isActive ? "bg-coral-500" : "bg-transparent"}`} />
          </Pressable>
        );
      })}
    </View>
  );
}
