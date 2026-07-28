import { zodResolver } from "@hookform/resolvers/zod";
import { User } from "lucide-react-native";
import { useState } from "react";
import { Controller, useForm } from "react-hook-form";
import { View } from "react-native";
import { Field } from "../../../components";
import type { RegisterRequest } from "../../../types/auth";
import { getApiErrorMessage } from "../../../util/apiError";
import { useRegister } from "../../auth/hooks/useRegister";
import AccountStep from "../components/AccountStep";
import CurrencyInput from "../components/CurrencyInput";
import IntroStep from "../components/IntroStep";
import OptionCard from "../components/OptionCard";
import StepScaffold from "../components/StepScaffold";
import {
  OBJETIVO_OPTIONS,
  OCUPACAO_OPTIONS,
  SITUACAO_OPTIONS,
  type Option,
} from "../constants/options";
import { onboardingSchema, type OnboardingValues } from "../schema/onboardingSchema";

type Props = {
  onAuthenticated: () => void;
  onGoToLogin: () => void;
};

const TOTAL = 6;

/** Fields validated when leaving each input step (screen index = position + 1). */
const STEP_FIELDS: (keyof OnboardingValues)[][] = [
  ["nome"],
  ["objetivoPrincipal"],
  ["situacaoFinanceira"],
  ["ocupacao"],
  ["rendaMensal"],
  ["nomeUsuario", "email", "senha", "confirmarSenha"],
];

const TITLES: { title: string; subtitle: string }[] = [
  { title: "Como podemos te chamar?", subtitle: "Seu primeiro nome ou um apelido." },
  { title: "O que te traz ao Tolev?", subtitle: "Escolha seu principal objetivo agora." },
  { title: "Como está sua vida financeira hoje?", subtitle: "Sem julgamentos — é só pra te ajudar melhor." },
  { title: "Como você ganha seu dinheiro?", subtitle: "Isso ajuda a entender sua renda." },
  { title: "Quanto você recebe por mês?", subtitle: "Um valor aproximado já basta." },
  { title: "Crie sua conta", subtitle: "Só falta isso pra você começar." },
];

export default function OnboardingFlow({ onAuthenticated, onGoToLogin }: Props) {
  const register = useRegister();
  const [screen, setScreen] = useState(0); // 0 = intro, 1..6 = input steps

  const {
    control,
    handleSubmit,
    trigger,
    setValue,
    setError,
    watch,
    formState: { errors },
  } = useForm<OnboardingValues>({
    resolver: zodResolver(onboardingSchema),
    defaultValues: {
      nome: "",
      rendaMensal: "",
      nomeUsuario: "",
      email: "",
      senha: "",
      confirmarSenha: "",
    },
    mode: "onTouched",
  });

  const values = watch();

  function onSubmit(v: OnboardingValues) {
    const payload: RegisterRequest = {
      nome: v.nome.trim(),
      objetivoPrincipal: v.objetivoPrincipal,
      situacaoFinanceira: v.situacaoFinanceira,
      ocupacao: v.ocupacao,
      rendaMensal: v.rendaMensal ? Number(v.rendaMensal) : null,
      nomeUsuario: v.nomeUsuario.trim(),
      email: v.email.trim(),
      senha: v.senha,
    };
    register.mutate(payload, {
      onSuccess: onAuthenticated,
      onError: (error) =>
        setError("root", {
          message: getApiErrorMessage(error, "Não foi possível criar a conta. Tente novamente."),
        }),
    });
  }

  async function goNext() {
    const ok = await trigger(STEP_FIELDS[screen - 1]);
    if (!ok) return;
    if (screen < TOTAL) setScreen(screen + 1);
    else handleSubmit(onSubmit)();
  }

  function renderOptions<T extends string>(
    field: keyof OnboardingValues,
    options: Option<T>[],
    compact = false,
  ) {
    return (
      <View className="gap-3">
        {options.map((o) => (
          <OptionCard
            key={o.value}
            icon={o.icon}
            title={o.title}
            subtitle={o.subtitle}
            compact={compact}
            selected={values[field] === o.value}
            onPress={() => setValue(field, o.value as never, { shouldValidate: true })}
          />
        ))}
      </View>
    );
  }

  if (screen === 0) {
    return <IntroStep onStart={() => setScreen(1)} onGoToLogin={onGoToLogin} />;
  }

  const meta = TITLES[screen - 1];
  const isAccount = screen === TOTAL;

  const canContinue =
    screen === 1
      ? !!values.nome?.trim()
      : screen === 2
      ? !!values.objetivoPrincipal
      : screen === 3
      ? !!values.situacaoFinanceira
      : screen === 4
      ? !!values.ocupacao
      : screen === 5
      ? !!values.rendaMensal
      : true;

  const scaffoldError =
    screen === 1
      ? errors.nome?.message
      : screen === 2
      ? errors.objetivoPrincipal?.message
      : screen === 3
      ? errors.situacaoFinanceira?.message
      : screen === 4
      ? errors.ocupacao?.message
      : screen === 5
      ? errors.rendaMensal?.message
      : errors.root?.message;

  return (
    <StepScaffold
      step={screen}
      total={TOTAL}
      onBack={() => setScreen(screen - 1)}
      title={meta.title}
      subtitle={meta.subtitle}
      onContinue={goNext}
      continueDisabled={!canContinue || (isAccount && register.isPending)}
      continueLabel={isAccount ? (register.isPending ? "Criando conta..." : "Criar conta") : "Continuar"}
      error={scaffoldError}
    >
      {screen === 1 && (
        <Controller
          control={control}
          name="nome"
          render={({ field: { value, onChange } }) => (
            <Field
              icon={User}
              placeholder="Seu nome"
              value={value}
              onChangeText={onChange}
              autoCapitalize="words"
              autoComplete="name"
            />
          )}
        />
      )}

      {screen === 2 && renderOptions("objetivoPrincipal", OBJETIVO_OPTIONS)}
      {screen === 3 && renderOptions("situacaoFinanceira", SITUACAO_OPTIONS)}
      {screen === 4 && renderOptions("ocupacao", OCUPACAO_OPTIONS, true)}

      {screen === 5 && (
        <Controller
          control={control}
          name="rendaMensal"
          render={({ field: { value, onChange } }) => (
            <CurrencyInput value={value} onChange={onChange} autoFocus />
          )}
        />
      )}

      {screen === 6 && <AccountStep control={control} errors={errors} />}
    </StepScaffold>
  );
}
