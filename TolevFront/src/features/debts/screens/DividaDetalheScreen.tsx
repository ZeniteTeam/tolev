import { useNavigation, useRoute } from "@react-navigation/native";
import { LinearGradient } from "expo-linear-gradient";
import { AlertCircle, CalendarClock, Check, Clock, TrendingUp } from "lucide-react-native";
import { useState } from "react";
import { Alert, Text, View } from "react-native";
import { Button, Ring, Screen } from "../../../components";
import { colors, shadows } from "../../../theme";
import { getApiErrorMessage } from "../../../util/apiError";
import { isoToBrDate, isoToMonthYear } from "../../../util/date";
import RegistrarPagamentoModal from "../components/RegistrarPagamentoModal";
import {
  brl,
  DIVIDAS_SEED,
  isQuitada,
  parcelasEmAberto,
  pctQuitado,
  totalEmAberto,
  type ParcelaView,
} from "../constants/dividas";
import { useDividas } from "../hooks/useDividas";
import { useRegistrarPagamento } from "../hooks/useRegistrarPagamento";

const SISTEMA_LABEL = { PRICE: "PRICE · parcela fixa", SAC: "SAC · parcela decrescente" };
const REGIME_LABEL = { SIMPLES: "Juros simples", COMPOSTO: "Juros compostos" };

