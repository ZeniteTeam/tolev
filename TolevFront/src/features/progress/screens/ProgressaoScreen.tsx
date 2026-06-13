import { LinearGradient } from "expo-linear-gradient";
import { Award, CheckCircle, ChevronRight, Gift, Zap } from "lucide-react-native";
import { StyleSheet, Text, View } from "react-native";
import { PageTitle, Progress, Ring, Screen } from "../../../components";
import { colors, shadows } from "../../../theme";

export default function ProgressaoScreen() {
  return (
    <Screen bottomPad={120}>
      <PageTitle title="Sua progressão" sub="Acompanhe missões, recompensas e conquistas" />

      <View style={{ flexDirection: "row", gap: 12, marginBottom: 16 }}>
        <StatCard value="2" title="Missões" sub="disponíveis" />
        <StatCard value="3" title="Pagamentos" sub="pendentes" />
      </View>

      <LinearGradient
        colors={[colors.primary[25], colors.primary[100]]}
        start={{ x: 0, y: 0 }}
        end={{ x: 1, y: 1 }}
        style={styles.statusCard}
      >
        <Ring size="lg" style={{ backgroundColor: "#fff" }}>
          <CheckCircle size={28} color={colors.primary[700]} strokeWidth={2} />
        </Ring>
        <View style={{ flex: 1 }}>
          <Text style={styles.statusTitle}>Tudo em dia!</Text>
          <Text style={styles.statusSub}>Suas contas desse mês estão organizadas</Text>
        </View>
      </LinearGradient>

      <View style={[styles.card, shadows.card]}>
        <View style={{ flexDirection: "row", justifyContent: "space-between", alignItems: "flex-start" }}>
          <View>
            <Text style={styles.muted}>Pontuação Tolev</Text>
            <Text style={styles.scoreValue}>720</Text>
            <Text style={styles.scoreSub}>+45 pontos esse mês</Text>
          </View>
          <Ring size="lg"><Award size={28} color={colors.primary[700]} strokeWidth={2} /></Ring>
        </View>
        <View style={{ marginTop: 18 }}>
          <Progress pct={72} height={10} />
        </View>
        <View style={styles.scoreLevels}>
          <Text style={styles.levelLabel}>Iniciante</Text>
          <Text style={[styles.levelLabel, { color: colors.teal[500], fontFamily: "PlusJakartaSans_600SemiBold" }]}>Resiliente</Text>
          <Text style={styles.levelLabel}>Mestre</Text>
        </View>
      </View>

      <View style={[styles.card, shadows.card]}>
        <View style={{ flexDirection: "row", alignItems: "center", gap: 14, marginBottom: 14 }}>
          <Ring><Gift size={22} color={colors.primary[700]} strokeWidth={2} /></Ring>
          <View style={{ flex: 1 }}>
            <Text style={styles.cardTitle}>Próximo desbloqueio</Text>
            <Text style={styles.muted}>Miniatura de Banco Central</Text>
          </View>
        </View>
        <Progress pct={78} height={8} />
        <Text style={[styles.muted, { marginTop: 8 }]}>Faltam 2 contas pagas em dia</Text>
      </View>

      <View style={[styles.card, shadows.card, { flexDirection: "row", alignItems: "center", gap: 14 }]}>
        <Ring><Zap size={22} color={colors.primary[700]} strokeWidth={2} /></Ring>
        <View style={{ flex: 1 }}>
          <Text style={styles.cardTitle}>Próximos passos</Text>
          <Text style={styles.muted}>Revise seu planejamento mensal · +1 construção especial</Text>
        </View>
        <ChevronRight size={20} color={colors.text.secondary} strokeWidth={2} />
      </View>
    </Screen>
  );
}

function StatCard({ value, title, sub }: { value: string; title: string; sub: string }) {
  return (
    <View style={[styles.statCard, shadows.card]}>
      <Text style={styles.statValue}>{value}</Text>
      <Text style={styles.statTitle}>{title}</Text>
      <Text style={styles.muted}>{sub}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  statCard: { flex: 1, backgroundColor: "#fff", borderRadius: 18, padding: 18 },
  statValue: { fontFamily: "PlusJakartaSans_700Bold", fontSize: 32, color: colors.primary[700], lineHeight: 36 },
  statTitle: { fontSize: 13, color: colors.text.primary, fontFamily: "PlusJakartaSans_600SemiBold", marginTop: 8 },
  muted: { fontSize: 12, color: colors.text.secondary, fontFamily: "PlusJakartaSans_400Regular" },
  statusCard: {
    borderRadius: 18,
    padding: 20,
    flexDirection: "row",
    alignItems: "center",
    gap: 14,
    marginBottom: 14,
  },
  statusTitle: { fontFamily: "PlusJakartaSans_700Bold", fontSize: 17, color: colors.primary[700] },
  statusSub: { fontSize: 13, color: colors.text.primary, marginTop: 2, fontFamily: "PlusJakartaSans_400Regular" },
  card: { backgroundColor: "#fff", borderRadius: 18, padding: 20, marginBottom: 14 },
  cardTitle: { fontFamily: "PlusJakartaSans_700Bold", fontSize: 15, color: colors.text.primary },
  scoreValue: { fontFamily: "PlusJakartaSans_700Bold", fontSize: 32, color: colors.primary[700], marginTop: 4 },
  scoreSub: { fontSize: 12, color: colors.teal[500], fontFamily: "PlusJakartaSans_600SemiBold", marginTop: 4 },
  scoreLevels: { flexDirection: "row", justifyContent: "space-between", marginTop: 8 },
  levelLabel: { fontSize: 12, color: colors.text.secondary, fontFamily: "PlusJakartaSans_400Regular" },
});
