import { useNavigation } from "@react-navigation/native";
import { Monitor, Target } from "lucide-react-native";
import { Text, View } from "react-native";
import { FAB, PageTitle, Screen } from "../../../components";
import { GoalCard, GoalCardCompleted } from "../components/GoalCard";

export default function MetasScreen() {
  const navigation = useNavigation<any>();
  return (
    <View className="flex-1 bg-bg">
      <Screen bottomPad={140}>
        <PageTitle title="Suas metas" sub="Acompanhe sua jornada rumo aos seus sonhos" />

        <SectionHeader title="Em progresso" sub="2 ativas" />
        <GoalCard
          title="Comprar um carro"
          pct={60}
          valorAtual="R$ 40.000"
          valorFinal="R$ 70.000"
          icon={Target}
          onPress={() => navigation.navigate("MetaExpandida")}
        />
        <GoalCard
          title="Montar meu computador"
          pct={10}
          valorAtual="R$ 2.000"
          valorFinal="R$ 20.000"
          icon={Monitor}
        />

        <SectionHeader title="Concluídas" sub="1 alcançada" className="mt-4" />
        <GoalCardCompleted title="Viagem para Salvador" date="Mar/2026" />
      </Screen>
      <FAB onPress={() => navigation.navigate("CriarMeta")} />
    </View>
  );
}

function SectionHeader({ title, sub, className = "" }: { title: string; sub: string; className?: string }) {
  return (
    <View className={`flex-row justify-between items-baseline mb-3 pl-1 ${className}`}>
      <Text className="text-[11px] text-muted font-bold tracking-[0.6px]">{title}</Text>
      <Text className="text-[11px] text-muted font-regular">{sub}</Text>
    </View>
  );
}
