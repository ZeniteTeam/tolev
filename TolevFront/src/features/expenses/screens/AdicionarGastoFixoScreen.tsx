import { useNavigation, useRoute } from "@react-navigation/native";
import { Trash2 } from "lucide-react-native";
import { useState } from "react";
import { Alert, Pressable, Text, TextInput, View } from "react-native";
import { Button, Screen, Stagger } from "../../../components";
import { colors, shadows } from "../../../theme";
import type { GastoFixoResponse } from "../../../types/gastoFixo";
import { getApiErrorMessage } from "../../../util/apiError";
import { useAuthStore } from "../../../store/authStore";
import { digitsToDecimal, decimalToDigits, maskCurrency, onlyDigits } from "../../../util/masks";
import {
  useCreateGastoFixo,
  useDeleteGastoFixo,
  useUpdateGastoFixo,
} from "../hooks/useGastosFixos";
import { iconePara } from "../utils/gasto-fixo-view";

/** Sugestões de um toque. A maioria dos fixos de qualquer pessoa está aqui. */
const SUGESTOES = ["Aluguel", "Internet", "Energia", "Água", "Celular", "Academia", "Streaming"];

/**
 * Cadastro e edição no mesmo lugar: são o mesmo formulário, e ter duas telas
 * quase idênticas só garante que uma delas fique para trás.
 */
export default function AdicionarGastoFixoScreen() {
  const navigation = useNavigation<any>();
  const route = useRoute<any>();
  const existente: GastoFixoResponse | undefined = route.params?.gasto;
  const idUsuario = useAuthStore((s) => s.userId);

  const [nome, setNome] = useState(existente?.nome ?? "");
  const [valorDigits, setValorDigits] = useState(decimalToDigits(existente?.valor));
  const [dia, setDia] = useState(existente?.diaVencimento?.toString() ?? "");

  const criar = useCreateGastoFixo();
  const atualizar = useUpdateGastoFixo();
  const remover = useDeleteGastoFixo();
  const salvando = criar.isPending || atualizar.isPending;

  const valor = digitsToDecimal(valorDigits);
  const diaNumero = dia === "" ? null : Number(dia);
  const diaValido = diaNumero == null || (diaNumero >= 1 && diaNumero <= 31);
  const podeSalvar = nome.trim().length > 0 && valor > 0 && diaValido && !salvando;
  const Icon = iconePara(nome);

  function salvar() {
    if (!podeSalvar || idUsuario == null) return;

    const payload = { idUsuario, nome: nome.trim(), valor, diaVencimento: diaNumero };
    const onError = (err: unknown) =>
      Alert.alert("Erro", getApiErrorMessage(err, "Não foi possível salvar o gasto fixo."));

    if (existente) {
      atualizar.mutate(
        { id: existente.id, ...payload },
        { onSuccess: () => navigation.goBack(), onError },
      );
    } else {
      criar.mutate(payload, { onSuccess: () => navigation.goBack(), onError });
    }
  }

  function confirmarRemocao() {
    if (!existente) return;
    Alert.alert("Remover gasto fixo", `"${existente.nome}" sai da sua lista mensal.`, [
      { text: "Cancelar", style: "cancel" },
      {
        text: "Remover",
        style: "destructive",
        onPress: () =>
          remover.mutate(existente.id, {
            onSuccess: () => navigation.goBack(),
            onError: (err) =>
              Alert.alert("Erro", getApiErrorMessage(err, "Não foi possível remover.")),
          }),
      },
    ]);
  }

  return (
    <Screen bottomPad={40}>
      <Stagger>
        <View className="pt-1 mb-6">
          <Text className="font-bold text-[24px] leading-7 text-ink">
            {existente ? "Editar gasto fixo" : "Novo gasto fixo"}
          </Text>
          <Text className="text-[14px] text-muted mt-1 font-regular">
            {existente
              ? "Salvar também confirma que o valor está certo."
              : "Três campos e pronto."}
          </Text>
        </View>

        <Grupo label="O que é">
          <View className="h-[52px] rounded-[16px] bg-primary-50 flex-row items-center px-4 gap-3">
            <Icon size={20} color={colors.primary[700]} strokeWidth={2} />
            <TextInput
              value={nome}
              onChangeText={setNome}
              placeholder="Aluguel, internet, academia…"
              placeholderTextColor={colors.text.secondary}
              selectionColor={colors.primary[500]}
              className="flex-1 font-regular text-base text-ink py-0"
              autoFocus={!existente}
            />
          </View>

          {/* Some assim que a pessoa começa a digitar: sugestão depois disso
              é obstáculo, não atalho. */}
          {nome.trim() === "" && (
            <View className="flex-row flex-wrap gap-2 mt-3">
              {SUGESTOES.map((s) => (
                <Pressable
                  key={s}
                  onPress={() => setNome(s)}
                  className="px-3.5 h-9 rounded-pill bg-surface items-center justify-center active:opacity-80"
                  style={shadows.card}
                >
                  <Text className="text-[13px] font-semibold text-primary-700">{s}</Text>
                </Pressable>
              ))}
            </View>
          )}
        </Grupo>

        <Grupo label="Quanto por mês">
          <View className="h-[52px] rounded-[16px] bg-primary-50 flex-row items-center px-4">
            <TextInput
              value={maskCurrency(valorDigits)}
              onChangeText={(t) => setValorDigits(onlyDigits(t).slice(0, 9))}
              keyboardType="number-pad"
              placeholder="R$ 0,00"
              placeholderTextColor={colors.text.secondary}
              selectionColor={colors.primary[500]}
              className="flex-1 font-bold text-[20px] text-ink py-0"
            />
          </View>
        </Grupo>

        <Grupo label="Dia do vencimento" opcional>
          <View className="h-[52px] rounded-[16px] bg-primary-50 flex-row items-center px-4 gap-2">
            <TextInput
              value={dia}
              onChangeText={(t) => setDia(onlyDigits(t).slice(0, 2))}
              keyboardType="number-pad"
              placeholder="10"
              placeholderTextColor={colors.text.secondary}
              selectionColor={colors.primary[500]}
              className="w-12 font-bold text-[17px] text-ink py-0"
            />
            <Text className="text-[14px] text-muted font-regular">de cada mês</Text>
          </View>
          {!diaValido && (
            <Text className="text-[12px] text-coral-500 mt-2 font-regular">
              O dia vai de 1 a 31.
            </Text>
          )}
        </Grupo>

        <View className="mt-2">
          <Button onPress={salvar} style={podeSalvar ? undefined : { opacity: 0.45 }}>
            {salvando ? "Salvando…" : existente ? "Salvar" : "Adicionar"}
          </Button>
        </View>

        {existente && (
          <Pressable
            onPress={confirmarRemocao}
            className="h-12 mt-3 flex-row items-center justify-center gap-2 active:opacity-70"
          >
            <Trash2 size={17} color={colors.coral[500]} strokeWidth={2} />
            <Text className="text-coral-500 font-semibold text-[15px]">Remover</Text>
          </Pressable>
        )}
      </Stagger>
    </Screen>
  );
}

function Grupo({
  label,
  opcional,
  children,
}: {
  label: string;
  opcional?: boolean;
  children: React.ReactNode;
}) {
  return (
    <View className="mb-5">
      <View className="flex-row items-center gap-2 mb-2.5">
        <Text className="text-[13px] font-bold text-ink">{label}</Text>
        {opcional && <Text className="text-[12px] text-muted font-regular">opcional</Text>}
      </View>
      {children}
    </View>
  );
}
