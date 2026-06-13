import { useNavigation } from "@react-navigation/native";
import { LinearGradient } from "expo-linear-gradient";
import {
  ArrowDownRight,
  ArrowUpRight,
  Award,
  ChevronRight,
  Eye,
  Lightbulb,
  Monitor,
  Target,
  Thermometer,
  TrendingUp,
  type LucideIcon,
} from "lucide-react-native";
import { useState } from "react";
import { Pressable, StyleSheet, Text, View } from "react-native";
import { BankFilter, Progress, Ring, Screen } from "../../../components";
import type { BankId } from "../../../components/BankFilter";
import { colors, shadows } from "../../../theme";
import { CategoriaGastosCompact } from "../components/CategoriaGastos";

export default function HomeScreen() {
  const navigation = useNavigation<any>();
  const [bank, setBank] = useState<BankId>("all");

  return (
    <Screen bottomPad={120}>
      <BankFilter active={bank} onChange={setBank} />

      <LinearGradient
        colors={[colors.primary[700], colors.primary[600]]}
        start={{ x: 0, y: 0 }}
        end={{ x: 1, y: 1 }}
        style={[styles.balanceCard, shadows.card]}
      >
        <View style={styles.balanceTopRow}>
          <Text style={styles.balanceTitle}>Balanço do mês</Text>
          <Eye size={18} color="#fff" strokeWidth={2} />
        </View>
        <Text style={styles.balanceAmount}>R$ 1.240,00</Text>
        <View style={{ flexDirection: "row", alignItems: "center", gap: 6, marginTop: 8 }}>
          <TrendingUp size={14} color={colors.primary[300]} strokeWidth={2} />
          <Text style={styles.balanceSub}>+12% em relação ao mês anterior</Text>
        </View>

        <View style={styles.balanceGrid}>
          <View style={{ flex: 1 }}>
            <View style={{ flexDirection: "row", alignItems: "center", gap: 6 }}>
              <ArrowUpRight size={14} color={colors.primary[300]} strokeWidth={2} />
              <Text style={styles.gridLabel}>Receitas</Text>
            </View>
            <Text style={styles.gridValue}>R$ 4.500</Text>
          </View>
          <View style={styles.gridDivider} />
          <View style={{ flex: 1 }}>
            <View style={{ flexDirection: "row", alignItems: "center", gap: 6 }}>
              <ArrowDownRight size={14} color={colors.coral[300]} strokeWidth={2} />
              <Text style={styles.gridLabel}>Despesas</Text>
            </View>
            <Text style={styles.gridValue}>R$ 3.260</Text>
          </View>
        </View>
      </LinearGradient>

      <SectionLink title="Onde seu dinheiro vai" sub="Despesas do mês" onPress={() => navigation.navigate("Financas")}>
        <CategoriaGastosCompact />
      </SectionLink>

      <SectionLink title="Suas metas" sub="3 ativas" onPress={() => navigation.navigate("Metas")}>
        <View style={{ gap: 14 }}>
          <MetaPreview icon={Target} title="Comprar um carro" pct={60} />
          <MetaPreview icon={Monitor} title="Montar meu computador" pct={10} />
        </View>
      </SectionLink>

      <SectionLink title="Sua progressão" sub="720 pontos" onPress={() => navigation.navigate("Progresso")}>
        <View style={{ flexDirection: "row", alignItems: "center", gap: 14 }}>
          <Ring size="lg"><Award size={28} color={colors.primary[700]} strokeWidth={2} /></Ring>
          <View style={{ flex: 1 }}>
            <Text style={styles.miniLabel}>Nível</Text>
            <Text style={styles.miniValue}>Resiliente</Text>
            <View style={{ marginTop: 10 }}>
              <Progress pct={72} height={8} />
            </View>
            <Text style={styles.miniSub}>280 pontos para o próximo nível</Text>
          </View>
        </View>
      </SectionLink>

      <SectionLink title="Termômetro da dívida" sub="40% do caminho" onPress={() => navigation.navigate("Financas")}>
        <View style={{ flexDirection: "row", alignItems: "center", gap: 14 }}>
          <Ring size="lg"><Thermometer size={26} color={colors.primary[700]} strokeWidth={2} /></Ring>
          <View style={{ flex: 1 }}>
            <Text style={styles.miniLabel}>Faltam para zerar</Text>
            <Text style={[styles.miniValue, { color: colors.coral[500], fontSize: 22 }]}>7 meses</Text>
            <View style={{ marginTop: 10 }}>
              <Progress pct={40} height={8} />
            </View>
          </View>
        </View>
      </SectionLink>

      <View style={styles.dica}>
        <Ring style={{ backgroundColor: "#fff" }}>
          <Lightbulb size={22} color={colors.primary[700]} strokeWidth={2} />
        </Ring>
        <View style={{ flex: 1 }}>
          <Text style={styles.dicaTag}>DICA TOLEV</Text>
          <Text style={styles.dicaBody}>
            Dedicando mais <Text style={{ color: colors.primary[700], fontFamily: "PlusJakartaSans_700Bold" }}>R$ 100/mês</Text>, você pode alcançar seus objetivos <Text style={{ color: colors.coral[500], fontFamily: "PlusJakartaSans_700Bold" }}>10% mais rápido</Text>.
          </Text>
        </View>
      </View>
    </Screen>
  );
}

