import { zodResolver } from "@hookform/resolvers/zod";
import { LinearGradient } from "expo-linear-gradient";
import { AtSign, Lock, Mail, User } from "lucide-react-native";
import { Controller, useForm } from "react-hook-form";
import {
  KeyboardAvoidingView,
  Platform,
  Pressable,
  ScrollView,
  Text,
  View,
} from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { Button, Field } from "../../../components";
import { colors } from "../../../theme";
import { getApiErrorMessage } from "../../../util/apiError";
import { useRegister } from "../hooks/useRegister";
import { registerFormSchema, type RegisterFormValues } from "../schema/authSchema";

type Props = {
  onAuthenticated: () => void;
  onGoToLogin: () => void;
};

export default function RegisterScreen({ onAuthenticated, onGoToLogin }: Props) {
  const insets = useSafeAreaInsets();
  const register = useRegister();

  const {
    control,
    handleSubmit,
    setError,
    formState: { errors },
  } = useForm<RegisterFormValues>({
    resolver: zodResolver(registerFormSchema),
    defaultValues: { nome: "", nomeUsuario: "", email: "", senha: "", confirmarSenha: "" },
  });

  function onSubmit(values: RegisterFormValues) {
    register.mutate(
      {
        nome: values.nome?.trim() ? values.nome.trim() : null,
        nomeUsuario: values.nomeUsuario.trim(),
        email: values.email.trim(),
        senha: values.senha,
      },
      {
        onSuccess: onAuthenticated,
        onError: (error) =>
          setError("root", {
            message: getApiErrorMessage(error, "Não foi possível criar a conta. Tente novamente."),
          }),
      },
    );
  }

  return (
    <View className="flex-1 bg-primary-600">
      <LinearGradient
        colors={[colors.primary[700], colors.primary[800]]}
        className="absolute top-0 left-0 right-0 bottom-0"
      />
      <View className="px-10 pb-8" style={{ paddingTop: insets.top + 56 }}>
        <Text className="font-bold text-[48px] leading-[54px] tracking-[-1px] text-white">Criar conta</Text>
        <Text className="text-white text-[16px] mt-1.5 font-regular">Comece sua jornada financeira</Text>
      </View>

      <KeyboardAvoidingView
        behavior={Platform.OS === "ios" ? "padding" : undefined}
        className="flex-1 bg-[#FEFEFE] rounded-t-[32px]"
      >
        <ScrollView
          className="px-8 pt-9"
          contentContainerStyle={{ paddingBottom: insets.bottom + 32 }}
          keyboardShouldPersistTaps="handled"
          showsVerticalScrollIndicator={false}
        >
          <View className="gap-3.5">
            <FormField error={errors.nome?.message}>
              <Controller
                control={control}
                name="nome"
                render={({ field: { value, onChange } }) => (
                  <Field
                    icon={User}
                    placeholder="Nome (opcional)"
                    value={value}
                    onChangeText={onChange}
                    autoCapitalize="words"
                    autoComplete="name"
                  />
                )}
              />
            </FormField>

            <FormField error={errors.nomeUsuario?.message}>
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
            </FormField>

            <FormField error={errors.email?.message}>
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
            </FormField>

            <FormField error={errors.senha?.message}>
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
            </FormField>

            <FormField error={errors.confirmarSenha?.message}>
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
            </FormField>

            {errors.root ? (
              <Text className="text-[13px] text-coral-500 font-medium text-center">{errors.root.message}</Text>
            ) : null}

            <Button
              variant="primary"
              onPress={register.isPending ? undefined : handleSubmit(onSubmit)}
            >
              {register.isPending ? "Criando conta..." : "Criar conta"}
            </Button>

            <Pressable className="mt-1.5" onPress={onGoToLogin}>
              <Text className="text-center text-[14px] text-muted font-regular">
                Já tem uma conta? <Text className="text-info-700 font-semibold">Entrar</Text>
              </Text>
            </Pressable>
          </View>
        </ScrollView>
      </KeyboardAvoidingView>
    </View>
  );
}

function FormField({ error, children }: { error?: string; children: React.ReactNode }) {
  return (
    <View>
      {children}
      {error ? (
        <Text className="text-[12px] text-coral-500 font-regular mt-1.5 pl-1">{error}</Text>
      ) : null}
    </View>
  );
}
