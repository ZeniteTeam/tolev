import { Award, CheckCircle, Gift, MoreVertical, type LucideIcon } from "lucide-react-native";
import { Pressable, StyleSheet, Text, View } from "react-native";
import { Progress } from "../../../components";
import { colors, shadows } from "../../../theme";

type Props = {
  title: string;
  pct: number;
  valorAtual: string;
  valorFinal: string;
  icon: LucideIcon;
  onPress?: () => void;
};

export function GoalCard({ title, pct, valorAtual, valorFinal, icon: Icon, onPress }: Props) {
  return (
    <Pressable onPress={onPress} style={[styles.card, shadows.card]}>
      <View style={styles.row}>
        <View style={styles.iconTile}>
          <Icon size={18} color={colors.primary[700]} strokeWidth={2} />
        </View>
        <Text style={styles.title}>{title}</Text>
        <MoreVertical size={18} color={colors.text.secondary} strokeWidth={2} />
      </View>

      <View>
        <View style={{ flexDirection: "row", justifyContent: "space-between", marginBottom: 8 }}>
          <Text style={styles.label}>Progresso</Text>
          <Text style={styles.pct}>{pct}%</Text>
        </View>
        <Progress pct={pct} height={6} />
      </View>

      <View style={{ flexDirection: "row", justifyContent: "space-between" }}>
        <View>
          <Text style={styles.metaLabel}>Valor atual</Text>
          <Text style={styles.metaValue}>{valorAtual}</Text>
        </View>
        <View style={{ alignItems: "flex-end" }}>
          <Text style={styles.metaLabel}>Meta</Text>
          <Text style={styles.metaValue}>{valorFinal}</Text>
        </View>
      </View>
    </Pressable>
  );
}

export function GoalCardCompleted({ title, date }: { title: string; date: string }) {
  return (
    <View style={[styles.card, shadows.card]}>
      <View style={styles.row}>
        <View style={[styles.iconTile, { backgroundColor: colors.primary[700] }]}>
          <Award size={18} color="#fff" strokeWidth={2} />
        </View>
        <Text style={styles.title}>{title}</Text>
        <MoreVertical size={18} color={colors.text.secondary} strokeWidth={2} />
      </View>

      <View style={{ flexDirection: "row", justifyContent: "space-between", alignItems: "center", gap: 12 }}>
        <View style={styles.tag}>
          <CheckCircle size={14} color={colors.primary[700]} strokeWidth={2} />
          <Text style={styles.tagText}>Alcançada em {date}</Text>
        </View>
        <Pressable style={styles.resgatar}>
          <Gift size={14} color="#fff" strokeWidth={2} />
          <Text style={styles.resgatarText}>Resgatar</Text>
        </Pressable>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: "#fff",
    borderRadius: 18,
    padding: 20,
    marginBottom: 14,
    gap: 20,
  },
  row: { flexDirection: "row", alignItems: "center", gap: 12 },
  iconTile: {
    width: 36,
    height: 36,
    borderRadius: 10,
    backgroundColor: colors.primary[100],
    alignItems: "center",
    justifyContent: "center",
  },
  title: {
    flex: 1,
    fontFamily: "PlusJakartaSans_600SemiBold",
    fontSize: 16,
    color: colors.text.primary,
  },
  label: { fontSize: 12, color: colors.text.secondary, fontFamily: "PlusJakartaSans_400Regular" },
  pct: { fontSize: 13, color: colors.teal[500], fontFamily: "PlusJakartaSans_700Bold" },
  metaLabel: { fontSize: 11, color: colors.text.secondary, marginBottom: 4, fontFamily: "PlusJakartaSans_400Regular" },
  metaValue: { fontFamily: "PlusJakartaSans_600SemiBold", fontSize: 14, color: colors.text.primary },
  tag: {
    flexDirection: "row",
    alignItems: "center",
    gap: 6,
    backgroundColor: colors.primary[100],
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 999,
  },
  tagText: { fontFamily: "PlusJakartaSans_600SemiBold", fontSize: 12, color: colors.primary[700] },
  resgatar: {
    flexDirection: "row",
    alignItems: "center",
    gap: 6,
    height: 36,
    paddingHorizontal: 14,
    backgroundColor: colors.coral[500],
    borderRadius: 999,
  },
  resgatarText: { color: "#fff", fontFamily: "PlusJakartaSans_700Bold", fontSize: 13 },
});