function SectionLink({
  title,
  sub,
  onPress,
  children,
}: {
  title: string;
  sub?: string;
  onPress?: () => void;
  children: React.ReactNode;
}) {
  return (
    <Pressable onPress={onPress} style={[styles.sectionCard, shadows.card]}>
      <View style={styles.sectionHeader}>
        <View>
          <Text style={styles.sectionTitle}>{title}</Text>
          {sub && <Text style={styles.sectionSub}>{sub}</Text>}
        </View>
        <ChevronRight size={20} color={colors.text.secondary} strokeWidth={2} />
      </View>
      {children}
    </Pressable>
  );
}

function MetaPreview({ icon: Icon, title, pct }: { icon: LucideIcon; title: string; pct: number }) {
  return (
    <View style={{ flexDirection: "row", alignItems: "center", gap: 12 }}>
      <Ring style={{ width: 38, height: 38, borderRadius: 19 }}>
        <Icon size={18} color={colors.primary[700]} strokeWidth={2} />
      </Ring>
      <View style={{ flex: 1 }}>
        <View style={{ flexDirection: "row", justifyContent: "space-between", marginBottom: 6 }}>
          <Text style={styles.metaTitle} numberOfLines={1}>{title}</Text>
          <Text style={styles.metaPct}>{pct}%</Text>
        </View>
        <Progress pct={pct} height={6} />
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  balanceCard: {
    borderRadius: 18,
    padding: 22,
    marginBottom: 18,
    marginTop: 4,
  },
  balanceTopRow: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: 14,
  },
  balanceTitle: { color: "rgba(255,255,255,0.85)", fontSize: 13, fontFamily: "PlusJakartaSans_400Regular" },
  balanceAmount: { color: "#fff", fontSize: 32, fontFamily: "PlusJakartaSans_700Bold", lineHeight: 36 },
  balanceSub: { color: "rgba(255,255,255,0.9)", fontSize: 13, fontFamily: "PlusJakartaSans_400Regular" },
  balanceGrid: {
    flexDirection: "row",
    marginTop: 18,
    paddingTop: 16,
    borderTopWidth: 1,
    borderTopColor: "rgba(255,255,255,0.18)",
    gap: 14,
  },
  gridDivider: { width: 1, backgroundColor: "rgba(255,255,255,0.18)" },
  gridLabel: { color: "rgba(255,255,255,0.82)", fontSize: 12, fontFamily: "PlusJakartaSans_400Regular" },
  gridValue: { color: "#fff", fontSize: 17, fontFamily: "PlusJakartaSans_700Bold", marginTop: 4 },
  sectionCard: {
    backgroundColor: colors.surface,
    borderRadius: 18,
    padding: 18,
    marginBottom: 14,
  },
  sectionHeader: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: 16,
  },
  sectionTitle: {
    fontFamily: "PlusJakartaSans_700Bold",
    fontSize: 16,
    color: colors.text.primary,
  },
  sectionSub: {
    fontSize: 12,
    color: colors.text.secondary,
    marginTop: 2,
    fontFamily: "PlusJakartaSans_400Regular",
  },
  miniLabel: { fontSize: 12, color: colors.text.secondary, fontFamily: "PlusJakartaSans_400Regular" },
  miniValue: { fontFamily: "PlusJakartaSans_700Bold", fontSize: 17, color: colors.primary[700], marginTop: 2 },
  miniSub: { fontSize: 11, color: colors.text.secondary, marginTop: 6, fontFamily: "PlusJakartaSans_400Regular" },
  metaTitle: { fontSize: 13, fontFamily: "PlusJakartaSans_600SemiBold", color: colors.text.primary, flex: 1, marginRight: 8 },
  metaPct: { fontSize: 12, color: colors.teal[500], fontFamily: "PlusJakartaSans_700Bold" },
  dica: {
    backgroundColor: colors.primary[25],
    borderRadius: 18,
    padding: 18,
    flexDirection: "row",
    gap: 14,
    alignItems: "flex-start",
  },
  dicaTag: {
    fontSize: 11,
    color: colors.primary[700],
    fontFamily: "PlusJakartaSans_700Bold",
    letterSpacing: 0.6,
  },
  dicaBody: {
    fontSize: 14,
    color: colors.text.primary,
    lineHeight: 20,
    marginTop: 6,
    fontFamily: "PlusJakartaSans_400Regular",
  },
});
