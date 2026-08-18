import { Check, Landmark, Wallet } from "lucide-react-native";
import { ActivityIndicator, Pressable, Text, View } from "react-native";
import { colors, shadows } from "../../../theme";
import type { ContaResponse } from "../../../types/transacao";

type Props = {
  contas: ContaResponse[];
  /** null = dinheiro / carteira. */
  value: number | null;
  onChange: (idConta: number | null) => void;
  loading?: boolean;
};

/**
 * Escolha da origem do dinheiro. "Dinheiro / carteira" vem sempre primeiro e é
 * a opção padrão: o app ainda não conecta bancos, então a lista de contas
 * costuma estar vazia e isso não é um estado de erro.
 */
export default function ContaPicker({ contas, value, onChange, loading }: Props) {
  return (
    <View className="gap-2.5">
      <Row
        icon={Wallet}
        title="Dinheiro / carteira"
        subtitle="Não movimenta o saldo de nenhuma conta"
        selected={value === null}
        onPress={() => onChange(null)}
      />

      {loading ? (
        <View className="py-4 items-center">
          <ActivityIndicator color={colors.primary[700]} />
        </View>
      ) : (
        contas.map((c) => (
          <Row
            key={c.id}
            icon={Landmark}
            title={c.nomeConta ?? c.tituloBanco ?? `Conta ${c.id}`}
            subtitle={subtitleFor(c)}
            selected={value === c.id}
            onPress={() => onChange(c.id)}
          />
        ))
      )}
    </View>
  );
}

function subtitleFor(conta: ContaResponse): string {
  const partes = [conta.tituloBanco, conta.numeroConta].filter(Boolean);
  return partes.length > 0 ? partes.join(" · ") : "Conta conectada";
}

function Row({
  icon: Icon,
  title,
  subtitle,
  selected,
  onPress,
}: {
  icon: typeof Wallet;
  title: string;
  subtitle: string;
  selected: boolean;
  onPress: () => void;
}) {
  return (
    <Pressable
      onPress={onPress}
      className="bg-surface rounded-lg px-4 py-3.5 flex-row items-center gap-3 active:opacity-90"
      style={[
        selected
          ? { borderWidth: 2, borderColor: colors.primary[700], backgroundColor: colors.primary[25] }
          : shadows.card,
      ]}
    >
      <View
        className="w-10 h-10 rounded-[11px] items-center justify-center"
        style={{ backgroundColor: selected ? colors.primary[700] : colors.primary[50] }}
      >
        <Icon
          size={19}
          color={selected ? colors.surface : colors.text.secondary}
          strokeWidth={2}
        />
      </View>

      <View className="flex-1">
        <Text
          className="font-semibold text-[15px]"
          style={{ color: selected ? colors.primary[700] : colors.text.primary }}
          numberOfLines={1}
        >
          {title}
        </Text>
        <Text className="text-[12px] text-muted mt-0.5 font-regular" numberOfLines={1}>
          {subtitle}
        </Text>
      </View>

      <View
        className="w-6 h-6 rounded-full items-center justify-center"
        style={{
          backgroundColor: selected ? colors.primary[700] : "transparent",
          borderWidth: selected ? 0 : 2,
          borderColor: colors.border.soft,
        }}
      >
        {selected && <Check size={15} color={colors.surface} strokeWidth={3} />}
      </View>
    </Pressable>
  );
}
