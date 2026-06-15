import { NavigationContainer, useNavigation } from "@react-navigation/native";
import { createNativeStackNavigator } from "@react-navigation/native-stack";
import { View } from "react-native";
import { SlimHeader } from "../components";
import LoginScreen from "../features/auth/screens/LoginScreen";
import CriarMetaScreen from "../features/missions/screens/CriarMetaScreen";
import MetaExpandidaScreen from "../features/missions/screens/MetaExpandidaScreen";
import NotificacoesScreen from "../features/notifications/screens/NotificacoesScreen";
import PerfilScreen from "../features/profile/screens/PerfilScreen";
import SimulacaoResultadoScreen from "../features/simulations/screens/SimulacaoResultadoScreen";
import MainTabs from "./MainTabs";

const Stack = createNativeStackNavigator();

function ModalShell({ children }: { children: React.ReactNode }) {
  const navigation = useNavigation<any>();
  return (
    <View className="flex-1 bg-bg">
      <SlimHeader
        onBack={() => navigation.goBack()}
        onNotifications={() => navigation.navigate("Notificacoes")}
      />
      {children}
    </View>
  );
}

const MetaExpandida = () => (
  <ModalShell><MetaExpandidaScreen /></ModalShell>
);
const CriarMeta = () => (
  <ModalShell><CriarMetaScreen /></ModalShell>
);
const SimulacaoResultado = () => (
  <ModalShell><SimulacaoResultadoScreen /></ModalShell>
);
const Perfil = () => (
  <ModalShell><PerfilScreen /></ModalShell>
);
const Notificacoes = () => (
  <ModalShell><NotificacoesScreen /></ModalShell>
);

export default function RootNavigator() {
  return (
    <NavigationContainer>
      <Stack.Navigator screenOptions={{ headerShown: false }} initialRouteName="Login">
        <Stack.Screen name="Login">
          {({ navigation }) => (
            <LoginScreen onLogin={() => navigation.replace("Main")} />
          )}
        </Stack.Screen>
        <Stack.Screen name="Main" component={MainTabs} />
        <Stack.Screen name="MetaExpandida" component={MetaExpandida} />
        <Stack.Screen name="CriarMeta" component={CriarMeta} />
        <Stack.Screen name="SimulacaoResultado" component={SimulacaoResultado} />
        <Stack.Screen name="Perfil" component={Perfil} />
        <Stack.Screen name="Notificacoes" component={Notificacoes} />
      </Stack.Navigator>
    </NavigationContainer>
  );
}
