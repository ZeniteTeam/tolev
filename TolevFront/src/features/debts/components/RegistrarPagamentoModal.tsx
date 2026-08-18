import { Check, TriangleAlert, X } from "lucide-react-native";
import { useEffect, useMemo, useState } from "react";
import { Modal, Pressable, ScrollView, Text, TextInput, View } from "react-native";
import { colors, shadows } from "../../../theme";
import { isoToBrDate } from "../../../util/date";
import { digitsToDecimal, decimalToDigits, maskCurrency } from "../../../util/masks";
import { brl, parcelasEmAberto, type DividaView, type ParcelaView } from "../constants/dividas";

type Props = {
  visible: boolean;
  divida: DividaView;
  submitting?: boolean;
  onClose: () => void;
  /** Called with the exact amount paid for each selected installment. */
  onConfirm: (pagamentos: { numero: number; valorPago: number }[]) => void;
};

/**
 * Bottom sheet to register paid installments. Each row shows the installment's
 * real value and due date as returned by the backend — with SAC (or a PRICE
 * table whose last installment absorbs the rounding) they are not all equal, so
 * a single "valor da parcela" field cannot describe the payment.
 */
export default function RegistrarPagamentoModal({
  visible,
  divida,
  submitting = false,
  onClose,
  onConfirm,
}: Props) {
  const abertas = useMemo(() => parcelasEmAberto(divida), [divida]);

  const [selected, setSelected] = useState<number[]>([]);
  /** Digits of the total actually paid; empty means "o valor previsto". */
  const [totalDigits, setTotalDigits] = useState("");
  const [editandoTotal, setEditandoTotal] = useState(false);

  useEffect(() => {
    if (visible) {
      setSelected([]);
      setTotalDigits("");
      setEditandoTotal(false);
    }
  }, [visible]);

  const escolhidas = abertas.filter((p) => selected.includes(p.numero));
  const previsto = escolhidas.reduce((s, p) => s + p.valor, 0);
  const totalInformado = editandoTotal ? digitsToDecimal(totalDigits) : previsto;
  const diferenca = Number((totalInformado - previsto).toFixed(2));

  const canConfirm = escolhidas.length > 0 && totalInformado > 0 && !submitting;

  function toggle(n: number) {
    setSelected((cur) => (cur.includes(n) ? cur.filter((x) => x !== n) : [...cur, n]));
  }

  /**
   * Rateia o total informado entre as parcelas em proporção ao valor de cada
   * uma, para que a soma feche exatamente com o que a pessoa digitou. A última
   * leva a diferença de arredondamento.
   */
  function confirmar() {
    if (!canConfirm) return;
    if (!editandoTotal || diferenca === 0) {
      onConfirm(escolhidas.map((p) => ({ numero: p.numero, valorPago: p.valor })));
      return;
    }

    let acumulado = 0;
    const pagamentos = escolhidas.map((p, i) => {
      const ultimo = i === escolhidas.length - 1;
      const valorPago = ultimo
        ? Number((totalInformado - acumulado).toFixed(2))
        : Number(((p.valor / previsto) * totalInformado).toFixed(2));
      acumulado = Number((acumulado + valorPago).toFixed(2));
      return { numero: p.numero, valorPago };
    });
    onConfirm(pagamentos);
  }

  function comecarAEditarTotal() {
    setEditandoTotal(true);
    setTotalDigits(decimalToDigits(previsto));
  }

  return (
    <Modal visible={visible} transparent animationType="slide" onRequestClose={onClose}>
      <View className="flex-1 justify-end" style={{ backgroundColor: "rgba(0,0,0,0.35)" }}>
        <Pressable className="absolute inset-0" onPress={onClose} />

        <View className="bg-bg rounded-t-sheet px-5 pt-5 pb-8" style={shadows.card}>
          <View className="items-center mb-3">
            <View className="w-10 h-1 rounded-pill bg-line-soft" />
          </View>

          <View className="flex-row items-start justify-between mb-1">
            <View className="flex-1 pr-3">
              <Text className="font-bold text-lg text-ink">Registrar pagamento</Text>
              <Text className="text-xs text-muted mt-0.5 font-regular">{divida.nome}</Text>
            </View>
            <Pressable
              onPress={onClose}
              className="w-8 h-8 rounded-full bg-primary-50 items-center justify-center"
            >
              <X size={18} color={colors.text.secondary} strokeWidth={2} />
            </Pressable>
          </View>

          <Text className="text-xs text-muted font-bold tracking-[0.5px] mt-4 mb-2.5">
            QUAIS PARCELAS VOCÊ PAGOU?
          </Text>

          {abertas.length === 0 ? (
            <View className="bg-surface rounded-lg px-4 py-5 items-center" style={shadows.card}>
              <Text className="text-sm text-muted font-regular text-center">
                Não há parcelas em aberto nessa dívida.
              </Text>
            </View>
          ) : (
            <ScrollView style={{ maxHeight: 240 }} showsVerticalScrollIndicator={false}>
              <View className="gap-2">
                {abertas.map((p) => (
                  <ParcelaRow
                    key={p.numero}
                    parcela={p}
                    selected={selected.includes(p.numero)}
                    onPress={() => toggle(p.numero)}
                  />
                ))}
              </View>
            </ScrollView>
          )}

          <View className="bg-surface rounded-lg px-4 py-4 mt-5" style={shadows.card}>
            <View className="flex-row justify-between items-center">
              <Text className="text-sm text-muted font-regular">
                {escolhidas.length} {escolhidas.length === 1 ? "parcela" : "parcelas"}
              </Text>
              {editandoTotal ? (
                <TextInput
                  className="font-bold text-lg text-primary-700 text-right min-w-[130px] py-0"
                  value={maskCurrency(totalDigits)}
                  onChangeText={(t) => setTotalDigits(t.replace(/\D/g, ""))}
                  keyboardType="number-pad"
                  placeholder="R$ 0,00"
                  placeholderTextColor={colors.border.soft}
                  autoFocus
                />
              ) : (
                <Text className="font-bold text-lg text-primary-700">{brl(previsto)}</Text>
              )}
            </View>

            {escolhidas.length > 0 && (
              <Pressable onPress={comecarAEditarTotal} hitSlop={6} className="mt-2 active:opacity-60">
                <Text className="text-xs text-primary-700 font-semibold">
                  {editandoTotal ? "Valor previsto: " + brl(previsto) : "Paguei um valor diferente"}
                </Text>
              </Pressable>
            )}

            {editandoTotal && diferenca !== 0 && (
              <View className="flex-row items-start gap-2 mt-2.5">
                <TriangleAlert
                  size={14}
                  color={diferenca < 0 ? colors.coral[500] : colors.primary[700]}
                  strokeWidth={2.2}
                />
                <Text
                  className="flex-1 text-[11px] font-regular leading-[15px]"
                  style={{ color: diferenca < 0 ? colors.coral[500] : colors.text.secondary }}
                >
                  {diferenca > 0
                    ? `${brl(diferenca)} acima do previsto — o excedente abate o saldo devedor.`
                    : `${brl(Math.abs(diferenca))} abaixo do previsto. As parcelas serão marcadas como pagas mesmo assim.`}
                </Text>
              </View>
            )}
          </View>

          <Pressable
            onPress={confirmar}
            className="h-12 rounded-pill flex-row items-center justify-center gap-2 mt-4"
            style={{ backgroundColor: canConfirm ? colors.coral[500] : colors.border.soft }}
          >
            {!submitting && <Check size={18} color={colors.surface} strokeWidth={2.5} />}
            <Text className="font-bold text-base" style={{ color: colors.surface }}>
              {submitting ? "Registrando..." : "Registrar pagamento"}
            </Text>
          </Pressable>
        </View>
      </View>
    </Modal>
  );
}

