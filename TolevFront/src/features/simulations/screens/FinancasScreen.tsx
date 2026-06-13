import { useNavigation } from "@react-navigation/native";
import { useState } from "react";
import { View } from "react-native";
import { PageTitle, Screen, TabsUnderline } from "../../../components";
import AnaliseTab from "../components/AnaliseTab";
import ProjecoesTab from "../components/ProjecoesTab";
import SimulacaoTab from "../components/SimulacaoTab";

const TABS = [
  { key: "simulacao", label: "Simulação" },
  { key: "projecoes", label: "Projeções" },
  { key: "analise", label: "Análise" },
];

export default function FinancasScreen() {
  const navigation = useNavigation<any>();
  const [tab, setTab] = useState("projecoes");

  return (
    <Screen bottomPad={120}>
      <PageTitle title="Finanças" sub="Simule, projete e analise seu progresso" />

      <View style={{ marginBottom: 6 }}>
        <TabsUnderline items={TABS} active={tab} onChange={setTab} />
      </View>

      {tab === "simulacao" && (
        <SimulacaoTab onSubmit={(filters) => navigation.navigate("SimulacaoResultado", { filters })} />
      )}
      {tab === "projecoes" && <ProjecoesTab />}
      {tab === "analise" && <AnaliseTab />}
    </Screen>
  );
}
