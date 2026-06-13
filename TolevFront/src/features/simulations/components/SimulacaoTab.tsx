import { useState } from "react";
import { StyleSheet, Text, View } from "react-native";
import { Button, Chip, Field } from "../../../components";
import { colors, shadows } from "../../../theme";

type Filters = {
  categoria: string;
  periodo: string;
  valor: string;
};

type Props = {
  onSubmit: (filters: Filters) => void;
};

export default function SimulacaoTab({ onSubmit }: Props) {
  const [cat, setCat] = useState<"dividas" | "evolucao" | "gastos">("dividas");
  const [periodo, setPeriodo] = useState<"6m" | "1a" | "3a">("6m");
  const [valor, setValor] = useState("R$ 500,00");

  const submit = () =>
    onSubmit({
      categoria: cat === "dividas" ? "Dívidas" : cat === "evolucao" ? "Evolução" : "Gastos",
      periodo: periodo === "6m" ? "6 meses" : periodo === "1a" ? "1 ano" : "3 anos",
      valor: `${valor}/mês`,
    });

  return (
    <View style={{ paddingTop: 18 }}>
      <Text style={styles.intro}>Configure filtros para prever impactos financeiros</Text>

      <View style={[styles.card, shadows.card]}>
        <Text style={styles.cardTitle}>Categoria</Text>
        <Text style={styles.cardSub}>Qual área deseja simular?</Text>
        <View style={styles.chips}>
          <Chip active={cat === "dividas"} onPress={() => setCat("dividas")}>Dívidas</Chip>
          <Chip active={cat === "evolucao"} onPress={() => setCat("evolucao")}>Evolução</Chip>
          <Chip active={cat === "gastos"} onPress={() => setCat("gastos")}>Gastos</Chip>
        </View>
      </View>

      <View style={[styles.card, shadows.card]}>
        <Text style={styles.cardTitle}>Período</Text>
        <Text style={styles.cardSub}>Quanto tempo deseja simular?</Text>
        <View style={styles.chips}>
          <Chip active={periodo === "6m"} onPress={() => setPeriodo("6m")}>6 meses</Chip>
          <Chip active={periodo === "1a"} onPress={() => setPeriodo("1a")}>1 ano</Chip>
          <Chip active={periodo === "3a"} onPress={() => setPeriodo("3a")}>3 anos</Chip>
        </View>
      </View>

      <View style={[styles.card, shadows.card]}>
        <Text style={styles.cardTitle}>Valor mensal</Text>
        <Text style={styles.cardSub}>Quanto pretende dedicar?</Text>
        <Field value={valor} onChangeText={setValor} />
      </View>

      <Button variant="primary" onPress={submit} style={{ marginTop: 8 }}>Iniciar Simulação</Button>
    </View>
  );
}

const styles = StyleSheet.create({
  intro: {
    fontSize: 13,
    color: colors.text.secondary,
    marginBottom: 18,
    fontFamily: "PlusJakartaSans_400Regular",
    lineHeight: 18,
  },
  card: {
    backgroundColor: "#fff",
    borderRadius: 18,
    padding: 18,
    marginBottom: 14,
  },
  cardTitle: {
    fontFamily: "PlusJakartaSans_700Bold",
    fontSize: 15,
    color: colors.primary[700],
    marginBottom: 4,
  },
  cardSub: {
    fontSize: 12,
    color: colors.text.secondary,
    marginBottom: 14,
    fontFamily: "PlusJakartaSans_400Regular",
  },
  chips: {
    flexDirection: "row",
    flexWrap: "wrap",
    gap: 8,
  },
});
