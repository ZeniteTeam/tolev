import { Calendar, Gift, MoreVertical, Target } from "lucide-react-native";
import { StyleSheet, Text, View } from "react-native";
import { Button, LineChart, PageTitle, QuoteCard, Ring, Screen, Stars } from "../../../components";
import { colors, shadows } from "../../../theme";

export default function MetaExpandidaScreen() {
  const months = ["Jun/25", "Set/25", "Dez/25", "Mar/26", "Ago/26"];
  return (
    <Screen bottomPad={140}>
      <View style={{ flexDirection: "row", alignItems: "center", gap: 12, marginBottom: 18 }}>
        <Ring style={{ width: 48, height: 48, borderRadius: 24 }}>
          <Target size={26} color={colors.primary[700]} strokeWidth={2} />
        </Ring>
        <View style={{ flex: 1 }}>
          <Text style={styles.eyebrow}>META EM PROGRESSO</Text>
          <Text style={styles.heading}>Comprar um carro</Text>
        </View>
        <MoreVertical size={20} color={colors.text.secondary} strokeWidth={2} />
      </View>

      <View style={{ gap: 12, marginBottom: 18 }}>
        <QuoteCard variant="primary">
          Quero um carro para proporcionar mais conforto para a minha família
        </QuoteCard>
        <QuoteCard variant="accent">
          Quando eu conseguir, vou levar meus filhos para conhecer o mar
        </QuoteCard>
      </View>

      <View style={[styles.card, shadows.card]}>
        <View style={{ flexDirection: "row", justifyContent: "space-between", alignItems: "flex-start" }}>
          <View>
            <Text style={styles.muted}>Acumulado</Text>
            <Text style={styles.amount}>R$ 40.000</Text>
            <Text style={styles.muted}>de R$ 70.000</Text>
          </View>
          <View style={styles.pctBadge}>
            <Text style={styles.pctBadgeText}>60%</Text>
          </View>
        </View>

        <View style={{ marginTop: 16 }}>
          <LineChart
            values={[5, 18, 30, 45, 60]}
            color={colors.teal[500]}
            showGoalLine
          />
          <View style={{ flexDirection: "row", justifyContent: "space-between", paddingHorizontal: 4, marginTop: 4 }}>
            {months.map((m) => (
              <Text key={m} style={styles.axisLabel}>{m}</Text>
            ))}
          </View>
        </View>
      </View>

      <View style={[styles.card, shadows.card, { flexDirection: "row", alignItems: "center", gap: 16 }]}>
        <Ring><Calendar size={22} color={colors.primary[700]} strokeWidth={2} /></Ring>
        <View style={{ flex: 1 }}>
          <Text style={styles.muted}>Período da meta</Text>
          <View style={{ flexDirection: "row", alignItems: "baseline", gap: 8, marginTop: 4 }}>
            <Text style={styles.dateText}>01/06/2025</Text>
            <Text style={styles.muted}>—</Text>
            <Text style={styles.dateText}>01/08/2026</Text>
          </View>
        </View>
      </View>

      <View style={{ flexDirection: "row", gap: 12, marginBottom: 16 }}>
        <View style={[styles.miniCard, shadows.card]}>
          <Text style={styles.miniLabel}>Nível de{"\n"}comprometimento</Text>
          <View style={{ alignItems: "center", marginVertical: 10 }}><Stars /></View>
          <Text style={styles.miniValueCoral}>Excelente</Text>
        </View>
        <View style={[styles.miniCard, shadows.card]}>
          <Text style={styles.miniLabel}>Recompensa ao{"\n"}concluir</Text>
          <View style={{ alignItems: "center", marginVertical: 10 }}>
            <Ring><Gift size={22} color={colors.primary[700]} strokeWidth={2} /></Ring>
          </View>
          <Text style={styles.miniValueTeal}>+2 pontos de{"\n"}resiliência</Text>
        </View>
      </View>

      <Button variant="primary">Adicionar valor</Button>
    </Screen>
  );
}

const styles = StyleSheet.create({
  eyebrow: {
    fontSize: 11,
    color: colors.text.secondary,
    letterSpacing: 0.5,
    fontFamily: "PlusJakartaSans_600SemiBold",
  },
  heading: {
    fontFamily: "PlusJakartaSans_700Bold",
    fontSize: 22,
    color: colors.text.primary,
    marginTop: 2,
  },
  card: {
    backgroundColor: "#fff",
    borderRadius: 18,
    padding: 20,
    marginBottom: 14,
  },
  muted: {
    fontSize: 13,
    color: colors.text.secondary,
    fontFamily: "PlusJakartaSans_400Regular",
  },
  amount: {
    fontFamily: "PlusJakartaSans_700Bold",
    fontSize: 28,
    color: colors.primary[700],
    marginTop: 4,
  },
  pctBadge: {
    backgroundColor: colors.primary[100],
    paddingHorizontal: 14,
    paddingVertical: 8,
    borderRadius: 999,
  },
  pctBadgeText: {
    fontFamily: "PlusJakartaSans_700Bold",
    fontSize: 16,
    color: colors.primary[700],
  },
  axisLabel: {
    fontSize: 11,
    color: colors.text.secondary,
    fontFamily: "PlusJakartaSans_400Regular",
  },
  dateText: { fontFamily: "PlusJakartaSans_700Bold", fontSize: 15, color: colors.text.primary },
  miniCard: {
    flex: 1,
    backgroundColor: "#fff",
    borderRadius: 18,
    padding: 18,
    alignItems: "center",
  },
  miniLabel: {
    fontSize: 12,
    color: colors.text.secondary,
    fontFamily: "PlusJakartaSans_400Regular",
    textAlign: "center",
  },
  miniValueCoral: {
    color: colors.coral[500],
    fontFamily: "PlusJakartaSans_700Bold",
    fontSize: 14,
    textAlign: "center",
  },
  miniValueTeal: {
    color: colors.teal[500],
    fontFamily: "PlusJakartaSans_700Bold",
    fontSize: 13,
    textAlign: "center",
  },
});
