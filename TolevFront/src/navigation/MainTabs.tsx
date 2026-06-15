import { createBottomTabNavigator } from "@react-navigation/bottom-tabs";
import { useNavigation } from "@react-navigation/native";
import { View } from "react-native";
import { HomeHeader, SlimHeader } from "../components";
import HomeScreen from "../features/menu/screens/HomeScreen";
import MetasScreen from "../features/missions/screens/MetasScreen";
import NotificacoesScreen from "../features/notifications/screens/NotificacoesScreen";
import ProgressaoScreen from "../features/progress/screens/ProgressaoScreen";
import FinancasScreen from "../features/simulations/screens/FinancasScreen";
import TabBar from "./TabBar";

const Tab = createBottomTabNavigator();

function withHeader(Component: React.ComponentType<any>, variant: "home" | "slim") {
  return function Wrapped(props: any) {
    const navigation = useNavigation<any>();
    return (
      <View className="flex-1 bg-bg">
        {variant === "home" ? (
          <HomeHeader
            onAvatar={() => navigation.getParent()?.navigate("Perfil")}
            onNotifications={() => navigation.getParent()?.navigate("Notificacoes")}
          />
        ) : (
          <SlimHeader
            onProfile={() => navigation.getParent()?.navigate("Perfil")}
            onNotifications={() => navigation.getParent()?.navigate("Notificacoes")}
          />
        )}
        <Component {...props} />
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
      <Tab.Screen name="Metas" component={withHeader(MetasScreen, "slim")} />
      <Tab.Screen name="Financas" component={withHeader(FinancasScreen, "slim")} />
      <Tab.Screen name="Progresso" component={withHeader(ProgressaoScreen, "slim")} />
    </Tab.Navigator>
  );
}
