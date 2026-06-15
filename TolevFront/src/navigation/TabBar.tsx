import { BottomTabBarProps } from "@react-navigation/bottom-tabs";
import { BarChart2, Home, PieChart, Target, type LucideIcon } from "lucide-react-native";
import { Pressable, Text, View } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";

const ICONS: Record<string, LucideIcon> = {
  Menu: Home,
  Metas: Target,
  Financas: PieChart,
  Progresso: BarChart2,
};

const LABELS: Record<string, string> = {
  Menu: "Menu",
  Metas: "Metas",
  Financas: "Finanças",
  Progresso: "Progresso",
};

export default function TabBar({ state, navigation }: BottomTabBarProps) {
  const insets = useSafeAreaInsets();
  return (
    <View className="flex-row bg-primary-700 pt-2.5 px-2" style={{ paddingBottom: Math.max(insets.bottom, 8) }}>
      {state.routes.map((route, idx) => {
        const isActive = state.index === idx;
        const Icon = ICONS[route.name] ?? Home;
        return (
          <Pressable
            key={route.key}
            onPress={() => navigation.navigate(route.name)}
            className="flex-1 items-center gap-1 py-1"
          >
            <Icon size={22} color="#fff" strokeWidth={2} style={!isActive && { opacity: 0.72 }} />
            <Text className={`text-white font-medium text-xs ${isActive ? "opacity-100" : "opacity-[0.72]"}`}>
              {LABELS[route.name] ?? route.name}
            </Text>
            {isActive && <View className="absolute -bottom-0.5 w-9 h-[3px] rounded-pill bg-coral-500" />}
          </Pressable>
        );
      })}
    </View>
  );
}
