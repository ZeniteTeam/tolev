import { Film, Home, LucideIcon, MoreHorizontal, ShoppingCart, Truck } from "lucide-react-native";
import { StyleSheet, Text, View } from "react-native";
import { Donut } from "../../../components";
import { colors } from "../../../theme";

type Categoria = {
  label: string;
  value: number;
  color: string;
  icon: LucideIcon;
};

export const GASTOS: Categoria[] = [
  { label: "Moradia", value: 1200, color: "#03643F", icon: Home },
  { label: "Alimentação", value: 850, color: "#1CA474", icon: ShoppingCart },
  { label: "Transporte", value: 420, color: "#30BCB3", icon: Truck },
  { label: "Lazer", value: 380, color: "#FE6F50", icon: Film },
  { label: "Outros", value: 410, color: "#FEAC96", icon: MoreHorizontal },
];

export function CategoriaGastosCompact() {
  const total = GASTOS.reduce((s, c) => s + c.value, 0);
  const top3 = [...GASTOS].sort((a, b) => b.value - a.value).slice(0, 3);
  return (
    <View style={{ flexDirection: "row", alignItems: "center", gap: 16 }}>
      <Donut
        segments={GASTOS.map((c) => ({ value: c.value, color: c.color }))}
        size={88}
        stroke={10}
        centerValue={`R$ ${(total / 1000).toFixed(1)}k`}
      />
      <View style={{ flex: 1, gap: 8 }}>
        {top3.map((c) => {
          const pct = Math.round((c.value / total) * 100);
          return (
            <View key={c.label} style={styles.row}>
              <View style={[styles.dot, { backgroundColor: c.color }]} />
              <Text style={styles.label} numberOfLines={1}>{c.label}</Text>
              <Text style={[styles.pct, { color: c.color }]}>{pct}%</Text>
            </View>
          );
        })}
      </View>
    </View>
  );
}

export function CategoriaGastosDetailed() {
  const total = GASTOS.reduce((s, c) => s + c.value, 0);
  const sorted = [...GASTOS].sort((a, b) => b.value - a.value);
  return (
    <View style={{ gap: 16 }}>
      <View style={{ alignItems: "center" }}>
        <Donut
          segments={GASTOS.map((c) => ({ value: c.value, color: c.color }))}
          size={140}
          stroke={14}
          centerLabel="Total"
          centerValue={`R$ ${(total / 1000).toFixed(1)}k`}
        />
      </View>
      <View style={{ gap: 12 }}>
        {sorted.map((c) => {
          const pct = Math.round((c.value / total) * 100);
          const Icon = c.icon;
          return (
            <View key={c.label}>
              <View style={{ flexDirection: "row", alignItems: "center", gap: 12 }}>
                <View style={[styles.tile, { backgroundColor: c.color }]}>
                  <Icon size={16} color="#fff" strokeWidth={2} />
                </View>
                <View style={{ flex: 1 }}>
                  <Text style={styles.tileLabel}>{c.label}</Text>
                  <Text style={styles.tileSub}>R$ {c.value.toLocaleString("pt-BR")}</Text>
                </View>
                <Text style={[styles.pct, { color: c.color }]}>{pct}%</Text>
              </View>
              <View style={[styles.barTrack, { marginLeft: 44 }]}>
                <View style={{ width: `${pct}%`, height: "100%", backgroundColor: c.color, borderRadius: 999 }} />
              </View>
            </View>
          );
        })}
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  row: { flexDirection: "row", alignItems: "center", gap: 10 },
  dot: { width: 8, height: 8, borderRadius: 2 },
  label: {
    flex: 1,
    fontSize: 13,
    fontFamily: "PlusJakartaSans_500Medium",
    color: colors.text.primary,
  },
  pct: {
    fontSize: 12,
    fontFamily: "PlusJakartaSans_700Bold",
  },
  tile: {
    width: 32,
    height: 32,
    borderRadius: 8,
    alignItems: "center",
    justifyContent: "center",
  },
  tileLabel: {
    fontSize: 14,
    fontFamily: "PlusJakartaSans_600SemiBold",
    color: colors.text.primary,
  },
  tileSub: {
    fontSize: 11,
    color: colors.text.secondary,
    fontFamily: "PlusJakartaSans_400Regular",
  },
  barTrack: {
    backgroundColor: "#F1F5F3",
    height: 4,
    borderRadius: 999,
    marginTop: 6,
    overflow: "hidden",
  },
});
