import { useNavigation } from "@react-navigation/native";
import { Flame, type LucideIcon } from "lucide-react-native";
import { useState } from "react";
import { Alert, Pressable, Text, View } from "react-native";
import { BankFilter, Button, Field, PageTitle, Screen } from "../../../components";
import { bankName, type BankId } from "../../../components/BankFilter";
import { colors, shadows } from "../../../theme";
import { useAuthStore } from "../../../store/authStore";
import { formatCurrencyBRL, parseCurrencyToNumber } from "../../../util/currency";
import { getApiErrorMessage } from "../../../util/apiError";
import { TIPO_ICON } from "../constants/dividas";
import { useCreateDivida } from "../hooks/useCreateDivida";
import type { TipoDivida } from "../../../types/divida";

const TIPOS: { id: TipoDivida; name: string; icon: LucideIcon }[] = [
  { id: "CARTAO", name: "Cartão", icon: TIPO_ICON.CARTAO },
  { id: "EMPRESTIMO", name: "Empréstimo", icon: TIPO_ICON.EMPRESTIMO },
  { id: "FINANCIAMENTO", name: "Financiamento", icon: TIPO_ICON.FINANCIAMENTO },
  { id: "CHEQUE_ESPECIAL", name: "Cheque esp.", icon: TIPO_ICON.CHEQUE_ESPECIAL },
  { id: "CARNE", name: "Carnê", icon: TIPO_ICON.CARNE },
  { id: "OUTROS", name: "Outros", icon: TIPO_ICON.OUTROS },
];

export default function AdicionarDividaScreen() {
  const navigation = useNavigation<any>();
  const userId = useAuthStore((s) => s.userId);
  const createDivida = useCreateDivida();

  const [nome, setNome] = useState("");
  const [saldo, setSaldo] = useState("");
  const [juros, setJuros] = useState("");
  const [parcelas, setParcelas] = useState("");
  const [tipo, setTipo] = useState<TipoDivida>("CARTAO");
  const [banco, setBanco] = useState<BankId>("nubank");
  const [emocional, setEmocional] = useState(3);

  const saldoNum = parseCurrencyToNumber(saldo);
  const jurosNum = parseCurrencyToNumber(juros);
  const parcelasNum = Math.round(parseCurrencyToNumber(parcelas));
  // Parcela mínima é derivada: saldo ÷ nº de parcelas (o back é a fonte da verdade).
  const parcelaMinimaCalc = saldoNum > 0 && parcelasNum > 0 ? saldoNum / parcelasNum : 0;

  function handleSubmit() {
    if (createDivida.isPending) return;

    if (!nome.trim()) {
      Alert.alert("Nome obrigatório", "Dê um nome para a dívida.");
      return;
    }
    if (saldoNum <= 0) {
      Alert.alert("Saldo inválido", "Informe o saldo devedor da dívida.");
      return;
    }
    if (parcelasNum <= 0) {
      Alert.alert("Parcelas inválidas", "Informe em quantas parcelas a dívida será paga.");
      return;
    }
    if (userId == null) {
      Alert.alert("Sessão expirada", "Faça login novamente para continuar.");
      return;
    }

    createDivida.mutate(
      {
        idUsuario: userId,
        nome: nome.trim(),
        banco: bankName(banco),
        tipo,
        saldo: saldoNum,
        juros: jurosNum,
        parcelaMinima: parcelaMinimaCalc,
        pesoEmocional: emocional,
        quantidadeParcelas: parcelasNum,
      },
      {
        onSuccess: () => navigation.goBack(),
        onError: (err) =>
          Alert.alert("Erro", getApiErrorMessage(err, "Não foi possível adicionar a dívida.")),
      },
    );
  }

  return (
    <Screen bottomPad={40}>
      <PageTitle
        title="Adicionar dívida"
        sub="Registre uma dívida para acompanhar e planejar a quitação"
      />

      <FieldGroup label="Nome da dívida">
        <Field placeholder="Ex.: Cartão Nubank" value={nome} onChangeText={setNome} />
      </FieldGroup>

      <FieldGroup label="Tipo">
        <View className="flex-row flex-wrap gap-2.5">
          {TIPOS.map((t) => {
            const active = tipo === t.id;
            const Icon = t.icon;
            return (
              <Pressable
                key={t.id}
                onPress={() => setTipo(t.id)}
                className="rounded-[14px] items-center justify-center py-3.5"
                style={[
                  { width: "31.5%", backgroundColor: active ? colors.primary[100] : colors.surface },
                  active
                    ? { borderWidth: 2, borderColor: colors.primary[700] }
                    : shadows.card,
                ]}
              >
                <Icon
                  size={22}
                  color={active ? colors.primary[700] : colors.text.secondary}
                  strokeWidth={2}
                />
                <Text
                  className="text-[11px] mt-1.5 font-semibold"
                  style={{ color: active ? colors.primary[700] : colors.text.secondary }}
                >
                  {t.name}
                </Text>
              </Pressable>
            );
          })}
        </View>
      </FieldGroup>

      <FieldGroup label="Banco">
        <BankFilter active={banco} onChange={setBanco} />
      </FieldGroup>

      <FieldGroup label="Saldo devedor">
        <Field placeholder="R$ 0,00" value={saldo} onChangeText={setSaldo} keyboardType="numeric" />
      </FieldGroup>

      <FieldGroup label="Juros a.m.">
        <Field placeholder="0,0%" value={juros} onChangeText={setJuros} keyboardType="numeric" />
      </FieldGroup>

      <FieldGroup label="Quantidade de parcelas">
        <Field placeholder="Ex.: 12" value={parcelas} onChangeText={setParcelas} keyboardType="numeric" />
      </FieldGroup>

      <FieldGroup label="Valor da parcela">
        <View
          className="bg-surface rounded-[16px] px-5 py-4 flex-row items-center justify-between"
          style={shadows.card}
        >
          <Text
            className="text-base font-semibold"
            style={{ color: parcelaMinimaCalc > 0 ? colors.text.primary : colors.text.secondary }}
          >
            {parcelaMinimaCalc > 0 ? formatCurrencyBRL(parcelaMinimaCalc) : "R$ 0,00"}
          </Text>
          <Text className="text-[11px] text-muted font-regular">calculado · saldo ÷ parcelas</Text>
        </View>
      </FieldGroup>

      <FieldGroup label="Peso emocional dessa dívida">
        <View
          className="bg-surface rounded-[16px] px-5 py-4 flex-row items-center justify-between"
          style={shadows.card}
        >
          <View className="flex-row gap-1.5">
            {[1, 2, 3, 4, 5].map((i) => (
              <Pressable key={i} onPress={() => setEmocional(i)}>
                <Flame
                  size={26}
                  color={colors.coral[500]}
                  fill={i <= emocional ? colors.coral[500] : "none"}
                  strokeWidth={2}
                />
              </Pressable>
            ))}
          </View>
          <Text className="text-[11px] text-muted text-right max-w-[130px] leading-[15px] font-regular">
            Usado no método Tsunami
          </Text>
        </View>
      </FieldGroup>

      <Button variant="primary" onPress={handleSubmit}>
        {createDivida.isPending ? "Adicionando..." : "Adicionar dívida"}
      </Button>
      <Button variant="ghost" onPress={() => navigation.goBack()} style={{ marginTop: 6 }}>
        Cancelar
      </Button>
    </Screen>
  );
}

function FieldGroup({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <View className="mb-[18px]">
      <Text className="text-[13px] font-semibold text-ink mb-2 pl-1">{label}</Text>
      {children}
    </View>
  );
}
