import { useNavigation } from "@react-navigation/native";
import { LinearGradient } from "expo-linear-gradient";
import { useState } from "react";
import { Text, View } from "react-native";
import { PageTitle, Screen, TabsUnderline } from "../../../components";
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

      {tab === "lista" && <DividasLista />}
      {tab === "projecoes" && <ProjecoesTab />}
    </Screen>
  );
}

function DividasLista() {
  const navigation = useNavigation<any>();
  const { dividas } = useDividas();
  const total = dividas.reduce((s, d) => s + d.saldo, 0);
  const minTotal = dividas.reduce((s, d) => s + d.min, 0);

  return (
    <View className="pt-[22px]">
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
          totalDivida={total}
          onPress={() => navigation.navigate("DividaDetalhe", { id: d.id })}
        />
      ))}
    </View>
  );
}
