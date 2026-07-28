import { useNavigation } from "@react-navigation/native";
import { PageTitle, Screen } from "../../../components";
import SimulacaoTab from "../components/SimulacaoTab";

export default function SimulacaoScreen() {
  const navigation = useNavigation<any>();
  return (
    <Screen bottomPad={40}>
      <PageTitle title="Simulação" sub="Configure filtros para prever impactos financeiros" />
      <SimulacaoTab
        onSubmit={(filters) => navigation.navigate("SimulacaoResultado", { filters })}
      />
    </Screen>
  );
}
