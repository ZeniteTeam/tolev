import { ChevronRight } from "lucide-react-native";
import { Pressable, Text, View } from "react-native";
import { Progress } from "../../../components";
import { colors, shadows } from "../../../theme";
import {
  brl,
  isQuitada,
  pctQuitado,
  quebraEmAberto,
  totalEmAberto,
  type DividaView,
} from "../constants/dividas";

type Props = {
  divida: DividaView;
  onPress?: () => void;
};

export default function DebtCard({ divida: d, onPress }: Props) {
  const quitada = isQuitada(d);
  const pctPago = quitada ? 100 : pctQuitado(d);
  const falta = totalEmAberto(d);
  const quebra = quebraEmAberto(d);
  const Icon = d.icon;

  return (
    <Pressable
      onPress={onPress}
      className="bg-surface rounded-[18px] p-[18px] mb-3.5 gap-4 active:opacity-90"
      style={shadows.card}
    >
      <View className="flex-row items-center gap-3">
        <View
          className="w-10 h-10 rounded-[11px] items-center justify-center"
          style={{ backgroundColor: d.bankColor }}
        >
          <Icon size={19} color="#fff" strokeWidth={2} />
        </View>
        <View className="flex-1">
          <Text className="font-semibold text-[15px] text-ink">{d.nome}</Text>
          <Text className="text-[12px] text-muted mt-0.5 font-regular">{d.banco}</Text>
        </View>
        <ChevronRight size={20} color={colors.text.secondary} strokeWidth={2} />
      </View>

      <View className="flex-row justify-between items-end">
        <View className="flex-1 pr-3">
          {/* Quitada não tem o que faltar: "Falta pagar R$ 0" está certo e lê
              como erro de conta. O que interessa depois de pagar é o preço
              total que a dívida teve. */}
          <Text className="text-[11px] text-muted mb-0.5 font-regular">
            {quitada ? "Custou no total" : "Falta pagar"}
          </Text>
          <Text className="font-bold text-[20px] text-ink">
            {brl(quitada ? d.totalAPagar : falta)}
          </Text>
          {/* A quebra fica junto do número: sem ela "falta pagar" e o saldo que
              o banco informa parecem contradição, e o usuário confia no menor. */}
          {quitada ? (
            d.totalJuros > 0 && (
              <Text className="text-[11px] text-muted mt-0.5 font-regular">
                {brl(d.totalJuros)} disso foram juros
              </Text>
            )
          ) : (
            quebra.juros > 0 && (
              <Text className="text-[11px] text-muted mt-0.5 font-regular">
                {brl(quebra.principal)} + {brl(quebra.juros)} de juros
              </Text>
            )
          )}
        </View>
        <View className="flex-row gap-[18px]">
          <View className="items-end">
            <Text className="text-[11px] text-muted mb-0.5 font-regular">Juros a.m.</Text>
            <Text
              className="font-bold text-[14px]"
              style={{ color: d.juros >= 8 ? colors.coral[500] : colors.text.primary }}
            >
              {d.juros.toFixed(1).replace(".", ",")}%
            </Text>
          </View>
          <View className="items-end">
            <Text className="text-[11px] text-muted mb-0.5 font-regular">Mínimo</Text>
            <Text className="font-bold text-[14px] text-ink">{brl(d.min)}</Text>
          </View>
        </View>
      </View>

      <View>
        <View className="flex-row justify-between mb-1.5">
          <Text className="text-[11px] text-muted font-regular">
            {quitada ? "Dívida quitada" : "Progresso de quitação"}
          </Text>
          <Text className="text-[12px] text-teal-500 font-bold">{pctPago}%</Text>
        </View>
        <Progress pct={pctPago} height={6} />
      </View>
    </Pressable>
  );
}
