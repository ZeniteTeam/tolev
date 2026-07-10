import { useNavigation, useRoute } from "@react-navigation/native";
import { LinearGradient } from "expo-linear-gradient";
import {
  AlertCircle,
  CalendarClock,
  Check,
  TrendingUp,
} from "lucide-react-native";
import { useState } from "react";
import { Alert, Text, View } from "react-native";
import { Button, Ring, Screen } from "../../../components";
import { colors, shadows } from "../../../theme";
import { getApiErrorMessage } from "../../../util/apiError";
import RegistrarPagamentoModal from "../components/RegistrarPagamentoModal";
import { brl, DIVIDAS_SEED, isQuitada, pctQuitado } from "../constants/dividas";
import { useDividas } from "../hooks/useDividas";
import { useRegistrarPagamento } from "../hooks/useRegistrarPagamento";

export default function DividaDetalheScreen() {
  const navigation = useNavigation<any>();
  const route = useRoute<any>();
  const { dividas } = useDividas();
  const registrar = useRegistrarPagamento();
  const [modalVisible, setModalVisible] = useState(false);
  const id = route.params?.id;
  const d = dividas.find((x) => x.id === id) ?? DIVIDAS_SEED[0];

  function registrarPagamento(total: number, parcelas: number[]) {
    if (typeof d.id !== "number") {
      Alert.alert(
        "Dívida de exemplo",
        "Esta é uma dívida de demonstração e ainda não está no servidor. Cadastre uma dívida para registrar pagamentos.",
      );
      setModalVisible(false);
      return;
    }
    if (parcelas.length === 0) return;
    const valorPorParcela = total / parcelas.length;
    registrar.mutate(
      { idDivida: d.id, parcelas, valorPorParcela },
      {
        onSuccess: () => {
          setModalVisible(false);
          const label = parcelas.length === 1 ? "parcela" : "parcelas";
          Alert.alert(
            "Pagamento registrado",
            `${parcelas.length} ${label} · ${brl(total)}.`,
          );
        },
        onError: (err) =>
          Alert.alert(
            "Erro",
            getApiErrorMessage(err, "Não foi possível registrar o pagamento."),
          ),
      },
    );
  }

  const quitada = isQuitada(d);
  const pctPago = quitada ? 100 : pctQuitado(d);
  const parcelasPagas = Math.min(d.parcelasPagas.length, d.parcelas);
  const jurosMes = quitada ? 0 : Math.round(d.saldo * (d.juros / 100));

  const historico = [
    { mes: "Set/2025", valor: d.min, status: "ok" as const },
    { mes: "Ago/2025", valor: d.min, status: "ok" as const },
    {
      mes: "Jul/2025",
      valor: Math.round(d.min * 0.6),
      status: "parcial" as const,
    },
    { mes: "Jun/2025", valor: d.min, status: "ok" as const },
  ];

  const Icon = d.icon;

  return (
    <Screen bottomPad={40}>
      <View className="pt-1 mb-[18px]">
        <View
          className="flex-row items-center gap-2 self-start px-3 py-1.5 rounded-pill mb-2.5"
          style={{ backgroundColor: colors.primary[100] }}
        >
          <View
            className="w-4 h-4 rounded-[5px] items-center justify-center"
            style={{ backgroundColor: d.bankColor }}
          >
            <Icon size={10} color="#fff" strokeWidth={2} />
          </View>
          <Text className="text-[12px] font-semibold text-primary-700">
            {d.banco}
          </Text>
        </View>
        <Text className="font-bold text-[24px] leading-[28px] text-ink">
          {d.nome}
        </Text>
      </View>

      <LinearGradient
        colors={
          quitada
            ? [colors.primary[600], colors.primary[500]]
            : [colors.primary[700], colors.primary[600]]
        }
        start={{ x: 0, y: 0 }}
        end={{ x: 1, y: 1 }}
        className="rounded-[18px] px-[22px] pt-[22px] pb-[18px] mb-4"
        style={shadows.card}
      >
        {quitada ? (
          <View className="flex-row items-center gap-2">
            <View className="w-6 h-6 rounded-full items-center justify-center bg-white/20">
              <Check size={15} color="#fff" strokeWidth={3} />
            </View>
            <Text className="text-white text-[15px] font-bold">
              Dívida quitada
            </Text>
          </View>
        ) : (
          <Text className="text-white/[0.85] text-sm font-semibold">
            Saldo devedor
          </Text>
        )}
        <Text className="text-white text-[32px] leading-9 font-bold mt-1.5">
          {quitada ? "Parabéns!" : brl(d.saldo)}
        </Text>

        <View className="flex-row justify-between mt-3.5 mb-1.5">
          <Text className="text-white/90 text-[12px] font-regular">
            {parcelasPagas} de {d.parcelas} parcelas
          </Text>
          <Text className="text-white/90 text-[12px] font-regular">
            {pctPago}% quitado
          </Text>
        </View>
        <View className="h-2 rounded-pill overflow-hidden bg-white/[0.22]">
          <View
            className="h-full rounded-pill"
            style={{
              width: `${pctPago}%`,
              backgroundColor: colors.primary[300],
            }}
          />
        </View>

        <View className="flex-row mt-4 pt-4 border-t border-t-white/[0.18] gap-3.5">
          <View className="flex-1">
            <Text className="text-white/[0.78] text-[11px] font-regular">
              Juros a.m.
            </Text>
            <Text className="text-white text-[17px] font-bold mt-0.5">
              {d.juros.toFixed(1).replace(".", ",")}%
            </Text>
          </View>
          <View className="w-px bg-white/[0.18]" />
          <View className="flex-1">
            <Text className="text-white/[0.78] text-[11px] font-regular">
              Parcela mínima
            </Text>
            <Text className="text-white text-[17px] font-bold mt-0.5">
              {brl(d.min)}
            </Text>
          </View>
        </View>
      </LinearGradient>

      <View className="flex-row gap-3 mb-4">
        <View
          className="flex-1 bg-surface rounded-[18px] p-[18px]"
          style={shadows.card}
        >
          <Ring style={{ width: 34, height: 34, borderRadius: 17 }}>
            <CalendarClock
              size={17}
              color={colors.primary[700]}
              strokeWidth={2}
            />
          </Ring>
          <Text className="font-bold text-[18px] text-ink mt-3">
            {quitada ? "Quitada" : "Set/2027"}
          </Text>
          <Text className="text-[11px] text-muted mt-0.5 font-regular">
            {quitada ? "sem parcelas em aberto" : "quitação no ritmo atual"}
          </Text>
        </View>
        <View
          className="flex-1 bg-surface rounded-[18px] p-[18px]"
          style={shadows.card}
        >
          <View className="w-[34px] h-[34px] rounded-full items-center justify-center bg-coral-500/[0.12]">
            <TrendingUp size={17} color={colors.coral[500]} strokeWidth={2} />
          </View>
          <Text className="font-bold text-[18px] text-coral-500 mt-3">
            {brl(jurosMes)}
          </Text>
          <Text className="text-[11px] text-muted mt-0.5 font-regular">
            juros neste mês
          </Text>
        </View>
      </View>

      <View className="bg-surface rounded-[18px] p-5 mb-4" style={shadows.card}>
        <Text className="font-bold text-[16px] text-ink mb-3.5">
          Histórico de pagamentos
        </Text>
        {historico.map((h, i) => (
          <View
            key={h.mes}
            className="flex-row items-center gap-3 py-[11px]"
            style={
              i !== historico.length - 1
                ? { borderBottomWidth: 1, borderBottomColor: "#F1F5F3" }
                : undefined
            }
          >
            <View
              className="w-8 h-8 rounded-sm items-center justify-center"
              style={{
                backgroundColor:
                  h.status === "ok" ? colors.primary[100] : "#FEE7E0",
              }}
            >
              {h.status === "ok" ? (
                <Check size={16} color={colors.primary[700]} strokeWidth={2} />
              ) : (
                <AlertCircle
                  size={16}
                  color={colors.coral[500]}
                  strokeWidth={2}
                />
              )}
            </View>
            <View className="flex-1">
              <Text className="text-[14px] font-medium text-ink">{h.mes}</Text>
              <Text className="text-[11px] text-muted font-regular">
                {h.status === "ok" ? "Pago integralmente" : "Pagamento parcial"}
              </Text>
            </View>
            <Text className="text-[14px] font-bold text-ink">
              {brl(h.valor)}
            </Text>
          </View>
        ))}
      </View>

      {!quitada && (
        <Button variant="primary" onPress={() => setModalVisible(true)}>
          Registrar pagamento
        </Button>
      )}
      <Button
        variant="ghost"
        onPress={() => navigation.goBack()}
        style={{ marginTop: 4 }}
      >
        Voltar
      </Button>

      <RegistrarPagamentoModal
        visible={modalVisible}
        divida={d}
        submitting={registrar.isPending}
        onClose={() => setModalVisible(false)}
        onConfirm={registrarPagamento}
      />
    </Screen>
  );
}
