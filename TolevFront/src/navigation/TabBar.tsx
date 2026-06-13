import { BottomTabBarProps } from "@react-navigation/bottom-tabs";
import { BarChart2, Home, PieChart, Target, type LucideIcon } from "lucide-react-native";
import { Pressable, StyleSheet, Text, View } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { colors } from "../theme";

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
    <View style={[styles.bar, { paddingBottom: Math.max(insets.bottom, 8) }]}>
      {state.routes.map((route, idx) => {
        const isActive = state.index === idx;
        const Icon = ICONS[route.name] ?? Home;
        return (
          <Pressable
            key={route.key}
            onPress={() => navigation.navigate(route.name)}
            style={styles.slot}
          >
            <Icon size={22} color="#fff" strokeWidth={2} style={!isActive && { opacity: 0.72 }} />
            <Text style={[styles.label, !isActive && { opacity: 0.72 }]}>
              {LABELS[route.name] ?? route.name}
            </Text>
            {isActive && <View style={styles.underline} />}
          </Pressable>
        );
      })}
    </View>
  );
}

const styles = StyleSheet.create({
  bar: {
    flexDirection: "row",
    backgroundColor: colors.primary[700],
    paddingTop: 10,
    paddingHorizontal: 8,
  },
  slot: {
    flex: 1,
    alignItems: "center",
    gap: 4,
    paddingVertical: 4,
  },
  label: {
    color: "#fff",
    fontFamily: "PlusJakartaSans_500Medium",
    fontSize: 11,
  },
  underline: {
    position: "absolute",
    bottom: -2,
    width: 36,
    height: 3,
    borderRadius: 999,
    backgroundColor: colors.coral[500],
  },
});
