import { zodResolver } from "@hookform/resolvers/zod";
import { useNavigation } from "@react-navigation/native";
import {
  ArrowDownLeft,
  ArrowUpRight,
  CalendarDays,
  Hash,
  Layers,
  Store,
  Wallet,
} from "lucide-react-native";
import { useState } from "react";
import { Controller, useForm } from "react-hook-form";
import { Pressable, Text, View } from "react-native";
import { Field, MaskedField, StepScaffold } from "../../../components";
import { useAuthStore } from "../../../store/authStore";
import { colors, shadows } from "../../../theme";
import type { CategoriaResponse, TransacaoRequest } from "../../../types/transacao";
import { getApiErrorMessage } from "../../../util/apiError";
import { brDateToIso } from "../../../util/date";
import { digitsToDecimal } from "../../../util/masks";
import CategoriaGrid from "../components/CategoriaGrid";
import ContaPicker from "../components/ContaPicker";
import MetodoPicker from "../components/MetodoPicker";
import TipoCard from "../components/TipoCard";
import { STEPS, TOTAL_STEPS } from "../constants/novaTransacaoSteps";
import { useCategorias } from "../hooks/useCategorias";
import { useContas } from "../hooks/useContas";
import { useCreateTransacao } from "../hooks/useCreateTransacao";
import {
  novaTransacaoSchema,
  STEP_FIELDS,
  type NovaTransacaoValues,
} from "../schema/novaTransacaoSchema";

/** Hoje em DD/MM/AAAA — a data já vem preenchida porque o gasto é recente. */
function hojeBr(): string {
  const now = new Date();
  const dd = String(now.getDate()).padStart(2, "0");
  const mm = String(now.getMonth() + 1).padStart(2, "0");
  return `${dd}/${mm}/${now.getFullYear()}`;
}

/**
 * Fluxo em etapas de "adicionar transação", no mesmo formato de "nova dívida":
 * uma ideia por tela, barra de progresso e uma folha de ajuda por etapa. São
 * quatro passos (contra cinco da dívida) porque isso aqui a pessoa lança
 * várias vezes por semana — a única coisa que ela precisa saber de cabeça, o
 * valor, vem logo na primeira tela.
 */
