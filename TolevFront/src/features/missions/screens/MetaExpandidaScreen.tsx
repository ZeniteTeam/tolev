import { useRoute } from "@react-navigation/native";
import { Calendar, Gift, MoreVertical } from "lucide-react-native";
import { ActivityIndicator, Text, View } from "react-native";
import { Button, Progress, QuoteCard, Ring, Screen } from "../../../components";
import { colors, shadows } from "../../../theme";
import type { StatusMeta } from "../../../types/meta";
import { formatCurrencyBRL } from "../../../util/currency";
import { isoToBrDate } from "../../../util/date";
import { categoriaIcon, categoriaLabel } from "../constants/categorias";
import { useMeta } from "../hooks/useMeta";
import { metaProgressPct } from "../utils/meta-view";

const STATUS_LABEL: Record<StatusMeta, string> = {
  ATIVA: "META EM PROGRESSO",
  CONCLUIDA: "META CONCLUÍDA",
  CANCELADA: "META CANCELADA",
};

export default function MetaExpandidaScreen() {
  const route = useRoute<any>();
  const id = route.params?.id as number | undefined;
  const { data: meta, isLoading, isError } = useMeta(id);

  if (isLoading) {
    return (
      <Screen bottomPad={140}>
        <View className="py-24 items-center">
          <ActivityIndicator color={colors.primary[700]} />
        </View>
      </Screen>
    );
  }

  if (isError || !meta) {
    return (
      <Screen bottomPad={140}>
        <View className="py-24 items-center">
          <Text className="text-muted font-regular text-center">
            Não foi possível carregar esta meta.
          </Text>
        </View>
      </Screen>
    );
  }

  const Icon = categoriaIcon(meta.categoria);
  const pct = metaProgressPct(meta);

  return (
    <Screen bottomPad={140}>
      <View className="flex-row items-center gap-3 mb-[18px]">
        <Ring style={{ width: 48, height: 48, borderRadius: 24 }}>
          <Icon size={26} color={colors.primary[700]} strokeWidth={2} />
        </Ring>
        <View className="flex-1">
          <Text className="text-[11px] text-muted tracking-[0.5px] font-semibold">
            {STATUS_LABEL[meta.status]}
          </Text>
          <Text className="font-bold text-[22px] text-ink mt-0.5">{meta.nomeMeta}</Text>
        </View>
        <MoreVertical size={20} color={colors.text.secondary} strokeWidth={2} />
      </View>

      {meta.motivacaoMeta ? (
        <View className="gap-3 mb-[18px]">
          <QuoteCard variant="primary">{meta.motivacaoMeta}</QuoteCard>
        </View>
      ) : null}

      <View className="bg-white rounded-[18px] p-5 mb-3.5" style={shadows.card}>
        <View className="flex-row justify-between items-start">
          <View>
            <Text className="text-sm text-muted font-regular">Valor Atual Dedicado</Text>
            <Text className="font-bold text-[28px] text-primary-700 mt-1">
              {formatCurrencyBRL(meta.progresso)}
            </Text>
            <Text className="text-sm text-muted font-regular">
              de {formatCurrencyBRL(meta.valorMeta)}
            </Text>
          </View>
          <View className="bg-primary-100 px-3.5 py-2 rounded-pill">
            <Text className="font-bold text-[16px] text-primary-700">{pct}%</Text>
          </View>
        </View>

        <View className="mt-4">
          <Progress pct={pct} height={8} />
        </View>
      </View>

      {/* <View className="bg-white rounded-[18px] p-5 mb-3.5 flex-row items-center gap-4" style={shadows.card}>
        <View className="flex-1">
          <Text className="font-bold text-[15px] text-ink mt-1">
            Gráfico de Projeção de Conclusão
          </Text>

          
        </View>
      </View> */}

      <View className="bg-white rounded-[18px] p-5 mb-3.5 flex-row items-center gap-4" style={shadows.card}>
        <Ring>
          <Calendar size={22} color={colors.primary[700]} strokeWidth={2} />
        </Ring>
        <View className="flex-1">
          <Text className="text-sm text-muted font-regular">Data limite</Text>
          <Text className="font-bold text-[15px] text-ink mt-1">
            {isoToBrDate(meta.dataLimite) || "Sem prazo definido"}
          </Text>
        </View>
      </View>

      <View className="flex-row gap-3 mb-4">
        <View className="flex-1 bg-white rounded-[18px] p-[18px] items-center" style={shadows.card}>
          <Text className="text-[12px] text-muted font-regular text-center">Categoria</Text>
          <View className="items-center my-2.5">
            <Ring>
              <Icon size={22} color={colors.primary[700]} strokeWidth={2} />
            </Ring>
          </View>
          <Text className="text-primary-700 font-bold text-[14px] text-center">
            {categoriaLabel(meta.categoria)}
          </Text>
        </View>
        <View className="flex-1 bg-white rounded-[18px] p-[18px] items-center" style={shadows.card}>
          <Text className="text-[12px] text-muted font-regular text-center">Recompensa ao{"\n"}concluir</Text>
          <View className="items-center my-2.5">
            <Ring>
              <Gift size={22} color={colors.primary[700]} strokeWidth={2} />
            </Ring>
          </View>
          <Text className="text-teal-500 font-bold text-sm text-center">
            {meta.recompensa || "Sem recompensa"}
          </Text>
        </View>
      </View>

      <Button variant="primary">Adicionar valor</Button>
    </Screen>
  );
}
