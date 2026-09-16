import { useNavigation } from "@react-navigation/native";
import { AnimatePresence, MotiView } from "moti";
import { CheckCircle2, FileSearch, TriangleAlert } from "lucide-react-native";
import { Pressable, Text, View } from "react-native";
import { colors, shadows } from "../../../theme";
import { useImportacoesExtrato } from "../hooks/useImportacoesExtrato";

/**
 * O aviso que segue o usuário pelo app enquanto o extrato é lido.
 *
 * É o que permite abandonar a tela de acompanhamento sem perder o resultado:
 * fica montado em todas as abas, some quando não há nada a dizer e reaparece
 * assim que o extrato termina — inclusive na abertura seguinte do app, porque o
 * "já viu isso?" mora no servidor, não nesta sessão.
 *
 * Nunca atualiza nada sozinho. Ele avisa e leva à tela onde o usuário decide.
 *
 * @param bottom 146 = os 78 onde o FAB começa + os 56 dele + uma folga. O aviso
 *               empilha acima do botão em vez de disputar o mesmo canto.
 */
export default function ExtratoStatusBanner({ bottom = 146 }: { bottom?: number }) {
  const navigation = useNavigation<any>();
  const { emAndamento, aguardandoUsuario } = useImportacoesExtrato();

  // Um resultado por ver ganha da leitura em curso: é o que pede ação.
  const importacao = aguardandoUsuario ?? emAndamento;
  if (!importacao) {
    return null;
  }

  const pronto = importacao.status === "CONCLUIDA";
  const falhou = importacao.status === "FALHOU";
  const importados = importacao.lancamentosImportados ?? 0;

  const abrir = () =>
    navigation.navigate("ExtratoProcessando", { idImportacao: importacao.id });

  return (
    <AnimatePresence>
      <MotiView
        from={{ opacity: 0, translateY: 16 }}
        animate={{ opacity: 1, translateY: 0 }}
        exit={{ opacity: 0, translateY: 16 }}
        style={{ position: "absolute", left: 16, right: 16, bottom }}
      >
        <Pressable
          onPress={abrir}
          className="flex-row items-center gap-3 rounded-[16px] bg-surface px-4 py-3.5 active:opacity-90"
          style={[
            shadows.card,
            { borderWidth: 1, borderColor: falhou ? "#FEC9BB" : colors.primary[100] },
          ]}
        >
          <View
            className="w-10 h-10 rounded-full items-center justify-center"
            style={{ backgroundColor: falhou ? "#FEE7E1" : colors.primary[100] }}
          >
            {falhou ? (
              <TriangleAlert size={19} color={colors.coral[500]} strokeWidth={2} />
            ) : pronto ? (
              <CheckCircle2 size={19} color={colors.primary[700]} strokeWidth={2} />
            ) : (
              <MotiView
                from={{ opacity: 0.5 }}
                animate={{ opacity: 1 }}
                transition={{ type: "timing", duration: 900, loop: true }}
              >
                <FileSearch size={19} color={colors.primary[700]} strokeWidth={2} />
              </MotiView>
            )}
          </View>

          <View className="flex-1">
            <Text className="text-[14px] font-bold text-ink">
              {falhou
                ? "Não deu para importar"
                : pronto
                  ? "Seu extrato está pronto"
                  : "Lendo seu extrato…"}
            </Text>
            <Text className="text-[12px] text-muted font-regular mt-0.5" numberOfLines={1}>
              {falhou
                ? "Toque para ver o motivo"
                : pronto
                  ? `${importados} ${importados === 1 ? "lançamento novo" : "lançamentos novos"} · toque para atualizar`
                  : "Pode continuar usando o app normalmente"}
            </Text>
          </View>

          {pronto ? (
            <View className="px-3.5 py-2 rounded-pill bg-coral-500">
              <Text className="text-[12px] font-bold text-white">Atualizar</Text>
            </View>
          ) : null}
        </Pressable>
      </MotiView>
    </AnimatePresence>
  );
}
