import { useNavigation } from "@react-navigation/native";
import { ActivityIndicator, Alert, Text, View } from "react-native";
import { Button, FAB, PageTitle, Screen } from "../../../components";
import { colors } from "../../../theme";
import { formatCurrencyBRL } from "../../../util/currency";
import { isoToMonthYear } from "../../../util/date";
import { GoalCard, GoalCardCompleted } from "../components/GoalCard";
import { categoriaIcon } from "../constants/categorias";
import { useDeleteMeta } from "../hooks/useDeleteMeta";
import { useMetas } from "../hooks/useMetas";
import { metaProgressPct } from "../utils/meta-view";

export default function MetasScreen() {
  const navigation = useNavigation<any>();
  const { data: metas, isLoading, isError, refetch } = useMetas();
  const deleteMeta = useDeleteMeta();

  const emProgresso = (metas ?? []).filter((m) => m.status === "ATIVA");
  const concluidas = (metas ?? []).filter((m) => m.status === "CONCLUIDA");

  function confirmDelete(id: number, nome: string) {
    Alert.alert("Excluir meta", `Deseja excluir "${nome}"?`, [
      { text: "Cancelar", style: "cancel" },
      {
        text: "Excluir",
        style: "destructive",
        onPress: () => deleteMeta.mutate(id),
      },
    ]);
  }

  return (
    <View className="flex-1 bg-bg">
      <Screen bottomPad={140}>
        <PageTitle
          title="Suas metas"
          sub="Acompanhe sua jornada rumo aos seus sonhos"
        />

        {isLoading ? (
          <View className="py-16 items-center">
            <ActivityIndicator color={colors.primary[700]} />
          </View>
        ) : isError ? (
          <View className="py-12 items-center gap-3">
            <Text className="text-muted font-regular text-center">
              Não foi possível carregar suas metas.
            </Text>
            <Button variant="outline" onPress={() => refetch()}>
              Tentar novamente
            </Button>
          </View>
        ) : emProgresso.length === 0 && concluidas.length === 0 ? (
          <View className="py-16 items-center gap-2">
            <Text className="text-ink font-semibold text-[16px] text-center">
              Você ainda não tem metas
            </Text>
            <Text className="text-muted font-regular text-center px-6">
              Crie sua primeira meta e comece a acompanhar seu progresso.
            </Text>
          </View>
        ) : (
          <>
            {emProgresso.length > 0 && (
              <>
                <SectionHeader
                  title="Em progresso"
                  sub={`${emProgresso.length} ${emProgresso.length === 1 ? "ativa" : "ativas"}`}
                />
                {emProgresso.map((meta) => (
                  <GoalCard
                    key={meta.id}
                    title={meta.nomeMeta}
                    pct={metaProgressPct(meta)}
                    valorAtual={formatCurrencyBRL(meta.progresso)}
                    valorFinal={formatCurrencyBRL(meta.valorMeta)}
                    icon={categoriaIcon(meta.categoria)}
                    onPress={() =>
                      navigation.navigate("MetaExpandida", { id: meta.id })
                    }
                    onMore={() => confirmDelete(meta.id, meta.nomeMeta)}
                  />
                ))}
              </>
            )}

            {concluidas.length > 0 && (
              <>
                <SectionHeader
                  title="Concluídas"
                  sub={`${concluidas.length} ${concluidas.length === 1 ? "alcançada" : "alcançadas"}`}
                  className={emProgresso.length > 0 ? "mt-4" : ""}
                />
                {concluidas.map((meta) => (
                  <GoalCardCompleted
                    key={meta.id}
                    title={meta.nomeMeta}
                    date={isoToMonthYear(meta.dataLimite) || "—"}
                  />
                ))}
              </>
            )}
          </>
        )}
      </Screen>
      <FAB onPress={() => navigation.navigate("CriarMeta")} />
    </View>
  );
}

function SectionHeader({
  title,
  sub,
  className = "",
}: {
  title: string;
  sub: string;
  className?: string;
}) {
  return (
    <View
      className={`flex-row justify-between items-baseline mb-3 pl-1 ${className}`}
    >
      <Text className="text-[11px] text-muted font-bold tracking-[0.6px]">
        {title}
      </Text>
      <Text className="text-[11px] text-muted font-regular">{sub}</Text>
    </View>
  );
}
