import { ArrowUpRight, type LucideIcon } from "lucide-react-native";
import { Pressable, ScrollView, Text, View } from "react-native";
import { colors, shadows } from "../../../theme";

export type Atalho = {
  key: string;
  icon: LucideIcon;
  title: string;
  /** Uma linha de estado real — nunca texto decorativo. */
  sub: string;
  /** Chama atenção quando há algo pendente de fato. */
  destaque?: boolean;
  onPress: () => void;
};

/**
 * Faixa de cartões que desliza na horizontal, no lugar onde os apps de banco
 * põem os atalhos da conta.
 *
 * Cada cartão carrega um estado, não só um nome: "5 gastos · R$ 1.640" diz se
 * vale entrar. Atalho que só repete o próprio título vira enfeite e some da
 * percepção depois da primeira semana.
 */
export default function AtalhosCards({ atalhos }: { atalhos: Atalho[] }) {
  return (
    <ScrollView
      horizontal
      showsHorizontalScrollIndicator={false}
      /* O padding vai no content: assim o primeiro cartão alinha com o resto
         da tela e o último não encosta na borda ao fim do scroll. */
      contentContainerStyle={{ paddingRight: 4, gap: 10 }}
      className="mb-[18px] -mx-1 px-1"
    >
      {atalhos.map((a) => (
        <AtalhoCard key={a.key} atalho={a} />
      ))}
    </ScrollView>
  );
}

function AtalhoCard({ atalho }: { atalho: Atalho }) {
  const { icon: Icon, title, sub, destaque, onPress } = atalho;

  return (
    <Pressable
      onPress={onPress}
      className="bg-surface rounded-[16px] p-4 active:opacity-90"
      style={[shadows.card, { width: 168 }]}
    >
      <View className="flex-row items-center justify-between mb-3">
        <View
          className="w-9 h-9 rounded-[11px] items-center justify-center"
          style={{
            backgroundColor: destaque ? "rgba(254,111,80,0.12)" : colors.primary[100],
          }}
        >
          <Icon
            size={17}
            color={destaque ? colors.coral[500] : colors.primary[700]}
            strokeWidth={2}
          />
        </View>
        <ArrowUpRight size={16} color={colors.text.secondary} strokeWidth={2} />
      </View>
      <Text className="font-bold text-[14px] text-ink">{title}</Text>
      <Text
        className="text-[12px] mt-0.5 font-regular"
        style={{ color: destaque ? colors.coral[500] : colors.text.secondary }}
        numberOfLines={1}
      >
        {sub}
      </Text>
    </Pressable>
  );
}
