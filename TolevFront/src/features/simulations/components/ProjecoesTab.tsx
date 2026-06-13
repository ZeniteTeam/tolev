import { LinearGradient } from "expo-linear-gradient";
import { AlertCircle, Smile, Thermometer, Zap } from "lucide-react-native";
import { StyleSheet, Text, View } from "react-native";
import { LineChart, Progress, Ring } from "../../../components";
import { colors, shadows } from "../../../theme";

export default function ProjecoesTab() {
  return (
    <View style={{ paddingTop: 22 }}>
      <LinearGradient
        colors={[colors.primary[700], colors.primary[600]]}
        start={{ x: 0, y: 0 }}
        end={{ x: 1, y: 1 }}
        style={[styles.heroCard, shadows.card]}
      >
        <Text style={styles.heroLabel}>Quitação prevista</Text>
        <Text style={styles.heroValue}>Dez/2026</Text>
        <View
          style={{
            flexDirection: "row",
            alignItems: "center",
            gap: 6,
            marginTop: 6,
          }}
        >
          <Zap size={14} color={colors.primary[300]} strokeWidth={2} />
          <Text style={styles.heroSub}>
            <Text style={{ fontFamily: "PlusJakartaSans_700Bold" }}>
              Passa voando!{" "}
            </Text>
            Faltam 7 meses
          </Text>
        </View>

        <View style={{ marginTop: 14 }}>
          <LineChart
            values={[5, 13, 22, 30, 40, 52, 65, 78, 92, 100]}
            height={120}
            color={colors.primary[300]}
            showFill
          />
        </View>

        <View style={styles.heroGrid}>
          <Stat label="Dívida total" value="R$ 30.000" sub="a quitar" />
          <Stat label="Pagamento mensal" value="R$ 4.285" sub="previsto" />
          <Stat
            label="Juros economizados"
            value="R$ 1.840"
            sub="vs. plano original"
            valueColor={colors.primary[300]}
          />
          <Stat label="Confiança" value="87%" sub="na previsão" />
        </View>
      </LinearGradient>

      <View style={[styles.card, shadows.card]}>
        <View
          style={{
            flexDirection: "row",
            alignItems: "flex-start",
            gap: 14,
            marginBottom: 14,
          }}
        >
          <Ring>
            <Thermometer
              size={22}
              color={colors.primary[700]}
              strokeWidth={2}
            />
          </Ring>
          <View style={{ flex: 1 }}>
            <Text style={styles.cardTitle}>Termômetro da dívida</Text>
            <Text style={styles.cardSub}>
              Você já percorreu{" "}
              <Text
                style={{
                  color: colors.text.primary,
                  fontFamily: "PlusJakartaSans_700Bold",
                }}
              >
                40%
              </Text>
            </Text>
          </View>
        </View>
        <Progress pct={40} height={12} />
        <View
          style={{
            flexDirection: "row",
            justifyContent: "space-between",
            marginTop: 10,
          }}
        >
          <Text style={styles.termoStat}>R$ 20.000 pagos</Text>
          <Text style={styles.termoStat}>R$ 30.000 restantes</Text>
        </View>
      </View>

      <View style={{ flexDirection: "row", gap: 12, marginBottom: 16 }}>
        <View style={[styles.miniCard, shadows.card]}>
          <View
            style={{
              flexDirection: "row",
              alignItems: "center",
              gap: 8,
              marginBottom: 12,
            }}
          >
            <Ring style={{ width: 32, height: 32, borderRadius: 16 }}>
              <Smile size={16} color={colors.teal[500]} strokeWidth={2} />
            </Ring>
            <Text style={styles.miniLabel}>Livre hoje</Text>
          </View>
          <Text style={[styles.miniValue, { color: colors.teal[500] }]}>
            R$ 84
          </Text>
          <Text style={styles.miniSub}>sem comprometer dívidas</Text>
        </View>
        <View style={[styles.miniCard, shadows.card]}>
          <View
            style={{
              flexDirection: "row",
              alignItems: "center",
              gap: 8,
              marginBottom: 12,
            }}
          >
            <View
              style={[
                styles.iconBubble,
                { backgroundColor: "rgba(254,111,80,0.12)" },
              ]}
            >
              <AlertCircle
                size={16}
                color={colors.coral[500]}
                strokeWidth={2}
              />
            </View>
            <Text style={styles.miniLabel}>Comprometido</Text>
          </View>
          <Text style={[styles.miniValue, { color: colors.coral[500] }]}>
            72%
          </Text>
          <Text style={styles.miniSub}>da renda mensal</Text>
        </View>
      </View>

      <View style={[styles.card, shadows.card]}>
        <Text style={styles.cardTitle}>Próximos 6 meses</Text>
        <Text style={[styles.cardSub, { marginBottom: 18 }]}>
          Dívida projetada vs. pagamentos
        </Text>
        <ProjectionBars />
      </View>
    </View>
  );
}

function Stat({
  label,
  value,
  sub,
  valueColor,
}: {
  label: string;
  value: string;
  sub: string;
  valueColor?: string;
}) {
  return (
    <View style={{ width: "48%" }}>
      <Text style={styles.statLabel}>{label}</Text>
      <Text style={[styles.statValue, valueColor && { color: valueColor }]}>
        {value}
      </Text>
      <Text style={styles.statSub}>{sub}</Text>
    </View>
  );
}

