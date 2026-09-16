import { useNavigation } from "@react-navigation/native";
import { LinearGradient } from "expo-linear-gradient";
import { Landmark } from "lucide-react-native";
import { useState } from "react";
import { Pressable, Text, View } from "react-native";
import { PageTitle, Screen, Stagger, TabSwitch, TabsUnderline } from "../../../components";
import { colors, shadows } from "../../../theme";
import DebtCard from "../components/DebtCard";
import ProjecoesTab from "../components/ProjecoesTab";
import { brl } from "../constants/dividas";
import { useDividas } from "../hooks/useDividas";

const TABS = [
  { key: "lista", label: "Dívidas" },
  { key: "projecoes", label: "Projeções" },
];

export default function DividasScreen() {
  const [tab, setTab] = useState("lista");

  return (
    <Screen bottomPad={140}>
      <PageTitle title="Suas dívidas" sub="Acompanhe e planeje a quitação de cada uma" />

      <View className="mb-1.5">
        <TabsUnderline items={TABS} active={tab} onChange={setTab} />
      </View>

      {/* A `key` da aba remonta o painel, então a lista de dívidas e os
          gráficos das projeções reaparecem com a própria entrada. */}
      <TabSwitch tabKey={tab}>
        {tab === "lista" && <DividasLista />}
        {tab === "projecoes" && <ProjecoesTab />}
      </TabSwitch>
    </Screen>
  );
}

function DividasLista() {
  const navigation = useNavigation<any>();
  const { dividas, isPending } = useDividas();

  // Enquanto a primeira busca não volta, a lista vazia e o estado vazio são
  // indistinguíveis — mostrar o convite para cadastrar aqui seria um pisca.
  if (isPending) return null;
  if (dividas.length === 0) return <DividasVazio />;

  const total = dividas.reduce((s, d) => s + d.saldo, 0);
  const minTotal = dividas.reduce((s, d) => s + d.min, 0);

  return (
    <Stagger className="pt-[22px]">
      <LinearGradient
        colors={[colors.primary[700], colors.primary[600]]}
        start={{ x: 0, y: 0 }}
        end={{ x: 1, y: 1 }}
        className="rounded-[18px] px-[22px] pt-[22px] pb-[18px] mb-4"
        style={shadows.card}
      >
        <Text className="text-white/[0.85] text-sm font-semibold">Dívida total</Text>
        <Text className="text-white text-[32px] leading-9 font-bold mt-1.5">{brl(total)}</Text>

        <View className="flex-row mt-4 pt-4 border-t border-t-white/[0.18] gap-3.5">
          <View className="flex-1">
            <Text className="text-white/[0.78] text-[11px] font-regular">Parcela mínima</Text>
            <Text className="text-white text-[17px] font-bold mt-0.5">
              {brl(minTotal)}
              <Text className="text-white/70 text-[12px] font-regular">/mês</Text>
            </Text>
          </View>
          <View className="w-px bg-white/[0.18]" />
          <View className="flex-1">
            <Text className="text-white/[0.78] text-[11px] font-regular">Dívidas ativas</Text>
            <Text className="text-white text-[17px] font-bold mt-0.5">{dividas.length}</Text>
          </View>
        </View>
      </LinearGradient>

      <Text className="text-[11px] text-muted font-bold tracking-[0.6px] mx-1 mb-3">EM ABERTO</Text>

      {dividas.map((d) => (
        <DebtCard
          key={d.id}
          divida={d}
          onPress={() => navigation.navigate("DividaDetalhe", { id: d.id })}
        />
      ))}
    </Stagger>
  );
}

function DividasVazio() {
  const navigation = useNavigation<any>();

  return (
    <Stagger className="items-center pt-16 px-4">
      <View className="w-16 h-16 rounded-[20px] items-center justify-center bg-primary-50 mb-4">
        <Landmark size={28} color={colors.primary[700]} strokeWidth={2} />
      </View>
      <Text className="font-bold text-[18px] text-ink text-center">Nenhuma dívida por aqui</Text>
      <Text className="text-[14px] text-muted text-center leading-[21px] mt-2 mb-6 font-regular">
        Cadastre sua primeira dívida para acompanhar o saldo, as parcelas e o plano de quitação.
      </Text>
      <Pressable
        onPress={() => navigation.navigate("AdicionarDivida")}
        className="h-[52px] px-7 rounded-pill items-center justify-center bg-primary-700 active:scale-[0.99]"
        style={shadows.cta}
      >
        <Text className="font-bold text-[15px] text-white">Adicionar dívida</Text>
      </Pressable>
    </Stagger>
  );
}
