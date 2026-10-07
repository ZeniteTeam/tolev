import { useNavigation, useRoute } from "@react-navigation/native";
import { useState } from "react";
import { View } from "react-native";
import { PageTitle, Screen, TabSwitch, TabsUnderline } from "../../../components";
import AnaliseTab from "../components/AnaliseTab";
import PlanejamentoTab from "../components/PlanejamentoTab";

const TABS = [
  { key: "analise", label: "Análise" },
  { key: "planejamento", label: "Planejamento" },
];

export default function FinancasScreen() {
  const navigation = useNavigation<any>();
  const route = useRoute<any>();

  // A aba nasce do param, não de um valor fixo. Voltar de uma tela empilhada
  // (escolher um método, abrir categorias) devolve o foco a esta aba, e o
  // `EntradaAoFocar` do MainTabs remonta a subárvore inteira para reexecutar a
  // animação de entrada — o que recria este `useState`. Param é estado do
  // navigator: sobrevive ao remonte, e é por isso que ele guarda a escolha.
  const [tab, setTab] = useState<string>(route.params?.tab ?? "analise");

  function trocarTab(key: string) {
    setTab(key);
    navigation.setParams({ tab: key });
  }

  return (
    <Screen bottomPad={120}>
      <PageTitle title="Finanças" sub="Analise seus gastos e planeje sua saída das dívidas" />

      <View className="mb-1.5">
        <TabsUnderline items={TABS} active={tab} onChange={trocarTab} />
      </View>

      {/* A `key` da aba remonta o painel: trocar de aba redesenha os gráficos
          da análise em vez de trocar o conteúdo embaixo do usuário. */}
      <TabSwitch tabKey={tab}>
        {tab === "analise" && (
          <AnaliseTab
            onOpenCategorias={() => navigation.navigate("Categorias")}
            onImportarExtrato={() => navigation.navigate("ImportarExtrato")}
            onAcompanharExtrato={() => navigation.navigate("ExtratoProcessando")}
          />
        )}
        {tab === "planejamento" && (
          <PlanejamentoTab
            onOpenMetodo={(id) => navigation.navigate("MetodoOnboarding", { id })}
          />
        )}
      </TabSwitch>
    </Screen>
  );
}
