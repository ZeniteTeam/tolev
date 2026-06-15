import { Film, Home, LucideIcon, MoreHorizontal, ShoppingCart, Truck } from "lucide-react-native";
import { Text, View } from "react-native";
import { Donut } from "../../../components";

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
    <View className="flex-row items-center gap-4">
      <Donut
        segments={GASTOS.map((c) => ({ value: c.value, color: c.color }))}
        size={88}
        stroke={10}
        centerValue={`R$ ${(total / 1000).toFixed(1)}k`}
      />
      <View className="flex-1 gap-2">
        {top3.map((c) => {
          const pct = Math.round((c.value / total) * 100);
          return (
            <View key={c.label} className="flex-row items-center gap-2.5">
              <View className="w-2 h-2 rounded-[2px]" style={{ backgroundColor: c.color }} />
              <Text className="flex-1 text-sm font-medium text-ink" numberOfLines={1}>{c.label}</Text>
              <Text className="text-[12px] font-bold" style={{ color: c.color }}>{pct}%</Text>
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
    <View className="gap-4">
      <View className="items-center">
        <Donut
          segments={GASTOS.map((c) => ({ value: c.value, color: c.color }))}
          size={140}
          stroke={14}
          centerLabel="Total"
          centerValue={`R$ ${(total / 1000).toFixed(1)}k`}
        />
      </View>
      <View className="gap-3">
        {sorted.map((c) => {
          const pct = Math.round((c.value / total) * 100);
          const Icon = c.icon;
          return (
            <View key={c.label}>
              <View className="flex-row items-center gap-3">
                <View className="w-8 h-8 rounded-sm items-center justify-center" style={{ backgroundColor: c.color }}>
                  <Icon size={16} color="#fff" strokeWidth={2} />
                </View>
                <View className="flex-1">
                  <Text className="text-[14px] font-semibold text-ink">{c.label}</Text>
                  <Text className="text-xs text-muted font-regular">R$ {c.value.toLocaleString("pt-BR")}</Text>
                </View>
                <Text className="text-[12px] font-bold" style={{ color: c.color }}>{pct}%</Text>
              </View>
              <View className="bg-[#F1F5F3] h-1 rounded-pill mt-1.5 overflow-hidden ml-11">
                <View className="h-full rounded-pill" style={{ width: `${pct}%`, backgroundColor: c.color }} />
              </View>
            </View>
          );
        })}
      </View>
    </View>
  );
}
