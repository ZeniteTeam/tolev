import { Text, View } from "react-native";
import { GiftedDonut } from "../../../components";
import { colors, shadows } from "../../../theme";
import { brl, type DividaView } from "../constants/dividas";

type Props = {
  divida: DividaView;
  /** Atraso da entrada, para o gráfico chegar depois do card que o contém. */
  delay?: number;
};

/**
 * O custo real: quanto do total a pagar é a dívida em si e quanto é juros.
 *
 * O saldo devedor sozinho não diz o preço do dinheiro — "peguei 8.400 e vou
 * devolver 12.900" diz, e é o número que muda decisão.
 */
export default function CustoRealCard({ divida, delay = 0 }: Props) {
  const principal = divida.saldo;
  const juros = divida.totalJuros;
  const total = divida.totalAPagar;

  // Sem juros calculados não há custo a mostrar — o donut seria um círculo só.
  if (juros <= 0 || total <= 0) return null;

  const pctJuros = Math.round((juros / total) * 100);

  return (
    <View className="bg-surface rounded-[18px] p-5 mb-4" style={shadows.card}>
      <Text className="font-bold text-base text-ink">O custo real desta dívida</Text>
      <Text className="text-[12px] text-muted mt-0.5 font-regular">
        Quanto você devolve além do que deve
      </Text>

      <View className="flex-row items-center gap-5 mt-[18px]">
        <GiftedDonut
          delay={delay}
          size={116}
          stroke={16}
          data={[
            { value: principal, color: colors.primary[500] },
            { value: juros, color: colors.coral[500] },
          ]}
          center={
            <View className="items-center">
              <Text className="font-bold text-[20px] text-coral-500">{pctJuros}%</Text>
              <Text className="text-[10px] text-muted font-regular">juros</Text>
            </View>
          }
        />

        <View className="flex-1 gap-3.5">
          <Linha color={colors.primary[500]} label="Saldo devedor" valor={brl(principal)} />
          <Linha color={colors.coral[500]} label="Juros até o fim" valor={brl(juros)} />
          <View className="pt-3 border-t border-t-line-soft">
            <Text className="text-[11px] text-muted font-regular">Total a devolver</Text>
            <Text className="font-bold text-[18px] text-ink mt-0.5">{brl(total)}</Text>
          </View>
        </View>
      </View>
    </View>
  );
}

function Linha({ color, label, valor }: { color: string; label: string; valor: string }) {
  return (
    <View className="flex-row items-center gap-2.5">
      <View className="w-2.5 h-2.5 rounded-[3px]" style={{ backgroundColor: color }} />
      <View className="flex-1">
        <Text className="text-[11px] text-muted font-regular">{label}</Text>
        <Text className="font-bold text-[15px] text-ink mt-0.5">{valor}</Text>
      </View>
    </View>
  );
}
