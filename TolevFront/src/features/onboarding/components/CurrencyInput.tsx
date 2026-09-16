import { TextInput, View } from "react-native";
import { Text } from "react-native";
import { colors, shadows } from "../../../theme";
import { maskCurrency, onlyDigits } from "../../../util/masks";

/** Até R$ 9.999.999,99 — acima disso o número não cabe na linha. */
const MAX_DIGITS = 9;

type Props = {
  /** Estado cru: dígitos, com os dois últimos valendo os centavos (ex.: "350000" = R$ 3.500,00). */
  value: string;
  onChange: (digits: string) => void;
  autoFocus?: boolean;
};

/** Encolhe o número conforme ele cresce, para nunca quebrar em duas linhas. */
function fontSizeFor(length: number): number {
  if (length <= 12) return 40;
  if (length <= 15) return 32;
  return 26;
}

export default function CurrencyInput({ value, onChange, autoFocus }: Props) {
  const display = maskCurrency(value);
  const fontSize = fontSizeFor(display.length);

  return (
    <View className="items-center">
      <View
        className="w-full bg-surface rounded-lg px-5 py-6"
        style={shadows.card}
      >
        <TextInput
          value={display}
          onChangeText={(t) => onChange(onlyDigits(t).slice(0, MAX_DIGITS))}
          keyboardType="number-pad"
          placeholder="R$ 0,00"
          placeholderTextColor={colors.border.soft}
          autoFocus={autoFocus}
          selectionColor={colors.primary[500]}
          className="w-full font-bold text-ink text-center py-0"
          style={{ fontSize, lineHeight: fontSize + 8 }}
        />
      </View>

      <Text className="text-[13px] text-muted mt-3 font-regular text-center">
        Valor aproximado por mês
      </Text>
    </View>
  );
}
