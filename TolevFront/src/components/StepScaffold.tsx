import { ArrowLeft } from "lucide-react-native";
import { useState, type ReactNode } from "react";
import {
  KeyboardAvoidingView,
  Platform,
  Pressable,
  ScrollView,
  Text,
  View,
} from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { colors } from "../theme";
import HelpSheet, { type HelpContent } from "./HelpSheet";

type Props = {
  /** Índice da etapa atual começando em 1 (e não em 0), para a barra. */
  step: number;
  total: number;
  onBack: () => void;
  title: string;
  subtitle?: string;
  children: ReactNode;
  onContinue: () => void;
  continueLabel?: string;
  continueDisabled?: boolean;
  /** Mensagem de erro logo acima do botão Continuar. */
  error?: string;
  /** Abre a folha "Onde encontro isso?" logo acima do botão. */
  help?: HelpContent;
};

export default function StepScaffold({
  step,
  total,
  onBack,
  title,
  subtitle,
  children,
  onContinue,
  continueLabel = "Continuar",
  continueDisabled = false,
  error,
  help,
}: Props) {
  const insets = useSafeAreaInsets();
  const [helpOpen, setHelpOpen] = useState(false);
  const pct = Math.max(0, Math.min(100, (step / total) * 100));

  return (
    <View className="flex-1 bg-bg">
      <View
        className="flex-row items-center gap-3 px-5 pb-2"
        style={{ paddingTop: insets.top + 8 }}
      >
        <Pressable
          onPress={onBack}
          hitSlop={10}
          className="w-9 h-9 rounded-full items-center justify-center active:opacity-70"
        >
          <ArrowLeft size={24} color={colors.text.secondary} strokeWidth={2.4} />
        </Pressable>
        <View className="flex-1 h-3 rounded-pill bg-primary-100 overflow-hidden">
          <View
            className="h-full rounded-pill bg-primary-500"
            style={{ width: `${pct}%` }}
          />
        </View>
      </View>

      <KeyboardAvoidingView
        behavior={Platform.OS === "ios" ? "padding" : undefined}
        className="flex-1"
      >
        <ScrollView
          className="flex-1"
          contentContainerClassName="px-6 pt-6 pb-4"
          keyboardShouldPersistTaps="handled"
          showsVerticalScrollIndicator={false}
        >
          <Text className="font-bold text-[26px] leading-[32px] tracking-[-0.5px] text-ink">
            {title}
          </Text>
          {subtitle && (
            <Text className="text-[15px] text-muted mt-2 leading-[21px] font-regular">
              {subtitle}
            </Text>
          )}
          <View className="mt-7">{children}</View>
        </ScrollView>

        <View
          className="px-6 pt-3 bg-bg"
          style={{
            borderTopWidth: 1,
            borderTopColor: colors.primary[50],
            paddingBottom: insets.bottom + 14,
          }}
        >
          {help ? (
            <Pressable
              onPress={() => setHelpOpen(true)}
              hitSlop={8}
              className="self-center mb-3 active:opacity-60"
            >
              <Text className="text-[15px] font-bold text-primary-700 underline">
                {help.label ?? "Onde encontro isso?"}
              </Text>
            </Pressable>
          ) : null}

          {error ? (
            <Text className="text-[13px] text-coral-500 font-medium text-center mb-2.5">
              {error}
            </Text>
          ) : null}

          <Pressable
            onPress={continueDisabled ? undefined : onContinue}
            className="h-[54px] rounded-pill items-center justify-center active:scale-[0.99]"
            style={{
              backgroundColor: continueDisabled ? colors.primary[100] : colors.coral[500],
            }}
          >
            <Text
              className="font-bold text-[17px]"
              style={{ color: continueDisabled ? colors.text.secondary : colors.surface }}
            >
              {continueLabel}
            </Text>
          </Pressable>
        </View>
      </KeyboardAvoidingView>

      {help ? (
        <HelpSheet content={help} visible={helpOpen} onClose={() => setHelpOpen(false)} />
      ) : null}
    </View>
  );
}
