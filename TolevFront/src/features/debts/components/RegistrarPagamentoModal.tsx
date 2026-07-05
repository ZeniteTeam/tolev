import { Check, X } from "lucide-react-native";
import { useEffect, useMemo, useState } from "react";
import { Modal, Pressable, ScrollView, Text, TextInput, View } from "react-native";
import { colors, shadows } from "../../../theme";
import { formatCurrencyBRL, parseCurrencyToNumber } from "../../../util/currency";
import { brl, type DividaView } from "../constants/dividas";

type Props = {
  visible: boolean;
  divida: DividaView;
  submitting?: boolean;
  onClose: () => void;
  /** Called with the total value (value-per-parcela × selected parcelas). */
  onConfirm: (total: number, parcelas: number[]) => void;
};

/** Bottom-sheet modal to register one or more paid installments for a debt. */
export default function RegistrarPagamentoModal({
  visible,
  divida,
  submitting = false,
  onClose,
  onConfirm,
}: Props) {
  const min = divida.min;
  // Total installments of the debt, capped for rendering.
  const totalParcelas = Math.min(60, Math.max(1, divida.parcelas));

  const [selected, setSelected] = useState<number[]>([]);
  const [valueStr, setValueStr] = useState(String(min));

  // Reset the form whenever the sheet is (re)opened for a debt.
  useEffect(() => {
    if (visible) {
      setSelected([]);
      setValueStr(String(min));
    }
  }, [visible, min]);

  const valueNum = parseCurrencyToNumber(valueStr);
  const belowMin = valueNum < min;
  const total = useMemo(() => valueNum * selected.length, [valueNum, selected.length]);
  const canConfirm = selected.length > 0 && !belowMin && !submitting;

  const toggle = (n: number) =>
    setSelected((cur) => (cur.includes(n) ? cur.filter((x) => x !== n) : [...cur, n]));

  return (
    <Modal visible={visible} transparent animationType="slide" onRequestClose={onClose}>
      <View className="flex-1 justify-end" style={{ backgroundColor: "rgba(0,0,0,0.35)" }}>
        <Pressable className="absolute inset-0" onPress={onClose} />

        <View className="bg-bg rounded-t-[28px] px-5 pt-5 pb-8" style={shadows.card}>
          <View className="items-center mb-3">
            <View className="w-10 h-1 rounded-pill bg-line-soft" />
          </View>

          <View className="flex-row items-start justify-between mb-1">
            <View className="flex-1 pr-3">
              <Text className="font-bold text-[18px] text-ink">Registrar pagamento</Text>
              <Text className="text-[12px] text-muted mt-0.5 font-regular">{divida.nome}</Text>
            </View>
            <Pressable onPress={onClose} className="w-8 h-8 rounded-full bg-primary-50 items-center justify-center">
              <X size={18} color={colors.text.secondary} strokeWidth={2} />
            </Pressable>
          </View>

          <Text className="text-[11px] text-muted font-bold tracking-[0.5px] mt-4 mb-2.5">
            PARCELAS PAGAS
          </Text>
          <ScrollView style={{ maxHeight: 168 }} showsVerticalScrollIndicator={false}>
            <View className="flex-row flex-wrap gap-2.5">
              {Array.from({ length: totalParcelas }, (_, i) => i + 1).map((n) => {
                const paid = divida.parcelasPagas.includes(n);
                const active = selected.includes(n);
                if (paid) {
                  return (
                    <View
                      key={n}
                      className="w-11 h-11 rounded-[12px] items-center justify-center"
                      style={{ backgroundColor: colors.primary[100] }}
                    >
                      <Check size={16} color={colors.primary[700]} strokeWidth={2.5} />
                    </View>
                  );
                }
                return (
                  <Pressable
                    key={n}
                    onPress={() => toggle(n)}
                    className="w-11 h-11 rounded-[12px] items-center justify-center"
                    style={
                      active
                        ? { backgroundColor: colors.primary[700] }
                        : { backgroundColor: colors.surface, borderWidth: 1.5, borderColor: colors.primary[100] }
                    }
                  >
                    <Text
                      className="font-bold text-[14px]"
                      style={{ color: active ? "#fff" : colors.text.primary }}
                    >
                      {n}
                    </Text>
                  </Pressable>
                );
              })}
            </View>
          </ScrollView>

          <Text className="text-[11px] text-muted font-bold tracking-[0.5px] mt-5 mb-2.5">
            VALOR DA PARCELA
          </Text>
          <View
            className="h-12 rounded-pill bg-surface flex-row items-center px-[18px] gap-2"
            style={[shadows.card, belowMin && { borderWidth: 1.5, borderColor: colors.coral[500] }]}
          >
            <Text className="text-muted font-semibold text-base">R$</Text>
            <TextInput
              className="flex-1 font-regular text-base text-ink py-0"
              value={valueStr}
              onChangeText={setValueStr}
              keyboardType="numeric"
              placeholder={String(min)}
              placeholderTextColor={colors.text.secondary}
            />
          </View>
          <Text
            className="text-[11px] mt-1.5 pl-1 font-regular"
            style={{ color: belowMin ? colors.coral[500] : colors.text.secondary }}
          >
            {belowMin ? `Valor mínimo: ${brl(min)}` : `Mínimo ${brl(min)} · você pode pagar mais`}
          </Text>

          <View className="flex-row justify-between items-center mt-5 mb-4 px-1">
            <Text className="text-[13px] text-muted font-regular">
              {selected.length} {selected.length === 1 ? "parcela" : "parcelas"}
            </Text>
            <View className="flex-row items-baseline gap-1.5">
              <Text className="text-[12px] text-muted font-regular">Total</Text>
              <Text className="font-bold text-[20px] text-primary-700">
                {formatCurrencyBRL(total)}
              </Text>
            </View>
          </View>

          <Pressable
            onPress={() => canConfirm && onConfirm(total, selected)}
            className="h-12 rounded-[36px] flex-row items-center justify-center gap-2"
            style={{ backgroundColor: canConfirm ? colors.coral[500] : colors.border.soft }}
          >
            {!submitting && <Check size={18} color="#fff" strokeWidth={2.5} />}
            <Text className="font-bold text-[16px] text-white">
              {submitting ? "Registrando..." : "Registrar pagamento"}
            </Text>
          </Pressable>
        </View>
      </View>
    </Modal>
  );
}
