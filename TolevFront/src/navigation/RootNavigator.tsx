import { NavigationContainer, useNavigation } from "@react-navigation/native";
import { createNativeStackNavigator } from "@react-navigation/native-stack";
import { useEffect, useState } from "react";
import { View } from "react-native";
import { setOnUnauthorized } from "../api/axios";
import { SlimHeader } from "../components";
import LoginScreen from "../features/auth/screens/LoginScreen";
import RegisterScreen from "../features/auth/screens/RegisterScreen";
import AdicionarDividaScreen from "../features/debts/screens/AdicionarDividaScreen";
import DividaDetalheScreen from "../features/debts/screens/DividaDetalheScreen";
import NotificacoesScreen from "../features/notifications/screens/NotificacoesScreen";
import PerfilScreen from "../features/profile/screens/PerfilScreen";
import CategoriasScreen from "../features/simulations/screens/CategoriasScreen";
import MetodoOnboardingScreen from "../features/simulations/screens/MetodoOnboardingScreen";
import SimulacaoResultadoScreen from "../features/simulations/screens/SimulacaoResultadoScreen";
import SimulacaoScreen from "../features/simulations/screens/SimulacaoScreen";
import { useAuthStore } from "../store/authStore";
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

const DividaDetalhe = () => (
  <ModalShell><DividaDetalheScreen /></ModalShell>
);
const AdicionarDivida = () => (
  <ModalShell><AdicionarDividaScreen /></ModalShell>
);
const Categorias = () => (
  <ModalShell><CategoriasScreen /></ModalShell>
);
const Simulacao = () => (
  <ModalShell><SimulacaoScreen /></ModalShell>
);
const SimulacaoResultado = () => (
  <ModalShell><SimulacaoResultadoScreen /></ModalShell>
);
const Perfil = () => {
  const clearUser = useAuthStore((s) => s.clearUser);
  return (
    <ModalShell><PerfilScreen onLogout={clearUser} /></ModalShell>
  );
};
const Notificacoes = () => (
  <ModalShell><NotificacoesScreen /></ModalShell>
);

export default function RootNavigator() {
  const isAuthenticated = useAuthStore((s) => s.token != null);
  const clearUser = useAuthStore((s) => s.clearUser);
  const [hydrated, setHydrated] = useState(
    () => useAuthStore.persist?.hasHydrated() ?? true,
  );

  // Wait for the persisted token to be restored before deciding which stack to
  // show, so a logged-in user doesn't briefly see the login screen.
  useEffect(() => {
    const persist = useAuthStore.persist;
    if (!persist) {
      setHydrated(true);
      return;
    }
    const unsub = persist.onFinishHydration(() => setHydrated(true));
    if (persist.hasHydrated()) {
      setHydrated(true);
    }
    return unsub;
  }, []);

  // Log the user out automatically when the API rejects the token.
  useEffect(() => {
    setOnUnauthorized(() => clearUser());
    return () => setOnUnauthorized(null);
  }, [clearUser]);

  // Avoid flashing the login screen before the persisted token is restored.
  if (!hydrated) {
    return <View className="flex-1 bg-primary-700" />;
  }

  return (
    <NavigationContainer>
      <Stack.Navigator screenOptions={{ headerShown: false }}>
        {isAuthenticated ? (
          <Stack.Group>
            <Stack.Screen name="Main" component={MainTabs} />
            <Stack.Screen name="DividaDetalhe" component={DividaDetalhe} />
            <Stack.Screen name="AdicionarDivida" component={AdicionarDivida} />
            <Stack.Screen name="Categorias" component={Categorias} />
            <Stack.Screen name="Simulacao" component={Simulacao} />
            <Stack.Screen name="MetodoOnboarding" component={MetodoOnboardingScreen} />
            <Stack.Screen name="SimulacaoResultado" component={SimulacaoResultado} />
            <Stack.Screen name="Perfil" component={Perfil} />
            <Stack.Screen name="Notificacoes" component={Notificacoes} />
          </Stack.Group>
        ) : (
          <Stack.Group>
            <Stack.Screen name="Login">
              {({ navigation }) => (
                <LoginScreen
                  onAuthenticated={() => {}}
                  onGoToRegister={() => navigation.navigate("Register")}
                />
              )}
            </Stack.Screen>
            <Stack.Screen name="Register">
              {({ navigation }) => (
                <RegisterScreen
                  onAuthenticated={() => {}}
                  onGoToLogin={() => navigation.goBack()}
                />
              )}
            </Stack.Screen>
          </Stack.Group>
        )}
      </Stack.Navigator>
    </NavigationContainer>
  );
}
