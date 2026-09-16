import { LucideIcon } from "lucide-react-native";
import { Text, View } from "react-native";
import { Donut, GiftedDonut, Progress, Stagger } from "../../../components";
import { motion } from "../../../theme";

/** `color` e `icon` já vêm resolvidos por features/analysis/utils/categoria-view.ts. */
export type Categoria = {
  label: string;
  value: number;
  color: string;
  icon: LucideIcon;
};

type Props = { categorias: Categoria[] };

/**
 * O donut entra um passo antes da legenda: primeiro a forma, depois os nomes.
 * É a ordem em que o gráfico é lido.
 */
const ATRASO_DONUT = motion.stagger.step;
const ATRASO_LEGENDA = motion.stagger.step * 2;

/** Protegido para um mês zerado render 0% e não NaN%. */
function pctDe(value: number, total: number): number {
  return total === 0 ? 0 : Math.round((value / total) * 100);
}

function somar(categorias: Categoria[]): number {
  return categorias.reduce((s, c) => s + c.value, 0);
}

export function CategoriaGastosCompact({ categorias }: Props) {
  const total = somar(categorias);
  const top3 = [...categorias].sort((a, b) => b.value - a.value).slice(0, 3);
  return (
    <View className="flex-row items-center gap-4">
      <Donut
        segments={categorias.map((c) => ({ value: c.value, color: c.color }))}
        size={88}
        stroke={10}
        centerValue={`R$ ${(total / 1000).toFixed(1)}k`}
        delay={ATRASO_DONUT}
      />
      <Stagger className="flex-1 gap-2" delay={ATRASO_LEGENDA}>
        {top3.map((c) => {
          const pct = pctDe(c.value, total);
          return (
            <View key={c.label} className="flex-row items-center gap-2.5">
              <View className="w-2 h-2 rounded-[2px]" style={{ backgroundColor: c.color }} />
              <Text className="flex-1 text-sm font-medium text-ink" numberOfLines={1}>{c.label}</Text>
              <Text className="text-[12px] font-bold" style={{ color: c.color }}>{pct}%</Text>
            </View>
          );
        })}
      </Stagger>
    </View>
  );
}

export function CategoriaGastosDetailed({ categorias }: Props) {
  const total = somar(categorias);
  const sorted = [...categorias].sort((a, b) => b.value - a.value);
  return (
    <View className="gap-4">
      <View className="items-center">
        <GiftedDonut
          data={categorias.map((c) => ({ value: c.value, color: c.color }))}
          size={140}
          stroke={14}
          delay={ATRASO_DONUT}
          center={
            <View className="items-center">
              <Text className="text-[11px] text-muted font-medium">Total</Text>
              <Text className="text-[16px] text-ink font-bold">R$ {(total / 1000).toFixed(1)}k</Text>
            </View>
          }
        />
      </View>
      <Stagger className="gap-3" delay={ATRASO_LEGENDA}>
        {sorted.map((c) => {
          const pct = pctDe(c.value, total);
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
              {/* Mesma barra de antes, agora pelo `Progress`: o preenchimento
                  cresce junto com a entrada da linha em vez de já chegar cheio. */}
              <View className="mt-1.5 ml-11">
                <Progress pct={pct} height={4} trackColor="#F1F5F3" fillColor={c.color} />
              </View>
            </View>
          );
        })}
      </Stagger>
    </View>
  );
}
