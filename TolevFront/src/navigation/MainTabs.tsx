import { createBottomTabNavigator } from "@react-navigation/bottom-tabs";
import { useIsFocused, useNavigation } from "@react-navigation/native";
import { useEffect, useRef, useState, type ReactNode } from "react";
import { View } from "react-native";
import { GlobalFab, HomeHeader, SlimHeader } from "../components";
import DividasScreen from "../features/debts/screens/DividasScreen";
import HomeScreen from "../features/menu/screens/HomeScreen";
import ProgressaoScreen from "../features/progress/screens/ProgressaoScreen";
import ExtratoStatusBanner from "../features/extrato/components/ExtratoStatusBanner";
import FinancasScreen from "../features/simulations/screens/FinancasScreen";
import TabBar from "./TabBar";

const Tab = createBottomTabNavigator();

/**
 * Remonta o conteúdo a cada vez que a aba volta ao foco.
 *
 * As telas entram com `Stagger`, que é uma animação de montagem: sem isto ela
 * roda só na primeira visita — a aba monta sob demanda — e a partir da segunda a
 * troca fica sem entrada nenhuma. A `key` faz toda visita ser como a primeira.
 *
 * A aba monta já focada, então o primeiro foco não conta: só a transição
 * desfocada → focada incrementa, senão a entrada inicial rodaria duas vezes.
 */
function EntradaAoFocar({ children }: { children: ReactNode }) {
  const isFocused = useIsFocused();
  const [ciclo, setCiclo] = useState(0);
  const estavaFocada = useRef(isFocused);

  useEffect(() => {
    if (isFocused && !estavaFocada.current) setCiclo((c) => c + 1);
    estavaFocada.current = isFocused;
  }, [isFocused]);

  return (
    <View className="flex-1" key={ciclo}>
      {children}
    </View>
  );
}

function withHeader(Component: React.ComponentType<any>, variant: "home" | "slim") {
  return function Wrapped(props: any) {
    const navigation = useNavigation<any>();
    const parent = () => navigation.getParent();
    return (
      <View className="flex-1 bg-bg">
        {variant === "home" ? (
          <HomeHeader
            onAvatar={() => parent()?.navigate("Perfil")}
            onNotifications={() => parent()?.navigate("Notificacoes")}
          />
        ) : (
          <SlimHeader
            onProfile={() => parent()?.navigate("Perfil")}
            onNotifications={() => parent()?.navigate("Notificacoes")}
          />
        )}
        <EntradaAoFocar>
          <Component {...props} />
        </EntradaAoFocar>
        {/* Fora das telas, junto do FAB: o aviso do extrato precisa sobreviver
            à troca de aba — é o que deixa o usuário sair do acompanhamento sem
            perder o resultado de vista. */}
        <ExtratoStatusBanner />
        <GlobalFab
          onSimular={() => parent()?.navigate("Simulacao")}
          onAddDivida={() => parent()?.navigate("AdicionarDivida")}
          onAddTransacao={() => parent()?.navigate("AdicionarTransacao")}
        />
      </View>
    );
  };
}

export default function MainTabs() {
  return (
    <Tab.Navigator
      tabBar={(p) => <TabBar {...p} />}
      /*
        Sem animação no navigator: quem anima é o conteúdo, pelo `Stagger` que
        o `EntradaAoFocar` faz rodar de novo a cada visita.

        Cruzar as telas inteiras (`fade` ou `shift`) esbarra no header e no FAB,
        que são montados por aba: são dois elementos idênticos sobrepostos, e
        duas cópias a meia opacidade não somam o opaco original — no meio da
        troca o verde clareia. Trocando sem animação eles ficam simplesmente
        parados, e a transição fica sendo a entrada do conteúdo.
      */
      screenOptions={{ headerShown: false, animation: "none" }}
      initialRouteName="Menu"
    >
      <Tab.Screen name="Menu" component={withHeader(HomeScreen, "home")} />
      <Tab.Screen name="Dividas" component={withHeader(DividasScreen, "slim")} />
      <Tab.Screen name="Financas" component={withHeader(FinancasScreen, "slim")} />
      <Tab.Screen name="Progresso" component={withHeader(ProgressaoScreen, "slim")} />
    </Tab.Navigator>
  );
}
