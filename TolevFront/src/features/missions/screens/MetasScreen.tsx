import { useNavigation } from "@react-navigation/native";
import { Monitor, Target } from "lucide-react-native";
import { StyleSheet, Text, View } from "react-native";
import { FAB, PageTitle, Screen } from "../../../components";
import { colors } from "../../../theme";
import { GoalCard, GoalCardCompleted } from "../components/GoalCard";

export default function MetasScreen() {
  const navigation = useNavigation<any>();
  return (
    <View style={{ flex: 1, backgroundColor: colors.background }}>
      <Screen bottomPad={140}>
        <PageTitle title="Suas metas" sub="Acompanhe sua jornada rumo aos seus sonhos" />

        <SectionHeader title="Em progresso" sub="2 ativas" />
        <GoalCard
          title="Comprar um carro"
          pct={60}
          valorAtual="R$ 40.000"
          valorFinal="R$ 70.000"
          icon={Target}
          onPress={() => navigation.navigate("MetaExpandida")}
        />
        <GoalCard
          title="Montar meu computador"
          pct={10}
          valorAtual="R$ 2.000"
          valorFinal="R$ 20.000"
          icon={Monitor}
        />

        <SectionHeader title="Concluídas" sub="1 alcançada" style={{ marginTop: 16 }} />
        <GoalCardCompleted title="Viagem para Salvador" date="Mar/2026" />
      </Screen>
      <FAB onPress={() => navigation.navigate("CriarMeta")} />
    </View>
  );
}

function SectionHeader({ title, sub, style }: { title: string; sub: string; style?: any }) {
  return (
    <View style={[styles.section, style]}>
      <Text style={styles.title}>{title}</Text>
      <Text style={styles.sub}>{sub}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  section: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "baseline",
    marginBottom: 12,
    paddingLeft: 4,
  },
  title: {
    fontSize: 11,
    color: colors.text.secondary,
    fontFamily: "PlusJakartaSans_700Bold",
    letterSpacing: 0.6,
  },
  sub: { fontSize: 11, color: colors.text.secondary, fontFamily: "PlusJakartaSans_400Regular" },
});
