import { createBottomTabNavigator } from "@react-navigation/bottom-tabs";
import { useNavigation } from "@react-navigation/native";
import { View } from "react-native";
import { GlobalFab, HomeHeader, SlimHeader } from "../components";
import DividasScreen from "../features/debts/screens/DividasScreen";
import HomeScreen from "../features/menu/screens/HomeScreen";
import ProgressaoScreen from "../features/progress/screens/ProgressaoScreen";
import FinancasScreen from "../features/simulations/screens/FinancasScreen";
import TabBar from "./TabBar";

const Tab = createBottomTabNavigator();

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
        <Component {...props} />
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
      screenOptions={{ headerShown: false }}
      initialRouteName="Menu"
    >
      <Tab.Screen name="Menu" component={withHeader(HomeScreen, "home")} />
      <Tab.Screen name="Dividas" component={withHeader(DividasScreen, "slim")} />
      <Tab.Screen name="Financas" component={withHeader(FinancasScreen, "slim")} />
      <Tab.Screen name="Progresso" component={withHeader(ProgressaoScreen, "slim")} />
    </Tab.Navigator>
  );
}
