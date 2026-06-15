import { useState } from "react";
import { Text, View } from "react-native";
import { BankFilter, DotPagination, Donut, LineChart, PeriodFilter } from "../../../components";
import type { BankId } from "../../../components/BankFilter";
import { colors, shadows } from "../../../theme";
import { CategoriaGastosDetailed } from "../../menu/components/CategoriaGastos";

export default function AnaliseTab() {
  const [bank, setBank] = useState<BankId>("all");
  const [chartIdx, setChartIdx] = useState(0);
  const [pieIdx, setPieIdx] = useState(0);
  const [periodo, setPeriodo] = useState("6m");

  return (
    <View className="pt-[18px]">
      <Text className="text-sm text-ink font-semibold mb-2 pl-1">Filtrar por banco</Text>
      <BankFilter active={bank} onChange={setBank} />

      <View className="bg-white rounded-[18px] p-5 mb-3.5" style={shadows.card}>
        <Text className="font-bold text-[16px] text-ink">
          {pieIdx === 0 ? "Distribuição de gastos" : "Receitas vs. Despesas"}
        </Text>
        <Text className="text-[12px] text-muted mt-0.5 font-regular">Último mês</Text>

        <View className="mt-4">
          {pieIdx === 0 && <CategoriaGastosDetailed />}
          {pieIdx === 1 && (
            <PieView segments={[
              { label: "Receitas", value: 4500, color: colors.primary[500] },
              { label: "Despesas", value: 3260, color: colors.coral[500] },
            ]} />
          )}
        </View>

        <DotPagination count={2} active={pieIdx} onChange={setPieIdx} />
      </View>

      <View className="bg-white rounded-[18px] p-5 mb-3.5" style={shadows.card}>
        <View className="flex-row justify-between">
          <View>
            <Text className="font-bold text-[16px] text-ink">Progresso de dívidas</Text>
            <Text className="text-[12px] text-muted mt-0.5 font-regular">
              {chartIdx === 0 ? "Linha do tempo" : "Comparativo mensal"}
            </Text>
          </View>
          <Text className="font-bold text-[18px] text-teal-500">65%</Text>
        </View>

        <View className="mt-3">
          {chartIdx === 0 && (
            <LineChart values={[15, 22, 30, 38, 50, 58, 65]} color={colors.teal[500]} />
          )}
          {chartIdx === 1 && <DebtBars />}
        </View>

        <DotPagination count={2} active={chartIdx} onChange={setChartIdx} />
        <View className="mt-2">
          <PeriodFilter active={periodo} onChange={setPeriodo} />
        </View>
      </View>

      <View className="bg-white rounded-[18px] p-5 mb-3.5" style={shadows.card}>
        <Text className="font-bold text-[16px] text-ink">Vazamentos financeiros</Text>
        <Text className="text-[12px] text-muted mt-0.5 font-regular">Gastos categorizados</Text>
        <View className="mt-3 gap-2.5">
          {[
            ["Mercado", 100, 0.86],
            ["Streaming", 50, 0.42],
            ["Entretenimento", 105, 0.9],
            ["Transporte", 33, 0.28],
            ["Outros", 45, 0.38],
          ].map(([label, value, w]) => (
            <View key={label as string} className="flex-row items-center gap-3">
              <Text className="w-[100px] font-semibold text-sm text-ink text-right">{label}</Text>
              <View className="flex-1 h-[22px] bg-[#F1F5F3] rounded-sm overflow-hidden">
                <View
                  className="h-full bg-primary-500 rounded-sm justify-center items-end px-2.5"
                  style={{ width: `${(w as number) * 100}%` }}
                >
                  <Text className="text-white font-bold text-[12px]">R$ {value}</Text>
                </View>
              </View>
            </View>
          ))}
        </View>
      </View>
    </View>
  );
}

function DebtBars() {
  const months = [
    { m: "Nov", v: 0.22 },
    { m: "Dez", v: 0.30 },
    { m: "Jan", v: 0.38 },
    { m: "Fev", v: 0.45 },
    { m: "Mar", v: 0.52 },
    { m: "Abr", v: 0.60 },
    { m: "Mai", v: 0.65 },
  ];
  return (
    <View>
      <View className="flex-row items-end h-[160px] gap-2">
        {months.map(({ m, v }, i) => (
          <View key={m} className="flex-1 items-center justify-end">
            <View
              className="w-[70%] rounded-t-md"
              style={{
                height: `${v * 100}%`,
                backgroundColor: i === months.length - 1 ? colors.teal[500] : colors.teal[300],
              }}
            />
          </View>
        ))}
      </View>
      <View className="flex-row mt-2 gap-2">
        {months.map(({ m }) => (
          <Text key={m} className="flex-1 text-center text-[10px] text-muted">{m}</Text>
        ))}
      </View>
    </View>
  );
}

function PieView({ segments }: { segments: { label: string; value: number; color: string }[] }) {
  const total = segments.reduce((s, x) => s + x.value, 0);
  return (
    <View className="flex-row gap-[18px] items-center">
      <Donut
        segments={segments.map(s => ({ value: s.value, color: s.color }))}
        size={140}
        stroke={20}
        centerLabel="Total"
        centerValue={`R$ ${total}`}
      />
      <View className="flex-1 gap-2">
        {segments.map(s => {
          const pct = Math.round((s.value / total) * 100);
          return (
            <View key={s.label} className="flex-row items-center gap-2.5">
              <View className="w-2.5 h-2.5 rounded-[3px]" style={{ backgroundColor: s.color }} />
              <Text className="flex-1 text-[12px] text-ink font-regular">{s.label}</Text>
              <Text className="text-[12px] text-muted font-bold">{pct}%</Text>
            </View>
          );
        })}
      </View>
    </View>
  );
}