export default function DividaDetalheScreen() {
  const navigation = useNavigation<any>();
  const route = useRoute<any>();
  const { dividas } = useDividas();
  const registrar = useRegistrarPagamento();
  const [modalVisible, setModalVisible] = useState(false);
  const id = route.params?.id;
  const d = dividas.find((x) => x.id === id) ?? DIVIDAS_SEED[0];

  function registrarPagamento(pagamentos: { numero: number; valorPago: number }[]) {
    if (typeof d.id !== "number") {
      Alert.alert(
        "Dívida de exemplo",
        "Esta é uma dívida de demonstração e ainda não está no servidor. Cadastre uma dívida para registrar pagamentos.",
      );
      setModalVisible(false);
      return;
    }
    if (pagamentos.length === 0) return;

    const total = pagamentos.reduce((s, p) => s + p.valorPago, 0);
    registrar.mutate(
      { idDivida: d.id, parcelas: pagamentos },
      {
        onSuccess: () => {
          setModalVisible(false);
          const label = pagamentos.length === 1 ? "parcela" : "parcelas";
          Alert.alert("Pagamento registrado", `${pagamentos.length} ${label} · ${brl(total)}.`);
        },
        onError: (err) =>
          Alert.alert("Erro", getApiErrorMessage(err, "Não foi possível registrar o pagamento.")),
      },
    );
  }

  const quitada = isQuitada(d);
  const pctPago = quitada ? 100 : pctQuitado(d);
  const parcelasPagas = Math.min(d.parcelasPagas.length, d.parcelas);
  const abertas = parcelasEmAberto(d);
  const proxima = abertas[0];
  // Os juros do mês são os da próxima parcela — o número real da tabela, não
  // uma estimativa sobre o saldo.
  const jurosMes = quitada ? 0 : (proxima?.juros ?? 0);
  const Icon = d.icon;

  return (
    <Screen bottomPad={40}>
      <View className="pt-1 mb-[18px]">
        <View
          className="flex-row items-center gap-2 self-start px-3 py-1.5 rounded-pill mb-2.5"
          style={{ backgroundColor: colors.primary[100] }}
        >
          <View
            className="w-4 h-4 rounded-xs items-center justify-center"
            style={{ backgroundColor: d.bankColor }}
          >
            <Icon size={10} color="#fff" strokeWidth={2} />
          </View>
          <Text className="text-xs font-semibold text-primary-700">{d.banco}</Text>
        </View>
        <Text className="font-bold text-2xl leading-7 text-ink">{d.nome}</Text>
      </View>

      <LinearGradient
        colors={
          quitada
            ? [colors.primary[600], colors.primary[500]]
            : [colors.primary[700], colors.primary[600]]
        }
        start={{ x: 0, y: 0 }}
        end={{ x: 1, y: 1 }}
        className="rounded-lg px-[22px] pt-[22px] pb-[18px] mb-4"
        style={shadows.card}
      >
        {quitada ? (
          <View className="flex-row items-center gap-2">
            <View className="w-6 h-6 rounded-full items-center justify-center bg-white/20">
              <Check size={15} color="#fff" strokeWidth={3} />
            </View>
            <Text className="text-white text-md font-bold">Dívida quitada</Text>
          </View>
        ) : (
          <Text className="text-white/[0.85] text-sm font-semibold">Saldo devedor</Text>
        )}
        <Text className="text-white text-[32px] leading-9 font-bold mt-1.5">
          {quitada ? "Parabéns!" : brl(d.saldo)}
        </Text>

        {!quitada && (
          <Text className="text-white/[0.78] text-[11px] mt-1 font-regular">
            faltam {brl(totalEmAberto(d))} somando as parcelas em aberto
          </Text>
        )}

        <View className="flex-row justify-between mt-3.5 mb-1.5">
          <Text className="text-white/90 text-xs font-regular">
            {parcelasPagas} de {d.parcelas} parcelas
          </Text>
          <Text className="text-white/90 text-xs font-regular">{pctPago}% quitado</Text>
        </View>
        <View className="h-2 rounded-pill overflow-hidden bg-white/[0.22]">
          <View
            className="h-full rounded-pill"
            style={{ width: `${pctPago}%`, backgroundColor: colors.primary[300] }}
          />
        </View>

        <View className="flex-row mt-4 pt-4 border-t border-t-white/[0.18] gap-3.5">
          <HeaderStat label="Juros a.m." value={`${d.juros.toFixed(1).replace(".", ",")}%`} />
          <View className="w-px bg-white/[0.18]" />
          <HeaderStat label="Total a pagar" value={brl(d.totalAPagar)} />
          <View className="w-px bg-white/[0.18]" />
          <HeaderStat label="Custo dos juros" value={brl(d.totalJuros)} />
        </View>
      </LinearGradient>

      <View className="flex-row gap-3 mb-4">
        <View className="flex-1 bg-surface rounded-lg p-[18px]" style={shadows.card}>
          <Ring style={{ width: 34, height: 34, borderRadius: 17 }}>
            <CalendarClock size={17} color={colors.primary[700]} strokeWidth={2} />
          </Ring>
          <Text className="font-bold text-lg text-ink mt-3">
            {quitada
              ? "Quitada"
              : isoToMonthYear(d.cronograma[d.cronograma.length - 1]?.vencimento) || "—"}
          </Text>
          <Text className="text-[11px] text-muted mt-0.5 font-regular">
            {quitada ? "sem parcelas em aberto" : "última parcela"}
          </Text>
        </View>
        <View className="flex-1 bg-surface rounded-lg p-[18px]" style={shadows.card}>
          <View className="w-[34px] h-[34px] rounded-full items-center justify-center bg-coral-500/[0.12]">
            <TrendingUp size={17} color={colors.coral[500]} strokeWidth={2} />
          </View>
          <Text className="font-bold text-lg text-coral-500 mt-3">{brl(jurosMes)}</Text>
          <Text className="text-[11px] text-muted mt-0.5 font-regular">
            {quitada ? "juros pagos" : "juros da próxima parcela"}
          </Text>
        </View>
      </View>

      <View className="bg-surface rounded-lg p-5 mb-4" style={shadows.card}>
        <Text className="font-bold text-base text-ink mb-3.5">Condições do contrato</Text>
        <Condicao label="Sistema" valor={SISTEMA_LABEL[d.sistema]} />
        <Condicao label="Regime de juros" valor={REGIME_LABEL[d.regime]} />
        <Condicao
          label="Multa por atraso"
          valor={d.multaAtraso > 0 ? `${fmtPct(d.multaAtraso)} sobre a parcela` : "não informada"}
        />
        <Condicao
          label="Juros de mora"
          valor={d.jurosMora > 0 ? `${fmtPct(d.jurosMora)} a.m.` : "não informados"}
          last
        />
      </View>

      <View className="bg-surface rounded-lg p-5 mb-4" style={shadows.card}>
        <View className="flex-row items-baseline justify-between mb-3.5">
          <Text className="font-bold text-base text-ink">Parcelas</Text>
          {proxima && (
            <Text className="text-[11px] text-muted font-regular">
              próxima em {isoToBrDate(proxima.vencimento) || "—"}
            </Text>
          )}
        </View>

        {d.cronograma.length === 0 ? (
          <Text className="text-sm text-muted font-regular py-2">
            Essa dívida ainda não tem parcelas geradas.
          </Text>
        ) : (
          d.cronograma.map((p, i) => (
            <ParcelaRow key={p.numero} parcela={p} last={i === d.cronograma.length - 1} />
          ))
        )}
      </View>

      {!quitada && (
        <Button variant="primary" onPress={() => setModalVisible(true)}>
          Registrar pagamento
        </Button>
      )}
      <Button variant="ghost" onPress={() => navigation.goBack()} style={{ marginTop: 4 }}>
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

const fmtPct = (n: number) => `${n.toFixed(2).replace(".", ",")}%`;

function HeaderStat({ label, value }: { label: string; value: string }) {
  return (
    <View className="flex-1">
      <Text className="text-white/[0.78] text-[11px] font-regular">{label}</Text>
      <Text className="text-white text-md font-bold mt-0.5">{value}</Text>
    </View>
  );
}

function Condicao({ label, valor, last }: { label: string; valor: string; last?: boolean }) {
  return (
    <View
      className="flex-row justify-between items-center py-2.5"
      style={last ? undefined : { borderBottomWidth: 1, borderBottomColor: colors.primary[50] }}
    >
      <Text className="text-sm text-muted font-regular">{label}</Text>
      <Text className="text-sm text-ink font-semibold">{valor}</Text>
    </View>
  );
}

function ParcelaRow({ parcela, last }: { parcela: ParcelaView; last: boolean }) {
  const paga = parcela.status === "PAGA";
  const atrasada = parcela.status === "ATRASADA";

  const { bg, Icon, cor } = paga
    ? { bg: colors.primary[100], Icon: Check, cor: colors.primary[700] }
    : atrasada
    ? { bg: "#FEE7E0", Icon: AlertCircle, cor: colors.coral[500] }
    : { bg: colors.primary[50], Icon: Clock, cor: colors.text.secondary };

  return (
    <View
      className="flex-row items-center gap-3 py-[11px]"
      style={last ? undefined : { borderBottomWidth: 1, borderBottomColor: "#F1F5F3" }}
    >
      <View
        className="w-8 h-8 rounded-sm items-center justify-center"
        style={{ backgroundColor: bg }}
      >
        <Icon size={16} color={cor} strokeWidth={2} />
      </View>

      <View className="flex-1">
        <Text className="text-sm font-medium text-ink">
          Parcela {parcela.numero}
          {parcela.vencimento ? ` · ${isoToMonthYear(parcela.vencimento)}` : ""}
        </Text>
        <Text className="text-[11px] font-regular" style={{ color: cor }}>
          {paga
            ? `Paga em ${isoToBrDate(parcela.pagamento) || "—"}`
            : atrasada
            ? `Venceu em ${isoToBrDate(parcela.vencimento) || "—"}`
            : `Vence em ${isoToBrDate(parcela.vencimento) || "—"}`}
        </Text>
      </View>

      <View className="items-end">
        <Text className="text-sm font-bold text-ink">{brl(parcela.valor)}</Text>
        {parcela.juros > 0 && (
          <Text className="text-[10px] text-muted mt-0.5 font-regular">
            {brl(parcela.principal)} + {brl(parcela.juros)} juros
          </Text>
        )}
      </View>
    </View>
  );
}
