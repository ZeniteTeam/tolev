import { LinearGradient } from "expo-linear-gradient";
import { Lock, Mail } from "lucide-react-native";
import { useState } from "react";
import { KeyboardAvoidingView, Platform, Pressable, Text, View } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { Button, Field } from "../../../components";
import { colors } from "../../../theme";

type Props = {
  onLogin: () => void;
};

export default function LoginScreen({ onLogin }: Props) {
  const insets = useSafeAreaInsets();
  const [email, setEmail] = useState("maria@tolev.app");
  const [senha, setSenha] = useState("senha-leve");

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
          <Field icon={Mail} placeholder="Email" value={email} onChangeText={setEmail} />
          <Field icon={Lock} placeholder="Senha" secureTextEntry value={senha} onChangeText={setSenha} />

          <Pressable>
            <Text className="text-right text-[14px] text-info-700 px-1.5 pb-2 font-medium">Esqueci a senha</Text>
          </Pressable>

          <Button variant="primary" onPress={onLogin}>Entrar</Button>

          <View className="flex-row items-center gap-3 my-1.5">
            <View className="flex-1 h-px bg-black/[0.34]" />
            <Text className="text-muted text-base font-regular">ou</Text>
            <View className="flex-1 h-px bg-black/[0.34]" />
          </View>

          <Button variant="outline" onPress={onLogin}>Criar conta</Button>
        </View>
      </KeyboardAvoidingView>
    </View>
  );
}
