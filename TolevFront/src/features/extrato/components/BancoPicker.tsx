import { Check } from "lucide-react-native";
import { Pressable, Text, View } from "react-native";
import type { BancoResponse } from "../../../types/extrato";
import { colors } from "../../../theme";
import { bancoVisual } from "../constants/bancos";

type Props = {
  bancos: BancoResponse[];
  selecionado: number | null;
  onChange: (idBanco: number) => void;
  carregando?: boolean;
};

/**
 * Lista vertical, não a fileira horizontal do filtro de análise: aqui a escolha
 * é obrigatória e define de qual banco o extrato será lido, então nenhuma opção
 * pode ficar escondida fora da tela.
 */
export default function BancoPicker({
  bancos,
  selecionado,
  onChange,
  carregando = false,
}: Props) {
  if (carregando) {
    return (
      <View className="gap-2.5">
        {[0, 1, 2, 3].map((i) => (
          <View key={i} className="h-[62px] rounded-[14px] bg-[#F1F5F3]" />
        ))}
      </View>
    );
  }

  if (bancos.length === 0) {
    return (
      <Text className="text-[14px] text-muted font-regular leading-5">
        Não conseguimos carregar a lista de bancos agora. Verifique sua conexão e
        tente de novo.
      </Text>
    );
  }

  return (
    <View className="gap-2.5">
      {bancos.map((banco) => {
        const visual = bancoVisual(banco.codigoBanco);
        const ativo = selecionado === banco.id;
        return (
          <Pressable
            key={banco.id}
            onPress={() => onChange(banco.id)}
            className="flex-row items-center gap-3.5 px-4 py-3 rounded-[14px] bg-surface active:opacity-90"
            style={{
              borderWidth: 1.5,
              borderColor: ativo ? colors.primary[500] : colors.primary[50],
            }}
          >
            <View
              className="w-10 h-10 rounded-full items-center justify-center"
              style={{ backgroundColor: visual.cor }}
            >
              <Text
                className="font-bold text-[14px]"
                style={{ color: visual.corTexto }}
              >
                {visual.sigla}
              </Text>
            </View>

            <Text className="flex-1 text-[15px] font-semibold text-ink">
              {banco.titulo}
            </Text>

            {ativo ? (
              <View className="w-6 h-6 rounded-full bg-primary-500 items-center justify-center">
                <Check size={14} color="#fff" strokeWidth={3} />
              </View>
            ) : (
              <View
                className="w-6 h-6 rounded-full"
                style={{ borderWidth: 1.5, borderColor: colors.primary[100] }}
              />
            )}
          </Pressable>
        );
      })}
    </View>
  );
}
