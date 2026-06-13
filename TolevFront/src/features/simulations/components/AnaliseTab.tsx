import { useState } from "react";
import { StyleSheet, Text, View } from "react-native";
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
    <View style={{ paddingTop: 18 }}>
      <Text style={styles.label}>Filtrar por banco</Text>
      <BankFilter active={bank} onChange={setBank} />

      <View style={[styles.card, shadows.card]}>
        <Text style={styles.cardTitle}>
          {pieIdx === 0 ? "Distribuição de gastos" : "Receitas vs. Despesas"}
        </Text>
        <Text style={styles.cardSub}>Último mês</Text>

        <View style={{ marginTop: 16 }}>
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

      <View style={[styles.card, shadows.card]}>
        <View style={{ flexDirection: "row", justifyContent: "space-between" }}>
          <View>
            <Text style={styles.cardTitle}>Progresso de dívidas</Text>
            <Text style={styles.cardSub}>
              {chartIdx === 0 ? "Linha do tempo" : "Comparativo mensal"}
            </Text>
          </View>
          <Text style={styles.pct}>65%</Text>
        </View>

        <View style={{ marginTop: 12 }}>
          {chartIdx === 0 && (
            <LineChart values={[15, 22, 30, 38, 50, 58, 65]} color={colors.teal[500]} />
          )}
          {chartIdx === 1 && <DebtBars />}
        </View>

        <DotPagination count={2} active={chartIdx} onChange={setChartIdx} />
        <View style={{ marginTop: 8 }}>
          <PeriodFilter active={periodo} onChange={setPeriodo} />
        </View>
      </View>

      <View style={[styles.card, shadows.card]}>
        <Text style={styles.cardTitle}>Vazamentos financeiros</Text>
        <Text style={styles.cardSub}>Gastos categorizados</Text>
        <View style={{ marginTop: 12, gap: 10 }}>
          {[
            ["Mercado", 100, 0.86],
            ["Streaming", 50, 0.42],
            ["Entretenimento", 105, 0.9],
            ["Transporte", 33, 0.28],
            ["Outros", 45, 0.38],
          ].map(([label, value, w]) => (
            <View key={label as string} style={styles.hbarRow}>
              <Text style={styles.hbarLabel}>{label}</Text>
              <View style={styles.hbarTrack}>
                <View style={[styles.hbarFill, { width: `${(w as number) * 100}%` }]}>
                  <Text style={styles.hbarVal}>R$ {value}</Text>
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
      <View style={{ flexDirection: "row", alignItems: "flex-end", height: 160, gap: 8 }}>
        {months.map(({ m, v }, i) => (
          <View key={m} style={{ flex: 1, alignItems: "center", justifyContent: "flex-end" }}>
            <View
              style={{
                width: "70%",
                height: `${v * 100}%`,
                backgroundColor: i === months.length - 1 ? colors.teal[500] : colors.teal[300],
                borderTopLeftRadius: 6,
                borderTopRightRadius: 6,
              }}
            />
          </View>
        ))}
      </View>
      <View style={{ flexDirection: "row", marginTop: 8, gap: 8 }}>
        {months.map(({ m }) => (
          <Text key={m} style={{ flex: 1, textAlign: "center", fontSize: 10, color: colors.text.secondary }}>{m}</Text>
        ))}
      </View>
    </View>
  );
}

function PieView({ segments }: { segments: { label: string; value: number; color: string }[] }) {
  const total = segments.reduce((s, x) => s + x.value, 0);
  return (
    <View style={{ flexDirection: "row", gap: 18, alignItems: "center" }}>
      <Donut
        segments={segments.map(s => ({ value: s.value, color: s.color }))}
        size={140}
        stroke={20}
        centerLabel="Total"
        centerValue={`R$ ${total}`}
      />
      <View style={{ flex: 1, gap: 8 }}>
        {segments.map(s => {
          const pct = Math.round((s.value / total) * 100);
          return (
            <View key={s.label} style={{ flexDirection: "row", alignItems: "center", gap: 10 }}>
              <View style={{ width: 10, height: 10, borderRadius: 3, backgroundColor: s.color }} />
              <Text style={{ flex: 1, fontSize: 12, color: colors.text.primary, fontFamily: "PlusJakartaSans_400Regular" }}>{s.label}</Text>
              <Text style={{ fontSize: 12, color: colors.text.secondary, fontFamily: "PlusJakartaSans_700Bold" }}>{pct}%</Text>
            </View>
          );
        })}
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  label: {
    fontSize: 13,
    color: colors.text.primary,
    fontFamily: "PlusJakartaSans_600SemiBold",
    marginBottom: 8,
    paddingLeft: 4,
  },
  card: {
    backgroundColor: "#fff",
    borderRadius: 18,
    padding: 20,
    marginBottom: 14,
  },
  cardTitle: { fontFamily: "PlusJakartaSans_700Bold", fontSize: 16, color: colors.text.primary },
  cardSub: { fontSize: 12, color: colors.text.secondary, marginTop: 2, fontFamily: "PlusJakartaSans_400Regular" },
  pct: { fontFamily: "PlusJakartaSans_700Bold", fontSize: 18, color: colors.teal[500] },
  hbarRow: { flexDirection: "row", alignItems: "center", gap: 12 },
  hbarLabel: { width: 100, fontFamily: "PlusJakartaSans_600SemiBold", fontSize: 13, color: colors.text.primary, textAlign: "right" },
  hbarTrack: { flex: 1, height: 22, backgroundColor: "#F1F5F3", borderRadius: 4, overflow: "hidden" },
  hbarFill: {
    height: "100%",
    backgroundColor: colors.primary[500],
    borderRadius: 4,
    justifyContent: "center",
    alignItems: "flex-end",
    paddingHorizontal: 10,
  },
  hbarVal: { color: "#fff", fontFamily: "PlusJakartaSans_700Bold", fontSize: 12 },
});
