import { TextInput, View } from "react-native";
import { Text } from "react-native";
import { colors } from "../../../theme";

type Props = {
  /** Raw digits string of the amount in reais (e.g. "3500"). */
  value: string;
  onChange: (digits: string) => void;
  autoFocus?: boolean;
};

/** Big, centered "R$ 0" input for the monthly income step. */
export default function CurrencyInput({ value, onChange, autoFocus }: Props) {
  const display = value ? Number(value).toLocaleString("pt-BR") : "";

  return (
    <View className="items-center mt-6">
      <View className="flex-row items-center justify-center">
        <Text className="font-bold text-[34px] text-muted mr-2">R$</Text>
        <TextInput
          value={display}
          onChangeText={(t) => onChange(t.replace(/\D/g, ""))}
          keyboardType="number-pad"
          placeholder="0"
          placeholderTextColor={colors.border.soft}
          autoFocus={autoFocus}
          className="font-bold text-[48px] text-ink text-center min-w-[120px] py-0"
        />
      </View>
      <Text className="text-[13px] text-muted mt-3 font-regular">Valor aproximado por mês</Text>
    </View>
  );
}
