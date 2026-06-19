import { zodResolver } from "@hookform/resolvers/zod";
import { LinearGradient } from "expo-linear-gradient";
import { Lock, Mail } from "lucide-react-native";
import { Controller, useForm } from "react-hook-form";
import { KeyboardAvoidingView, Platform, Pressable, Text, View } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { Button, Field } from "../../../components";
import { colors } from "../../../theme";
import { getApiErrorMessage } from "../../../util/apiError";
import { useLogin } from "../hooks/useLogin";
import { loginFormSchema, type LoginFormValues } from "../schema/authSchema";

type Props = {
  onAuthenticated: () => void;
  onGoToRegister: () => void;
};

export default function LoginScreen({ onAuthenticated, onGoToRegister }: Props) {
  const insets = useSafeAreaInsets();
  const login = useLogin();

  const {
    control,
    handleSubmit,
    setError,
    formState: { errors },
  } = useForm<LoginFormValues>({
    resolver: zodResolver(loginFormSchema),
    defaultValues: { email: "", senha: "" },
  });

  function onSubmit(values: LoginFormValues) {
    login.mutate(
      { email: values.email.trim(), senha: values.senha },
      {
        onSuccess: onAuthenticated,
        onError: (error) =>
          setError("root", {
            message: getApiErrorMessage(error, "Não foi possível entrar. Tente novamente."),
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
      <View className="px-10 pb-10" style={{ paddingTop: insets.top + 80 }}>
        <Text className="font-bold text-[64px] leading-[70px] tracking-[-1px] text-white">TOLEV</Text>
        <Text className="text-white text-[18px] mt-1.5 font-regular">Sua experiência financeira</Text>
      </View>

      <KeyboardAvoidingView
        behavior={Platform.OS === "ios" ? "padding" : undefined}
        className="flex-1 bg-[#FEFEFE] rounded-t-[32px] px-8 pt-10"
      >
        <Text className="font-bold text-3xl text-primary-700 mb-9">Login</Text>

        <View className="gap-3.5">
          <View>
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
            {errors.email ? (
              <Text className="text-[12px] text-coral-500 font-regular mt-1.5 pl-1">{errors.email.message}</Text>
            ) : null}
          </View>

          <View>
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
                />
              )}
            />
            {errors.senha ? (
              <Text className="text-[12px] text-coral-500 font-regular mt-1.5 pl-1">{errors.senha.message}</Text>
            ) : null}
          </View>

          <Pressable>
            <Text className="text-right text-[14px] text-info-700 px-1.5 pb-2 font-medium">Esqueci a senha</Text>
          </Pressable>

          {errors.root ? (
            <Text className="text-[13px] text-coral-500 font-medium text-center -mt-1">{errors.root.message}</Text>
          ) : null}

          <Button variant="primary" onPress={login.isPending ? undefined : handleSubmit(onSubmit)}>
            {login.isPending ? "Entrando..." : "Entrar"}
          </Button>

          <View className="flex-row items-center gap-3 my-1.5">
            <View className="flex-1 h-px bg-black/[0.34]" />
            <Text className="text-muted text-base font-regular">ou</Text>
            <View className="flex-1 h-px bg-black/[0.34]" />
          </View>

          <Button variant="outline" onPress={onGoToRegister}>Criar conta</Button>
        </View>
      </KeyboardAvoidingView>
    </View>
  );
}