function ParcelaRow({
  parcela,
  selected,
  onPress,
}: {
  parcela: ParcelaView;
  selected: boolean;
  onPress: () => void;
}) {
  const atrasada = parcela.status === "ATRASADA";

  return (
    <Pressable
      onPress={onPress}
      className="flex-row items-center gap-3 rounded-lg px-3.5 py-3"
      style={[
        shadows.card,
        {
          backgroundColor: selected ? colors.primary[25] : colors.surface,
          borderWidth: 2,
          borderColor: selected ? colors.primary[700] : "transparent",
        },
      ]}
    >
      <View
        className="w-6 h-6 rounded-full items-center justify-center"
        style={{
          backgroundColor: selected ? colors.primary[700] : "transparent",
          borderWidth: selected ? 0 : 2,
          borderColor: colors.border.soft,
        }}
      >
        {selected && <Check size={14} color={colors.surface} strokeWidth={3} />}
      </View>

      <View className="flex-1">
        <Text className="text-sm font-semibold text-ink">Parcela {parcela.numero}</Text>
        <Text
          className="text-[11px] mt-0.5 font-regular"
          style={{ color: atrasada ? colors.coral[500] : colors.text.secondary }}
        >
          {atrasada ? "Em atraso · " : "Vence "}
          {isoToBrDate(parcela.vencimento) || "sem data"}
        </Text>
      </View>

      <View className="items-end">
        <Text className="text-sm font-bold text-ink">{brl(parcela.valor)}</Text>
        {parcela.juros > 0 && (
          <Text className="text-[10px] text-muted mt-0.5 font-regular">
            {brl(parcela.juros)} de juros
          </Text>
        )}
      </View>
    </Pressable>
  );
}
