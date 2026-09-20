import { useNavigation } from "@react-navigation/native";
import { LinearGradient } from "expo-linear-gradient";
import { Check, ChevronRight, Plus, Repeat } from "lucide-react-native";
import { Alert, Pressable, Text, View } from "react-native";
import { PageTitle, Screen, Stagger } from "../../../components";
import { colors, shadows } from "../../../theme";
import { getApiErrorMessage } from "../../../util/apiError";
import { formatCurrencyBRL } from "../../../util/currency";
import type { GastoFixoResponse } from "../../../types/gastoFixo";
import { useConfirmarGastosFixos, useGastosFixos } from "../hooks/useGastosFixos";
import {
  confirmadoNesteMes,
  iconePara,
  pendentesDeConfirmacao,
  rotuloVencimento,
} from "../utils/gasto-fixo-view";

export default function GastosFixosScreen() {
  const navigation = useNavigation<any>();
  const { gastos, total, isPending } = useGastosFixos();
  const confirmar = useConfirmarGastosFixos();

  if (isPending) return <Screen bottomPad={40}><View /></Screen>;

  const pendentes = pendentesDeConfirmacao(gastos);

  function confirmarTudo() {
    confirmar.mutate(undefined, {
      onError: (err) =>
        Alert.alert("Erro", getApiErrorMessage(err, "Não foi possível confirmar os gastos.")),
    });
  }

  return (
    <Screen bottomPad={120}>
      <PageTitle title="Gastos fixos" sub="O que sai da sua conta todo mês" />

      {gastos.length === 0 ? (
        <GastosFixosVazio onAdicionar={() => navigation.navigate("AdicionarGastoFixo")} />
      ) : (
        <Stagger>
          <LinearGradient
            colors={[colors.primary[700], colors.primary[600]]}
            start={{ x: 0, y: 0 }}
            end={{ x: 1, y: 1 }}
            className="rounded-[18px] px-[22px] pt-[22px] pb-[18px] mb-4"
            style={shadows.card}
          >
            <Text className="text-white/[0.85] text-sm font-semibold">Total por mês</Text>
            <Text className="text-white text-[32px] leading-9 font-bold mt-1.5">
              {formatCurrencyBRL(total)}
            </Text>
            <Text className="text-white/[0.78] text-[12px] mt-1 font-regular">
              {gastos.length === 1 ? "1 gasto fixo" : `${gastos.length} gastos fixos`}
            </Text>
          </LinearGradient>

          {/* Só aparece quando há o que conferir. Um aviso permanente vira
              parte do cenário e para de ser lido depois da segunda vez. */}
          {pendentes > 0 && (
            <ConfirmacaoCard
              pendentes={pendentes}
              carregando={confirmar.isPending}
              onConfirmar={confirmarTudo}
            />
          )}

          <Text className="text-[11px] text-muted font-bold tracking-[0.6px] mx-1 mb-3">
            TODO MÊS
          </Text>

          {gastos.map((g) => (
            <GastoFixoRow
              key={g.id}
              gasto={g}
              onPress={() => navigation.navigate("AdicionarGastoFixo", { gasto: g })}
            />
          ))}

          <Pressable
            onPress={() => navigation.navigate("AdicionarGastoFixo")}
            className="h-[52px] rounded-[16px] flex-row items-center justify-center gap-2 bg-primary-50 active:opacity-90 mt-1"
          >
            <Plus size={18} color={colors.primary[700]} strokeWidth={2.5} />
            <Text className="font-bold text-[15px] text-primary-700">Adicionar gasto fixo</Text>
          </Pressable>
        </Stagger>
      )}
    </Screen>
  );
}

/**
 * O gesto que mantém o cadastro vivo.
 *
 * Confirmar tudo de uma vez é o caminho certo para o caso comum — gasto fixo
 * muda pouco. Quem precisa corrigir um valor abre aquele gasto, e a edição
 * confirma sozinha; forçar linha por linha ensinaria a tocar sem ler.
 */
