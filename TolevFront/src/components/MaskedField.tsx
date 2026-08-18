import type { LucideIcon } from "lucide-react-native";
import { Text, TextInput, View } from "react-native";
import { colors, shadows } from "../theme";
import { maskCurrency, maskDate, maskInteger, maskPercent, onlyDigits } from "../util/masks";

export type MaskType = "currency" | "percent" | "integer" | "date" | "text";

type Props = {
  label: string;
  /** Raw state: digits for currency/percent/integer, "DD/MM/AAAA" for date. */
  value: string;
  onChange: (raw: string) => void;
  type: MaskType;
  placeholder?: string;
  /** Small note under the input, e.g. "por mês" or an example. */
  hint?: string;
  icon?: LucideIcon;
  autoFocus?: boolean;
};

const KEYBOARD: Record<MaskType, "number-pad" | "default"> = {
  currency: "number-pad",
  percent: "number-pad",
  integer: "number-pad",
  date: "number-pad",
  text: "default",
};

/** Formats the stored raw value for display. */
function format(value: string, type: MaskType): string {
  switch (type) {
    case "currency":
      return maskCurrency(value);
    case "percent":
      return maskPercent(value);
    default:
      return value;
  }
}

/** Turns whatever the user typed back into the raw value we store. */
function toRaw(text: string, type: MaskType): string {
  switch (type) {
    case "currency":
    case "percent":
      return onlyDigits(text);
    case "integer":
      return maskInteger(text);
    case "date":
      return maskDate(text);
    default:
      return text;
  }
}

/**
 * One labelled input card. Money and percentages fill from the right (the last
 * two digits are always the cents/decimals); dates fill from the left into
 * DD/MM/AAAA. See {@link ../util/masks}.
 */
export default function MaskedField({
  label,
  value,
  onChange,
  type,
  placeholder,
  hint,
  icon: Icon,
  autoFocus,
}: Props) {
  const filled = value.length > 0;

  return (
    <View className="bg-surface rounded-lg px-4 pt-3 pb-3.5" style={shadows.card}>
      <View className="flex-row items-center gap-2">
        {Icon && <Icon size={14} color={colors.text.secondary} strokeWidth={2.2} />}
        <Text className="text-xs text-muted font-semibold">{label}</Text>
      </View>

      <TextInput
        className="font-bold text-lg text-ink py-0 mt-1"
        value={format(value, type)}
        onChangeText={(text) => onChange(toRaw(text, type))}
        placeholder={placeholder}
        placeholderTextColor={colors.border.soft}
        keyboardType={KEYBOARD[type]}
        autoFocus={autoFocus}
        maxLength={type === "date" ? 10 : undefined}
      />

      {hint && filled ? (
        <Text className="text-[11px] text-muted mt-1 font-regular">{hint}</Text>
      ) : null}
    </View>
  );
}
