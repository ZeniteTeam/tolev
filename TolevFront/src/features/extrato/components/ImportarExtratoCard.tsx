import { ChevronRight, FileUp } from "lucide-react-native";
import { Pressable, Text, View } from "react-native";
import { colors, shadows } from "../../../theme";
import { useImportacoesExtrato } from "../hooks/useImportacoesExtrato";

type Props = {
  onImportar: () => void;
  onAcompanhar: () => void;
};

/**
 * A porta de entrada do recurso, dentro de Finanças.
 *
 * Enquanto há um extrato sendo lido, o card troca de função em vez de sumir ou
 * de aceitar um segundo envio: leva ao acompanhamento. O servidor recusa dois
 * uploads simultâneos do mesmo usuário (duas leituras concorrentes duplicariam
 * o período em comum), então oferecer o botão de enviar aqui só produziria um
 * erro que dá para evitar.
 */
export default function ImportarExtratoCard({ onImportar, onAcompanhar }: Props) {
  const { emAndamento } = useImportacoesExtrato();
  const processando = emAndamento != null;

  return (
    <Pressable
      onPress={processando ? onAcompanhar : onImportar}
      className="bg-surface rounded-[18px] p-[18px] mb-3.5 flex-row items-center gap-3.5 active:opacity-90"
      style={shadows.card}
    >
      <View className="w-11 h-11 rounded-[12px] bg-primary-100 items-center justify-center">
        <FileUp size={21} color={colors.primary[700]} strokeWidth={2} />
      </View>

      <View className="flex-1">
        <Text className="font-bold text-[15px] text-ink">
          {processando ? "Lendo seu extrato…" : "Importe seu extrato"}
        </Text>
        <Text className="text-[12px] text-muted mt-0.5 font-regular leading-[17px]">
          {processando
            ? "Toque para acompanhar. Pode continuar usando o app."
            : "Suba o PDF do banco e a gente lança tudo para você"}
        </Text>
      </View>

      <ChevronRight size={20} color={colors.text.secondary} strokeWidth={2} />
    </Pressable>
  );
}
