import { useNavigation } from "@react-navigation/native";
import { useState } from "react";
import { View } from "react-native";
import { PageTitle, Screen, TabsUnderline } from "../../../components";
import AnaliseTab from "../components/AnaliseTab";
import PlanejamentoTab from "../components/PlanejamentoTab";

const TABS = [
  { key: "analise", label: "Análise" },
  { key: "planejamento", label: "Planejamento" },
];

export default function FinancasScreen() {
  const navigation = useNavigation<any>();
  const [tab, setTab] = useState("analise");

  return (
    <Screen bottomPad={120}>
      <PageTitle title="Finanças" sub="Analise seus gastos e planeje sua saída das dívidas" />

      <View className="mb-1.5">
        <TabsUnderline items={TABS} active={tab} onChange={setTab} />
      </View>

      {tab === "analise" && (
        <AnaliseTab onOpenCategorias={() => navigation.navigate("Categorias")} />
      )}
      {tab === "planejamento" && (
        <PlanejamentoTab
          onOpenMetodo={(id) => navigation.navigate("MetodoOnboarding", { id })}
        />
      )}
    </Screen>
  );
}
