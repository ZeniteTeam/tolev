import { Pressable, Text, View } from "react-native";
import { colors, shadows } from "../../../theme";
import { METODO_PAGAMENTO, type MetodoPagamento } from "../../../types/transacao";
import { METODO_ICON, METODO_LABEL } from "../constants/transacoes";

type Props = {
  value: MetodoPagamento;
  onChange: (metodo: MetodoPagamento) => void;
};

/** Chips de forma de pagamento — cinco opções, cabem em duas linhas. */
export default function MetodoPicker({ value, onChange }: Props) {
  return (
    <View className="flex-row flex-wrap gap-2.5">
      {METODO_PAGAMENTO.map((m) => {
        const active = value === m;
        const Icon = METODO_ICON[m];

        return (
          <Pressable
            key={m}
            onPress={() => onChange(m)}
            className="rounded-lg items-center justify-center py-3"
            style={[
              {
                width: "31.5%",
                backgroundColor: active ? colors.primary[100] : colors.surface,
              },
              active ? { borderWidth: 2, borderColor: colors.primary[700] } : shadows.card,
            ]}
          >
            <Icon
              size={20}
              color={active ? colors.primary[700] : colors.text.secondary}
              strokeWidth={2}
            />
            <Text
              className="text-[11px] mt-1.5 font-semibold"
              style={{ color: active ? colors.primary[700] : colors.text.secondary }}
            >
              {METODO_LABEL[m]}
            </Text>
          </Pressable>
        );
      })}
    </View>
  );
}
