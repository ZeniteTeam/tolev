import { Pressable, ScrollView, Text, View } from "react-native";
import { colors } from "../../../theme";
import type { BancoUsuarioResponse } from "../../../types/extrato";
import { bancoVisual } from "../constants/bancos";

type Props = {
  bancos: BancoUsuarioResponse[];
  /** `null` = todos os gastos, inclusive os lançados à mão. */
  ativo: number | null;
  onChange: (idBanco: number | null) => void;
};

/**
 * Filtra a análise pelos bancos que o usuário realmente tem.
 *
 * Só existe depois do primeiro extrato importado: a procedência de um lançamento
 * digitado à mão é desconhecida, então antes disso um filtro por banco não teria
 * como separar nada — e uma fileira de bancos que o usuário não usa é enfeite.
 *
 * Com um banco só, também não aparece: escolher entre "todos" e "o único" não é
 * escolha.
 */
export default function BancoUsuarioFilter({ bancos, ativo, onChange }: Props) {
  if (bancos.length < 2) {
    return null;
  }

  return (
    <View className="mb-1">
      <Text className="text-[13px] text-ink font-semibold mb-1 pl-1">
        Filtrar por banco
      </Text>

      <ScrollView
        horizontal
        showsHorizontalScrollIndicator={false}
        contentContainerClassName="px-1 py-2 gap-3.5"
      >
        <Item
          sigla="✱"
          cor={colors.primary[700]}
          corTexto="#fff"
          nome="Todos"
          ativo={ativo == null}
          onPress={() => onChange(null)}
        />

        {bancos.map((banco) => {
          const visual = bancoVisual(banco.codigoBanco);
          return (
            <Item
              key={banco.idBanco}
              sigla={visual.sigla}
              cor={visual.cor}
              corTexto={visual.corTexto}
              nome={banco.nome}
              ativo={ativo === banco.idBanco}
              onPress={() => onChange(banco.idBanco)}
            />
          );
        })}
      </ScrollView>
    </View>
  );
}

function Item({
  sigla,
  cor,
  corTexto,
  nome,
  ativo,
  onPress,
}: {
  sigla: string;
  cor: string;
  corTexto: string;
  nome: string;
  ativo: boolean;
  onPress: () => void;
}) {
  return (
    <Pressable onPress={onPress} className="items-center gap-1.5 w-16">
      <View
        className="w-11 h-11 rounded-full items-center justify-center"
        style={[
          { backgroundColor: cor },
          ativo && { borderWidth: 2, borderColor: colors.coral[500] },
        ]}
      >
        <Text style={{ color: corTexto }} className="font-bold text-[14px]">
          {sigla}
        </Text>
      </View>
      <Text
        className={`text-xs text-center ${ativo ? "text-primary-700 font-bold" : "text-muted font-medium"}`}
        numberOfLines={1}
      >
        {nome}
      </Text>
    </Pressable>
  );
}