export default function AdicionarTransacaoScreen() {
  const navigation = useNavigation<any>();
  const userId = useAuthStore((s) => s.userId);
  const createTransacao = useCreateTransacao();
  const [step, setStep] = useState(1);

  const {
    control,
    handleSubmit,
    trigger,
    setValue,
    setError,
    watch,
    formState: { errors },
  } = useForm<NovaTransacaoValues>({
    resolver: zodResolver(novaTransacaoSchema),
    defaultValues: {
      tipo: "DESPESA",
      valor: "",
      categoriaId: 0,
      categoriaOrigem: "SISTEMA",
      estabelecimento: "",
      descricao: "",
      data: hojeBr(),
      metodoPagamento: "PIX",
      contaId: null,
      parcelado: false,
      totalParcelas: "",
      numeroParcela: "",
    },
    mode: "onTouched",
  });

  const values = watch();

  // O catálogo é o mesmo, mas só as categorias do lado escolhido fazem sentido:
  // o backend recusa categoria de despesa numa receita.
  const {
    categorias,
    isLoading: categoriasLoading,
    isError: categoriasError,
  } = useCategorias(values.tipo);
  const { contas, isLoading: contasLoading } = useContas();

  /** Trocar receita/despesa invalida a categoria já escolhida. */
  function selecionarTipo(tipo: NovaTransacaoValues["tipo"]) {
    setValue("tipo", tipo, { shouldValidate: true });
    setValue("categoriaId", 0);
  }

  function selecionarCategoria(categoria: CategoriaResponse) {
    setValue("categoriaId", categoria.id, { shouldValidate: true });
    setValue("categoriaOrigem", categoria.origem, { shouldValidate: true });
  }

  function onSubmit(v: NovaTransacaoValues) {
    if (userId == null) {
      setError("root", { message: "Sessão expirada. Faça login novamente para continuar." });
      return;
    }

    const estabelecimento = v.estabelecimento.trim();
    const descricao = v.descricao.trim();
    const daSistema = v.categoriaOrigem === "SISTEMA";

    const payload: TransacaoRequest = {
      idUsuario: userId,
      idContaBancaria: v.contaId,
      nomeVendedor: estabelecimento.length > 0 ? estabelecimento : null,
      valor: digitsToDecimal(v.valor),
      dataTransacao: brDateToIso(v.data),
      tipo: v.tipo,
      // Sem descrição própria, o nome do lugar já conta a história do gasto.
      descricao: descricao.length > 0 ? descricao : estabelecimento || null,
      parcelado: v.parcelado,
      totalParcelas: v.parcelado ? Number(v.totalParcelas) : null,
      numeroParcela: v.parcelado ? Number(v.numeroParcela) : null,
      metodoPagamento: v.metodoPagamento,
      idCategoriaGastoSistema: daSistema ? v.categoriaId : null,
      idCategoriaGastoUsuario: daSistema ? null : v.categoriaId,
    };

    createTransacao.mutate(payload, {
      onSuccess: () => navigation.goBack(),
      onError: (err) =>
        setError("root", {
          message: getApiErrorMessage(err, "Não foi possível adicionar a transação."),
        }),
    });
  }

  async function goNext() {
    const ok = await trigger(STEP_FIELDS[step - 1]);
    if (!ok) return;
    if (step < TOTAL_STEPS) setStep(step + 1);
    else handleSubmit(onSubmit)();
  }

  function goBack() {
    if (step === 1) navigation.goBack();
    else setStep(step - 1);
  }

  const isLast = step === TOTAL_STEPS;
  const meta = STEPS[step - 1];

  // Só os campos da etapa atual podem travar o botão.
  const canContinue =
    step === 1
      ? digitsToDecimal(values.valor) > 0
      : step === 2
      ? values.categoriaId > 0
      : step === 3
      ? values.data.length === 10
      : !values.parcelado ||
        (Number(values.totalParcelas) >= 2 && Number(values.numeroParcela) >= 1);

  const stepError =
    STEP_FIELDS[step - 1].map((f) => errors[f]?.message).find(Boolean) ?? errors.root?.message;

  return (
    <StepScaffold
      step={step}
      total={TOTAL_STEPS}
      onBack={goBack}
      title={meta.title}
      subtitle={meta.subtitle}
      help={meta.help}
      onContinue={goNext}
      continueDisabled={!canContinue || (isLast && createTransacao.isPending)}
      continueLabel={
        isLast
          ? createTransacao.isPending
            ? "Adicionando..."
            : "Adicionar transação"
          : "Continuar"
      }
      error={stepError}
    >
      {step === 1 && (
        <View className="gap-6">
          <View className="flex-row gap-3.5">
            <TipoCard
              icon={ArrowUpRight}
              title="Despesa"
              subtitle="Dinheiro que saiu"
              accent={colors.coral[500]}
              selected={values.tipo === "DESPESA"}
              onPress={() => selecionarTipo("DESPESA")}
            />
            <TipoCard
              icon={ArrowDownLeft}
              title="Receita"
              subtitle="Dinheiro que entrou"
              accent={colors.primary[500]}
              selected={values.tipo === "RECEITA"}
              onPress={() => selecionarTipo("RECEITA")}
            />
          </View>

          <Controller
            control={control}
            name="valor"
            render={({ field: { value, onChange } }) => (
              <MaskedField
                label={values.tipo === "RECEITA" ? "Quanto entrou" : "Quanto saiu"}
                type="currency"
                icon={Wallet}
                placeholder="R$ 0,00"
                value={value}
                onChange={onChange}
                autoFocus
              />
            )}
          />
        </View>
      )}

      {step === 2 && (
        <CategoriaGrid
          categorias={categorias}
          selectedId={values.categoriaId > 0 ? values.categoriaId : null}
          selectedOrigem={values.categoriaId > 0 ? values.categoriaOrigem : null}
          onSelect={selecionarCategoria}
          loading={categoriasLoading}
          error={categoriasError}
        />
      )}

      {step === 3 && (
        <View className="gap-3.5">
          <Controller
            control={control}
            name="estabelecimento"
            render={({ field: { value, onChange } }) => (
              <Group label="Onde foi? (opcional)">
                <Field
                  icon={Store}
                  placeholder="Ex.: Mercado do bairro"
                  value={value}
                  onChangeText={onChange}
                  autoCapitalize="sentences"
                />
              </Group>
            )}
          />
          <Controller
            control={control}
            name="descricao"
            render={({ field: { value, onChange } }) => (
              <Group label="Uma nota pra você lembrar (opcional)">
                <Field
                  placeholder="Ex.: compra do mês"
                  value={value}
                  onChangeText={onChange}
                  autoCapitalize="sentences"
                />
              </Group>
            )}
          />
          <Controller
            control={control}
            name="data"
            render={({ field: { value, onChange } }) => (
              <MaskedField
                label="Quando foi"
                type="date"
                icon={CalendarDays}
                placeholder="DD/MM/AAAA"
                hint="O dia do gasto, não o do vencimento da fatura"
                value={value}
                onChange={onChange}
              />
            )}
          />
          <Group label="Como você pagou?">
            <MetodoPicker
              value={values.metodoPagamento}
              onChange={(m) => setValue("metodoPagamento", m, { shouldValidate: true })}
            />
          </Group>
        </View>
      )}

      {step === 4 && (
        <View className="gap-6">
          <Group label="Saiu de onde?">
            <ContaPicker
              contas={contas}
              value={values.contaId}
              onChange={(id) => setValue("contaId", id, { shouldValidate: true })}
              loading={contasLoading}
            />
          </Group>

          <Group label="Foi parcelado?">
            <View className="gap-3.5">
              <View className="flex-row gap-3.5">
                <ParceladoToggle
                  label="À vista"
                  selected={!values.parcelado}
                  onPress={() => setValue("parcelado", false, { shouldValidate: true })}
                />
                <ParceladoToggle
                  label="Parcelado"
                  selected={values.parcelado}
                  onPress={() => setValue("parcelado", true, { shouldValidate: true })}
                />
              </View>

              {values.parcelado && (
                <View className="gap-3.5">
                  <Controller
                    control={control}
                    name="totalParcelas"
                    render={({ field: { value, onChange } }) => (
                      <MaskedField
                        label="Em quantas vezes"
                        type="integer"
                        icon={Layers}
                        placeholder="Ex.: 10"
                        value={value}
                        onChange={onChange}
                        autoFocus
                      />
                    )}
                  />
                  <Controller
                    control={control}
                    name="numeroParcela"
                    render={({ field: { value, onChange } }) => (
                      <MaskedField
                        label="Qual parcela é essa"
                        type="integer"
                        icon={Hash}
                        placeholder="Ex.: 1"
                        hint="Acabou de comprar? Então é a parcela 1"
                        value={value}
                        onChange={onChange}
                      />
                    )}
                  />
                </View>
              )}
            </View>
          </Group>
        </View>
      )}
    </StepScaffold>
  );
}

function ParceladoToggle({
  label,
  selected,
  onPress,
}: {
  label: string;
  selected: boolean;
  onPress: () => void;
}) {
  return (
    <Pressable
      onPress={onPress}
      className="flex-1 rounded-lg items-center justify-center py-3.5"
      style={[
        { backgroundColor: selected ? colors.primary[100] : colors.surface },
        selected ? { borderWidth: 2, borderColor: colors.primary[700] } : shadows.card,
      ]}
    >
      <Text
        className="text-sm font-semibold"
        style={{ color: selected ? colors.primary[700] : colors.text.secondary }}
      >
        {label}
      </Text>
    </Pressable>
  );
}

function Group({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <View>
      <Text className="text-sm font-semibold text-ink mb-2 pl-1">{label}</Text>
      {children}
    </View>
  );
}
