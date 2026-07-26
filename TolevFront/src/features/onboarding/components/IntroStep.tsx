import { LinearGradient } from "expo-linear-gradient";
import { Pressable, Text, View } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { colors, shadows } from "../../../theme";
import BrandLogo from "./BrandLogo";

type Props = {
  onStart: () => void;
  onGoToLogin: () => void;
};

/** Welcome screen — brand logo + a single call to action to begin onboarding. */
export default function IntroStep({ onStart, onGoToLogin }: Props) {
  const insets = useSafeAreaInsets();

  return (
    <View className="flex-1 bg-primary-700">
      <LinearGradient
        colors={[colors.primary[700], colors.primary[900]]}
        className="absolute top-0 left-0 right-0 bottom-0"
      />

      <View className="flex-1 items-center justify-center px-8">
        <BrandLogo size={92} variant="full" onDark />
        <Text className="text-white font-bold text-[26px] text-center mt-8 leading-[32px]">
          Sua vida financeira,{"\n"}leve de verdade
        </Text>
        <Text className="text-white/80 text-[15px] text-center mt-3 leading-[22px] font-regular">
          Vamos montar um plano sob medida pra você sair das dívidas e assumir o
          controle do seu dinheiro.
        </Text>
      </View>

      <View className="px-6" style={{ paddingBottom: insets.bottom + 16 }}>
        <Pressable
          onPress={onStart}
          className="h-[54px] rounded-pill items-center justify-center bg-coral-500 active:scale-[0.99]"
          style={shadows.cta}
        >
          <Text className="font-bold text-[17px] text-white">Começar</Text>
        </Pressable>
        <Pressable onPress={onGoToLogin} className="h-11 mt-2 items-center justify-center">
          <Text className="text-white/90 text-[14px] font-regular">
            Já tem uma conta? <Text className="font-bold text-white">Entrar</Text>
          </Text>
        </Pressable>
      </View>
    </View>
  );
}
