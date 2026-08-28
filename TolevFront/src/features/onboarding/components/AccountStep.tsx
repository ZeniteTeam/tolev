import { AtSign, Lock, Mail } from "lucide-react-native";
import { Control, Controller, FieldErrors } from "react-hook-form";
import { Text, View } from "react-native";
import { Field } from "../../../components";
import type { OnboardingValues } from "../schema/onboardingSchema";

type Props = {
  control: Control<OnboardingValues>;
  errors: FieldErrors<OnboardingValues>;
};

export default function AccountStep({ control, errors }: Props) {
  return (
    <View className="gap-3.5">
      <FieldRow error={errors.email?.message}>
        <Controller
          control={control}
          name="email"
          render={({ field: { value, onChange } }) => (
            <Field
              icon={Mail}
              placeholder="Email"
              value={value}
              onChangeText={onChange}
              keyboardType="email-address"
              autoCapitalize="none"
              autoComplete="email"
            />
          )}
        />
      </FieldRow>

      <FieldRow error={errors.nomeUsuario?.message}>
        <Controller
          control={control}
          name="nomeUsuario"
          render={({ field: { value, onChange } }) => (
            <Field
              icon={AtSign}
              placeholder="Nome de usuário"
              value={value}
              onChangeText={onChange}
              autoCapitalize="none"
              autoComplete="username"
            />
          )}
        />
      </FieldRow>

      <FieldRow error={errors.senha?.message}>
        <Controller
          control={control}
          name="senha"
          render={({ field: { value, onChange } }) => (
            <Field
              icon={Lock}
              placeholder="Senha"
              secureTextEntry
              value={value}
              onChangeText={onChange}
              autoCapitalize="none"
              autoComplete="password-new"
            />
          )}
        />
      </FieldRow>

      <FieldRow error={errors.confirmarSenha?.message}>
        <Controller
          control={control}
          name="confirmarSenha"
          render={({ field: { value, onChange } }) => (
            <Field
              icon={Lock}
              placeholder="Confirmar senha"
              secureTextEntry
              value={value}
              onChangeText={onChange}
              autoCapitalize="none"
              autoComplete="password-new"
            />
          )}
        />
      </FieldRow>
    </View>
  );
}

function FieldRow({ error, children }: { error?: string; children: React.ReactNode }) {
  return (
    <View>
      {children}
      {error ? (
        <Text className="text-[12px] text-coral-500 font-regular mt-1.5 pl-1">{error}</Text>
      ) : null}
    </View>
  );
}