function ConfirmacaoCard({
  pendentes,
  carregando,
  onConfirmar,
}: {
  pendentes: number;
  carregando: boolean;
  onConfirmar: () => void;
}) {
  return (
    <View
      className="rounded-[16px] px-[18px] py-4 mb-4 flex-row gap-3 items-center"
      style={{ backgroundColor: colors.primary[25] }}
    >
      <View className="w-[38px] h-[38px] rounded-[11px] bg-white items-center justify-center">
        <Repeat size={18} color={colors.primary[700]} strokeWidth={2} />
      </View>
      <View className="flex-1">
        <Text className="text-[13px] font-bold text-ink">Ainda vale este mês?</Text>
        <Text className="text-[12px] text-muted mt-0.5 font-regular leading-[17px]">
          {pendentes === 1
            ? "1 gasto não foi conferido neste mês."
            : `${pendentes} gastos não foram conferidos neste mês.`}
        </Text>
      </View>
      <Pressable
        onPress={onConfirmar}
        disabled={carregando}
        className="px-3.5 h-9 rounded-pill bg-primary-700 items-center justify-center active:opacity-90"
        style={carregando ? { opacity: 0.6 } : undefined}
      >
        <Text className="text-white font-bold text-[13px]">Confirmar</Text>
      </Pressable>
    </View>
  );
}

function GastoFixoRow({
  gasto,
  onPress,
}: {
  gasto: GastoFixoResponse;
  onPress: () => void;
}) {
  const Icon = iconePara(gasto.nome);
  const vencimento = rotuloVencimento(gasto.diaVencimento);
  const conferido = confirmadoNesteMes(gasto);

  return (
    <Pressable
      onPress={onPress}
      className="bg-surface rounded-[16px] p-4 mb-2.5 flex-row items-center gap-3.5 active:opacity-90"
      style={shadows.card}
    >
      <View className="w-[38px] h-[38px] rounded-[11px] bg-primary-100 items-center justify-center">
        <Icon size={18} color={colors.primary[700]} strokeWidth={2} />
      </View>

      <View className="flex-1">
        <Text className="font-semibold text-[15px] text-ink">{gasto.nome}</Text>
        <View className="flex-row items-center gap-1.5 mt-0.5">
          {conferido && <Check size={12} color={colors.teal[500]} strokeWidth={3} />}
          <Text className="text-[12px] text-muted font-regular">
            {conferido ? "conferido este mês" : vencimento || "sem dia definido"}
          </Text>
        </View>
      </View>

      <Text className="font-bold text-[15px] text-ink">{formatCurrencyBRL(gasto.valor, true)}</Text>
      <ChevronRight size={18} color={colors.text.secondary} strokeWidth={2} />
    </Pressable>
  );
}

function GastosFixosVazio({ onAdicionar }: { onAdicionar: () => void }) {
  return (
    <Stagger className="items-center pt-12 px-4">
      <View className="w-16 h-16 rounded-[20px] items-center justify-center bg-primary-50 mb-4">
        <Repeat size={28} color={colors.primary[700]} strokeWidth={2} />
      </View>
      <Text className="font-bold text-[18px] text-ink text-center">
        Nenhum gasto fixo cadastrado
      </Text>
      <Text className="text-[14px] text-muted text-center leading-[21px] mt-2 mb-6 font-regular">
        Aluguel, internet, academia — o que sai todo mês no mesmo valor. É com eles que o Tolev
        calcula quanto sobra de verdade.
      </Text>
      <Pressable
        onPress={onAdicionar}
        className="h-[52px] px-7 rounded-pill items-center justify-center bg-primary-700 active:scale-[0.99]"
        style={shadows.cta}
      >
        <Text className="font-bold text-[15px] text-white">Adicionar o primeiro</Text>
      </Pressable>
    </Stagger>
  );
}
