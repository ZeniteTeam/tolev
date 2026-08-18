import { ActivityIndicator, Pressable, Text, View } from "react-native";
import { colors, shadows } from "../../../theme";
import type { CategoriaResponse } from "../../../types/transacao";
import { CATEGORIA_COR_PADRAO, categoriaIcon } from "../constants/transacoes";

type Props = {
  categorias: CategoriaResponse[];
  /** Categoria escolhida, identificada pelo par id + origem. */
  selectedId: number | null;
  selectedOrigem: string | null;
  onSelect: (categoria: CategoriaResponse) => void;
  loading?: boolean;
  error?: boolean;
};

/**
 * Grade de categorias, três por linha. A cor vem do banco, então cada categoria
 * carrega a própria identidade visual em vez de tudo virar verde.
 */
export default function CategoriaGrid({
  categorias,
  selectedId,
  selectedOrigem,
  onSelect,
  loading,
  error,
}: Props) {
  if (loading) {
    return (
      <View className="py-10 items-center">
        <ActivityIndicator color={colors.primary[700]} />
      </View>
    );
  }

  if (error) {
    return (
      <View className="bg-surface rounded-lg px-5 py-6" style={shadows.card}>
        <Text className="text-sm text-muted font-regular text-center leading-[19px]">
          Não foi possível carregar as categorias. Verifique sua conexão e volte a esta etapa.
        </Text>
      </View>
    );
  }

  if (categorias.length === 0) {
    return (
      <View className="bg-surface rounded-lg px-5 py-6" style={shadows.card}>
        <Text className="text-sm text-muted font-regular text-center leading-[19px]">
          Nenhuma categoria disponível para esse tipo de transação.
        </Text>
      </View>
    );
  }

  return (
    <View className="flex-row flex-wrap gap-2.5">
      {categorias.map((c) => {
        const active = c.id === selectedId && c.origem === selectedOrigem;
        const Icon = categoriaIcon(c.nome);
        const cor = c.cor ?? CATEGORIA_COR_PADRAO;

        return (
          <Pressable
            key={`${c.origem}-${c.id}`}
            onPress={() => onSelect(c)}
            className="rounded-lg items-center justify-center py-3.5 px-1"
            style={[
              { width: "31.5%", backgroundColor: colors.surface },
              active ? { borderWidth: 2, borderColor: cor } : shadows.card,
            ]}
          >
            <View
              className="w-9 h-9 rounded-full items-center justify-center"
              style={{ backgroundColor: active ? cor : `${cor}1F` }}
            >
              <Icon size={18} color={active ? colors.surface : cor} strokeWidth={2} />
            </View>
            <Text
              className="text-[11px] mt-1.5 font-semibold text-center"
              numberOfLines={1}
              style={{ color: active ? cor : colors.text.secondary }}
            >
              {c.nome}
            </Text>
          </Pressable>
        );
      })}
    </View>
  );
}
