import { NavigationContainer, useNavigation } from "@react-navigation/native";
import { createNativeStackNavigator } from "@react-navigation/native-stack";
import { useEffect, useState } from "react";
import { View } from "react-native";
import { setOnUnauthorized } from "../api/axios";
import { SlimHeader } from "../components";
import LoginScreen from "../features/auth/screens/LoginScreen";
import OnboardingFlow from "../features/onboarding/screens/OnboardingFlow";
import AdicionarDividaScreen from "../features/debts/screens/AdicionarDividaScreen";
import DividaDetalheScreen from "../features/debts/screens/DividaDetalheScreen";
import AdicionarTransacaoScreen from "../features/transactions/screens/AdicionarTransacaoScreen";
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

  // Espera o token persistido ser restaurado antes de escolher a stack, senão
  // quem já está logado vê a tela de login piscar.
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

  // Desloga sozinho quando a API recusa o token.
  useEffect(() => {
    setOnUnauthorized(() => clearUser());
    return () => setOnUnauthorized(null);
  }, [clearUser]);

  // Evita o flash da tela de login antes do token ser restaurado.
  if (!hydrated) {
    return <View className="flex-1 bg-primary-700" />;
  }

  return (
    <NavigationContainer>
      {/*
        A `key` por estado de auth força o navigator a remontar inteiro no
        login/logout. Sem ela, trocar os grupos condicionais com uma rota
        empilhada em foco (ex.: "Register") deixa o navigator apontando para
        uma rota que não existe mais — tela branca.
      */}
      <Stack.Navigator
        key={isAuthenticated ? "app" : "guest"}
        screenOptions={{ headerShown: false }}
      >
        {isAuthenticated ? (
          <Stack.Group>
            <Stack.Screen name="Main" component={MainTabs} />
            <Stack.Screen name="DividaDetalhe" component={DividaDetalhe} />
            {/* Fluxo em etapas: traz o próprio cabeçalho com voltar + progresso. */}
            <Stack.Screen name="AdicionarDivida" component={AdicionarDividaScreen} />
            <Stack.Screen name="AdicionarTransacao" component={AdicionarTransacaoScreen} />
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
                <OnboardingFlow
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