function ProjectionBars() {
  const months = [
    { m: "Jun", divida: 26, pagto: 4 },
    { m: "Jul", divida: 22, pagto: 4 },
    { m: "Ago", divida: 18, pagto: 4 },
    { m: "Set", divida: 14, pagto: 4 },
    { m: "Out", divida: 10, pagto: 4 },
    { m: "Nov", divida: 6, pagto: 4 },
  ];
  const max = 30;
  return (
    <View>
      <View
        style={{
          flexDirection: "row",
          alignItems: "flex-end",
          gap: 10,
          height: 140,
        }}
      >
        {months.map(({ m, divida, pagto }) => (
          <View
            key={m}
            style={{
              flex: 1,
              alignItems: "center",
              justifyContent: "flex-end",
              height: "100%",
            }}
          >
            <View
              style={{ width: 24, height: "100%", justifyContent: "flex-end" }}
            >
              <View
                style={{
                  height: `${(divida / max) * 100}%`,
                  backgroundColor: colors.primary[500],
                  borderTopLeftRadius: 6,
                  borderTopRightRadius: 6,
                }}
              />
              <View
                style={{
                  height: `${(pagto / max) * 100}%`,
                  backgroundColor: colors.coral[500],
                }}
              />
            </View>
          </View>
        ))}
      </View>
      <View style={{ flexDirection: "row", gap: 10, marginTop: 8 }}>
        {months.map(({ m }) => (
          <Text
            key={m}
            style={[styles.barAxisLabel, { flex: 1, textAlign: "center" }]}
          >
            {m}
          </Text>
        ))}
      </View>
      <View
        style={{
          flexDirection: "row",
          gap: 16,
          justifyContent: "center",
          marginTop: 14,
        }}
      >
        <Legend color={colors.primary[500]} label="Dívida restante" />
        <Legend color={colors.coral[500]} label="Pagamento" />
      </View>
    </View>
  );
}

function Legend({ color, label }: { color: string; label: string }) {
  return (
    <View style={{ flexDirection: "row", alignItems: "center", gap: 6 }}>
      <View
        style={{
          width: 10,
          height: 10,
          borderRadius: 3,
          backgroundColor: color,
        }}
      />
      <Text
        style={{
          fontSize: 12,
          color: colors.text.secondary,
          fontFamily: "PlusJakartaSans_400Regular",
        }}
      >
        {label}
      </Text>
    </View>
  );
}

const styles = StyleSheet.create({
  heroCard: { borderRadius: 18, padding: 22, marginBottom: 14 },
  heroLabel: {
    color: "rgba(255,255,255,0.85)",
    fontSize: 13,
    fontFamily: "PlusJakartaSans_600SemiBold",
  },
  heroValue: {
    color: "#fff",
    fontSize: 30,
    fontFamily: "PlusJakartaSans_700Bold",
    marginTop: 6,
  },
  heroSub: {
    color: "rgba(255,255,255,0.9)",
    fontSize: 13,
    fontFamily: "PlusJakartaSans_400Regular",
  },
  heroGrid: {
    flexDirection: "row",
    flexWrap: "wrap",
    gap: 12,
    marginTop: 14,
    paddingTop: 16,
    borderTopWidth: 1,
    borderTopColor: "rgba(255,255,255,0.18)",
  },
  statLabel: {
    fontSize: 11,
    color: "rgba(255,255,255,0.78)",
    marginBottom: 4,
    fontFamily: "PlusJakartaSans_400Regular",
  },
  statValue: {
    color: "#fff",
    fontFamily: "PlusJakartaSans_700Bold",
    fontSize: 16,
  },
  statSub: {
    fontSize: 11,
    color: "rgba(255,255,255,0.7)",
    marginTop: 2,
    fontFamily: "PlusJakartaSans_400Regular",
  },
  card: {
    backgroundColor: "#fff",
    borderRadius: 18,
    padding: 20,
    marginBottom: 14,
  },
  cardTitle: {
    fontFamily: "PlusJakartaSans_700Bold",
    fontSize: 16,
    color: colors.text.primary,
  },
  cardSub: {
    fontSize: 12,
    color: colors.text.secondary,
    marginTop: 2,
    fontFamily: "PlusJakartaSans_400Regular",
  },
  termoStat: {
    fontSize: 12,
    color: colors.primary[700],
    fontFamily: "PlusJakartaSans_600SemiBold",
  },
  miniCard: { flex: 1, backgroundColor: "#fff", borderRadius: 18, padding: 18 },
  miniLabel: {
    fontSize: 12,
    color: colors.text.secondary,
    fontFamily: "PlusJakartaSans_400Regular",
  },
  miniValue: { fontFamily: "PlusJakartaSans_700Bold", fontSize: 22 },
  miniSub: {
    fontSize: 11,
    color: colors.text.secondary,
    marginTop: 4,
    fontFamily: "PlusJakartaSans_400Regular",
  },
  iconBubble: {
    width: 32,
    height: 32,
    borderRadius: 16,
    alignItems: "center",
    justifyContent: "center",
  },
  barAxisLabel: {
    fontSize: 11,
    color: colors.text.secondary,
    fontFamily: "PlusJakartaSans_400Regular",
  },
});
