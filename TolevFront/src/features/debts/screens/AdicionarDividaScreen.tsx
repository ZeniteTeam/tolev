import { zodResolver } from "@hookform/resolvers/zod";
import { useNavigation } from "@react-navigation/native";
import {
  CalendarCheck,
  CalendarPlus,
  Flame,
  Hash,
  Percent,
  TrendingUp,
  Wallet,
  type LucideIcon,
} from "lucide-react-native";
import { useState } from "react";
import { Controller, useForm } from "react-hook-form";
import { Pressable, Text, View } from "react-native";
import { BankFilter, Field, MaskedField, StepScaffold } from "../../../components";
import { bankName, type BankId } from "../../../components/BankFilter";
import { useAuthStore } from "../../../store/authStore";
import { colors, shadows } from "../../../theme";
import type { DividaRequest, TipoDivida } from "../../../types/divida";
import { getApiErrorMessage } from "../../../util/apiError";
import { brDateToIso } from "../../../util/date";
import { digitsToDecimal } from "../../../util/masks";
import ChoiceCard from "../components/ChoiceCard";
import { TIPO_ICON } from "../constants/dividas";
import { STEPS, TOTAL_STEPS } from "../constants/novaDividaSteps";
import { useCreateDivida } from "../hooks/useCreateDivida";
import {
  novaDividaSchema,
  STEP_FIELDS,
  type NovaDividaValues,
} from "../schema/novaDividaSchema";

const TIPOS: { id: TipoDivida; name: string; icon: LucideIcon }[] = [
  { id: "CARTAO", name: "Cartão", icon: TIPO_ICON.CARTAO },
  { id: "EMPRESTIMO", name: "Empréstimo", icon: TIPO_ICON.EMPRESTIMO },
  { id: "FINANCIAMENTO", name: "Financiamento", icon: TIPO_ICON.FINANCIAMENTO },
  { id: "CHEQUE_ESPECIAL", name: "Cheque esp.", icon: TIPO_ICON.CHEQUE_ESPECIAL },
  { id: "CARNE", name: "Carnê", icon: TIPO_ICON.CARNE },
  { id: "OUTROS", name: "Outros", icon: TIPO_ICON.OUTROS },
];

/**
 * Stepped "nova dívida" flow: one idea per screen, a progress bar that grows as
 * you go and a "Onde encontro isso?" sheet on every step, because most of these
 * numbers live in a contract the person has to go dig up.
 */
