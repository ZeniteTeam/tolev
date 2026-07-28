import { useState } from "react";
import { Text, View } from "react-native";
import { Button, Chip, Field } from "../../../components";
import { shadows } from "../../../theme";

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
    <View className="pt-[18px]">
      <View className="bg-white rounded-[18px] p-[18px] mb-3.5" style={shadows.card}>
        <Text className="font-bold text-[15px] text-primary-700 mb-1">Categoria</Text>
        <Text className="text-[12px] text-muted mb-3.5 font-regular">Qual área deseja simular?</Text>
        <View className="flex-row flex-wrap gap-2">
          <Chip active={cat === "dividas"} onPress={() => setCat("dividas")}>Dívidas</Chip>
          <Chip active={cat === "evolucao"} onPress={() => setCat("evolucao")}>Evolução</Chip>
          <Chip active={cat === "gastos"} onPress={() => setCat("gastos")}>Gastos</Chip>
        </View>
      </View>

      <View className="bg-white rounded-[18px] p-[18px] mb-3.5" style={shadows.card}>
        <Text className="font-bold text-[15px] text-primary-700 mb-1">Período</Text>
        <Text className="text-[12px] text-muted mb-3.5 font-regular">Quanto tempo deseja simular?</Text>
        <View className="flex-row flex-wrap gap-2">
          <Chip active={periodo === "6m"} onPress={() => setPeriodo("6m")}>6 meses</Chip>
          <Chip active={periodo === "1a"} onPress={() => setPeriodo("1a")}>1 ano</Chip>
          <Chip active={periodo === "3a"} onPress={() => setPeriodo("3a")}>3 anos</Chip>
        </View>
      </View>

      <View className="bg-white rounded-[18px] p-[18px] mb-3.5" style={shadows.card}>
        <Text className="font-bold text-[15px] text-primary-700 mb-1">Valor mensal</Text>
        <Text className="text-[12px] text-muted mb-3.5 font-regular">Quanto pretende dedicar?</Text>
        <Field value={valor} onChangeText={setValor} />
      </View>

      <Button variant="primary" onPress={submit} style={{ marginTop: 8 }}>Iniciar Simulação</Button>
    </View>
  );
}