export default function AdicionarDividaScreen() {
  const navigation = useNavigation<any>();
  const userId = useAuthStore((s) => s.userId);
  const createDivida = useCreateDivida();
  const [step, setStep] = useState(1);

  const {
    control,
    handleSubmit,
    trigger,
    setValue,
    setError,
    watch,
    formState: { errors },
  } = useForm<NovaDividaValues>({
    resolver: zodResolver(novaDividaSchema),
    defaultValues: {
      nome: "",
      tipo: "CARTAO",
      banco: "nubank",
      pesoEmocional: 3,
      valor: "",
      parcelas: "",
      dataLiberacao: "",
      dataPrimeiroVencimento: "",
      multaAtraso: "",
      jurosMensal: "",
      jurosMora: "",
      sistemaAmortizacao: "PRICE",
      regimeJuros: "COMPOSTO",
    },
    mode: "onTouched",
  });

  const values = watch();

  function onSubmit(v: NovaDividaValues) {
    if (userId == null) {
      setError("root", { message: "Sessão expirada. Faça login novamente para continuar." });
      return;
    }

    const payload: DividaRequest = {
      idUsuario: userId,
      nome: v.nome.trim(),
      banco: bankName(v.banco as BankId),
      tipo: v.tipo,
      saldo: digitsToDecimal(v.valor),
      juros: digitsToDecimal(v.jurosMensal),
      multaAtraso: digitsToDecimal(v.multaAtraso),
      jurosMora: digitsToDecimal(v.jurosMora),
      pesoEmocional: v.pesoEmocional,
      quantidadeParcelas: Number(v.parcelas),
      dataLiberacao: brDateToIso(v.dataLiberacao),
      dataPrimeiroVencimento: brDateToIso(v.dataPrimeiroVencimento),
      sistemaAmortizacao: v.sistemaAmortizacao,
      regimeJuros: v.regimeJuros,
    };

    createDivida.mutate(payload, {
      onSuccess: () => navigation.goBack(),
      onError: (err) =>
        setError("root", {
          message: getApiErrorMessage(err, "Não foi possível adicionar a dívida."),
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

  // Only the fields of the current step can block the button.
  const canContinue =
    step === 1
      ? !!values.nome?.trim() && values.banco !== "all"
      : step === 2
      ? digitsToDecimal(values.valor) > 0 &&
        Number(values.parcelas) >= 1 &&
        values.dataPrimeiroVencimento.length === 10
      : true;

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
      continueDisabled={!canContinue || (isLast && createDivida.isPending)}
      continueLabel={
        isLast ? (createDivida.isPending ? "Adicionando..." : "Adicionar dívida") : "Continuar"
      }
      error={stepError}
    >
      {step === 1 && (
        <View className="gap-6">
          <Controller
            control={control}
            name="nome"
            render={({ field: { value, onChange } }) => (
              <Group label="Nome da dívida">
                <Field
                  placeholder="Ex.: Cartão Nubank"
                  value={value}
                  onChangeText={onChange}
                  autoCapitalize="sentences"
                />
              </Group>
            )}
          />

          <Group label="Que tipo de dívida é essa?">
            <View className="flex-row flex-wrap gap-2.5">
              {TIPOS.map((t) => {
                const active = values.tipo === t.id;
                const Icon = t.icon;
                return (
                  <Pressable
                    key={t.id}
                    onPress={() => setValue("tipo", t.id, { shouldValidate: true })}
                    className="rounded-lg items-center justify-center py-3.5"
                    style={[
                      {
                        width: "31.5%",
                        backgroundColor: active ? colors.primary[100] : colors.surface,
                      },
                      active ? { borderWidth: 2, borderColor: colors.primary[700] } : shadows.card,
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
          </Group>

          <Group label="Quem cobra essa dívida?">
            <BankFilter
              active={values.banco as BankId}
              onChange={(b) => setValue("banco", b, { shouldValidate: true })}
            />
          </Group>

          <Group label="O quanto ela pesa em você?">
            <View
              className="bg-surface rounded-lg px-5 py-4 flex-row items-center justify-between"
              style={shadows.card}
            >
              <View className="flex-row gap-1.5">
                {[1, 2, 3, 4, 5].map((i) => (
                  <Pressable key={i} onPress={() => setValue("pesoEmocional", i)} hitSlop={4}>
                    <Flame
                      size={26}
                      color={colors.coral[500]}
                      fill={i <= values.pesoEmocional ? colors.coral[500] : "none"}
                      strokeWidth={2}
                    />
                  </Pressable>
                ))}
              </View>
              <Text className="text-xs text-muted text-right max-w-[130px] leading-[15px] font-regular">
                Usado no método Tsunami
              </Text>
            </View>
          </Group>
        </View>
      )}

      {step === 2 && (
        <View className="gap-3.5">
          <Controller
            control={control}
            name="valor"
            render={({ field: { value, onChange } }) => (
              <MaskedField
                label="Valor total da dívida"
                type="currency"
                icon={Wallet}
                placeholder="R$ 0,00"
                value={value}
                onChange={onChange}
                autoFocus
              />
            )}
          />
          <Controller
            control={control}
            name="parcelas"
            render={({ field: { value, onChange } }) => (
              <MaskedField
                label="Número de parcelas"
                type="integer"
                icon={Hash}
                placeholder="Ex.: 12"
                value={value}
                onChange={onChange}
              />
            )}
          />
          <Controller
            control={control}
            name="dataLiberacao"
            render={({ field: { value, onChange } }) => (
              <MaskedField
                label="Data de liberação"
                type="date"
                icon={CalendarPlus}
                placeholder="DD/MM/AAAA"
                hint="Quando o valor caiu na conta ou a compra foi feita"
                value={value}
                onChange={onChange}
              />
            )}
          />
          <Controller
            control={control}
            name="dataPrimeiroVencimento"
            render={({ field: { value, onChange } }) => (
              <MaskedField
                label="Vencimento da 1ª parcela"
                type="date"
                icon={CalendarCheck}
                placeholder="DD/MM/AAAA"
                hint="As próximas caem no mesmo dia dos meses seguintes"
                value={value}
                onChange={onChange}
              />
            )}
          />
        </View>
      )}

      {step === 3 && (
        <View className="gap-3.5">
          <Controller
            control={control}
            name="multaAtraso"
            render={({ field: { value, onChange } }) => (
              <MaskedField
                label="Multa por atraso"
                type="percent"
                icon={Percent}
                placeholder="0,00%"
                hint="Cobrada uma vez sobre a parcela atrasada"
                value={value}
                onChange={onChange}
                autoFocus
              />
            )}
          />
          <Controller
            control={control}
            name="jurosMensal"
            render={({ field: { value, onChange } }) => (
              <MaskedField
                label="Juros mensal (a.m.)"
                type="percent"
                icon={TrendingUp}
                placeholder="0,00%"
                hint="A taxa do contrato — é ela que monta suas parcelas"
                value={value}
                onChange={onChange}
              />
            )}
          />
          <Controller
            control={control}
            name="jurosMora"
            render={({ field: { value, onChange } }) => (
              <MaskedField
                label="Juros de mora (a.m.)"
                type="percent"
                icon={Percent}
                placeholder="0,00%"
                hint="Corre por dia de atraso, quase sempre 1% ao mês"
                value={value}
                onChange={onChange}
              />
            )}
          />
          <Text className="text-xs text-muted font-regular text-center mt-1 px-2 leading-[16px]">
            Não achou algum desses no contrato? Pode deixar em branco e seguir.
          </Text>
        </View>
      )}

      {step === 4 && (
        <View className="flex-row gap-3.5">
          <ChoiceCard
            art="price"
            title="PRICE"
            subtitle="Parcelas iguais do começo ao fim"
            selected={values.sistemaAmortizacao === "PRICE"}
            onPress={() => setValue("sistemaAmortizacao", "PRICE", { shouldValidate: true })}
          />
          <ChoiceCard
            art="sac"
            title="SAC"
            subtitle="Parcela começa maior e vai caindo"
            selected={values.sistemaAmortizacao === "SAC"}
            onPress={() => setValue("sistemaAmortizacao", "SAC", { shouldValidate: true })}
          />
        </View>
      )}

      {step === 5 && (
        <View className="flex-row gap-3.5">
          <ChoiceCard
            art="simples"
            title="Simples"
            subtitle="Juros sempre sobre o valor original"
            selected={values.regimeJuros === "SIMPLES"}
            onPress={() => setValue("regimeJuros", "SIMPLES", { shouldValidate: true })}
          />
          <ChoiceCard
            art="composto"
            title="Composto"
            subtitle="Juros sobre o saldo que ainda deve"
            selected={values.regimeJuros === "COMPOSTO"}
            onPress={() => setValue("regimeJuros", "COMPOSTO", { shouldValidate: true })}
          />
        </View>
      )}
    </StepScaffold>
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
