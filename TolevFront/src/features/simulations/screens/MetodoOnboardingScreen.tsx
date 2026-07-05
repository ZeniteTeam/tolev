import { useNavigation, useRoute } from "@react-navigation/native";
import {
  AlertCircle,
  ArrowLeft,
  CalendarClock,
  CheckCircle,
  PiggyBank,
  X,
} from "lucide-react-native";
import { useState } from "react";
import { Pressable, ScrollView, Text, View } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { colors, shadows } from "../../../theme";
import { brl, DIVIDAS_SEED } from "../../debts/constants/dividas";
import { metodoById } from "../constants/metodos";
import { usePlanStore } from "../store/planStore";

const STEPS = ["Visão geral", "Prós e projeções", "Resumo"];

export default function MetodoOnboardingScreen() {
  const navigation = useNavigation<any>();
  const route = useRoute<any>();
  const insets = useSafeAreaInsets();
  const setMetodo = usePlanStore((s) => s.setMetodo);

  const metodo = metodoById(route.params?.id);
  const [step, setStep] = useState(0);
  const ordem = metodo.ordenar(DIVIDAS_SEED);
  const total = DIVIDAS_SEED.reduce((s, d) => s + d.saldo, 0);
  const Icon = metodo.icon;

  const critLabel = (d: (typeof ordem)[number]) =>
    metodo.criterio === "juros"
      ? `${d.juros.toFixed(1).replace(".", ",")}% a.m.`
      : metodo.criterio === "saldo"
      ? brl(d.saldo)
      : "★".repeat(d.emocional);

  const close = () => navigation.goBack();
  const apply = () => {
    setMetodo(metodo.id);
    navigation.goBack();
  };

  return (
    <View className="flex-1 bg-bg">
      <View style={{ backgroundColor: metodo.color, paddingTop: insets.top + 12 }} className="px-[18px] pb-4">
        <View className="flex-row items-center gap-3">
          <Pressable
            onPress={step === 0 ? close : () => setStep((s) => s - 1)}
            className="w-9 h-9 rounded-full items-center justify-center bg-white/[0.16]"
          >
            <ArrowLeft size={20} color="#fff" strokeWidth={2} />
          </Pressable>
          <View className="flex-1">
            <Text className="text-white/[0.85] text-[12px] font-regular">Método {metodo.nome}</Text>
            <Text className="text-white font-bold text-[18px]">{STEPS[step]}</Text>
          </View>
          <Pressable onPress={close} className="w-9 h-9 rounded-full items-center justify-center bg-white/[0.16]">
            <X size={20} color="#fff" strokeWidth={2} />
          </Pressable>
        </View>
        <View className="flex-row gap-1.5 mt-4">
          {STEPS.map((_, i) => (
            <View
              key={i}
              className="flex-1 h-1 rounded-pill"
              style={{ backgroundColor: i <= step ? "#fff" : "rgba(255,255,255,0.3)" }}
            />
          ))}
        </View>
      </View>

      <ScrollView
        className="flex-1"
        contentContainerClassName="px-5 pt-[18px] pb-4"
        showsVerticalScrollIndicator={false}
      >
        {step === 0 && (
          <>
            <View
              className="w-[54px] h-[54px] rounded-[16px] items-center justify-center mb-3"
              style={{ backgroundColor: metodo.color + "1A" }}
            >
              <Icon size={26} color={metodo.color} strokeWidth={2} />
            </View>
            <Text className="font-bold text-[20px] text-ink mb-1.5">Como funciona</Text>
            <Text className="text-[14px] text-muted leading-[21px] mb-4 font-regular">{metodo.overview}</Text>

            <Text className="text-[11px] text-muted font-bold tracking-[0.5px] mb-2.5">ORDEM DE ATAQUE</Text>
            <View className="gap-2">
              {ordem.map((d, i) => (
                <View
                  key={d.id}
                  className="flex-row items-center gap-3 bg-surface rounded-[13px] px-3.5 py-[11px]"
                  style={shadows.card}
                >
                  <View
                    className="w-[26px] h-[26px] rounded-full items-center justify-center"
                    style={{ backgroundColor: i === 0 ? metodo.color : colors.primary[100] }}
                  >
                    <Text
                      className="font-bold text-[13px]"
                      style={{ color: i === 0 ? "#fff" : colors.primary[700] }}
                    >
                      {i + 1}
                    </Text>
                  </View>
                  <View className="flex-1">
                    <Text className="text-[14px] font-semibold text-ink">{d.nome}</Text>
                    <Text className="text-[12px] text-muted font-regular">{brl(d.saldo)}</Text>
                  </View>
                  <Text className="text-[13px] font-bold" style={{ color: metodo.color }}>
                    {critLabel(d)}
                  </Text>
                </View>
              ))}
            </View>
          </>
        )}

        {step === 1 && (
          <>
            <View className="flex-row gap-3 mb-5">
              <View className="flex-1 bg-surface rounded-[16px] p-[18px]" style={shadows.card}>
                <CalendarClock size={20} color={metodo.color} strokeWidth={2} />
                <Text className="font-bold text-[24px] text-ink mt-2.5">
                  {metodo.meses}
                  <Text className="text-[14px] text-muted font-regular"> meses</Text>
                </Text>
                <Text className="text-[11px] text-muted mt-0.5 font-regular">para quitar tudo</Text>
              </View>
              <View className="flex-1 bg-surface rounded-[16px] p-[18px]" style={shadows.card}>
                <PiggyBank size={20} color={colors.teal[500]} strokeWidth={2} />
                <Text className="font-bold text-[24px] text-teal-500 mt-2.5">{metodo.economia}</Text>
                <Text className="text-[11px] text-muted mt-0.5 font-regular">em juros economizados</Text>
              </View>
            </View>

            <ProsContras titulo="Vantagens" color={colors.teal[500]} icon="check" itens={metodo.pros} />
            <View className="h-3" />
            <ProsContras titulo="Desvantagens" color={colors.coral[500]} icon="alert" itens={metodo.contras} />
          </>
        )}

        {step === 2 && (
          <>
            <View className="rounded-[18px] p-[22px] mb-4" style={{ backgroundColor: metodo.color }}>
              <Text className="text-white/[0.85] text-[13px] font-regular">Com o método {metodo.nome}</Text>
              <Text className="text-white font-bold text-[26px] mt-1">Livre em {metodo.meses} meses</Text>
              <View className="flex-row mt-4 pt-4 border-t border-t-white/20 gap-3.5">
                <View className="flex-1">
                  <Text className="text-white/80 text-[11px] font-regular">Dívida total</Text>
                  <Text className="text-white font-bold text-[17px] mt-0.5">{brl(total)}</Text>
                </View>
                <View className="flex-1">
                  <Text className="text-white/80 text-[11px] font-regular">Economia</Text>
                  <Text className="text-white font-bold text-[17px] mt-0.5">{metodo.economia}</Text>
                </View>
              </View>
            </View>

            <Text className="text-[11px] text-muted font-bold tracking-[0.5px] mb-2.5">PRIMEIRO ALVO</Text>
            <View className="flex-row items-center gap-3 bg-surface rounded-[14px] px-4 py-4 mb-5" style={shadows.card}>
              <View
                className="w-10 h-10 rounded-[11px] items-center justify-center"
                style={{ backgroundColor: ordem[0].bankColor }}
              >
                {(() => {
                  const FirstIcon = ordem[0].icon;
                  return <FirstIcon size={19} color="#fff" strokeWidth={2} />;
                })()}
              </View>
              <View className="flex-1">
                <Text className="text-[14px] font-semibold text-ink">{ordem[0].nome}</Text>
                <Text className="text-[12px] text-muted font-regular">
                  {brl(ordem[0].saldo)} · {ordem[0].juros.toFixed(1).replace(".", ",")}% a.m.
                </Text>
              </View>
            </View>

            <Text className="text-[13px] text-muted leading-[20px] font-regular">
              Você pode trocar de método a qualquer momento sem perder seu progresso.
            </Text>
          </>
        )}
      </ScrollView>

      <View
        className="px-5 pt-3.5 bg-bg"
        style={{ borderTopWidth: 1, borderTopColor: "#EAEFEC", paddingBottom: insets.bottom + 16 }}
      >
        {step < 2 ? (
          <View className="flex-row gap-3">
            <Pressable
              onPress={close}
              className="h-[52px] px-5 rounded-pill bg-primary-50 items-center justify-center"
            >
              <Text className="font-bold text-[15px] text-muted">Trocar método</Text>
            </Pressable>
            <Pressable
              onPress={() => setStep((s) => s + 1)}
              className="flex-1 h-[52px] rounded-pill items-center justify-center"
              style={{ backgroundColor: metodo.color }}
            >
              <Text className="font-bold text-[16px] text-white">Continuar</Text>
            </Pressable>
          </View>
        ) : (
          <>
            <Pressable
              onPress={apply}
              className="h-[52px] rounded-pill items-center justify-center"
              style={{ backgroundColor: metodo.color }}
            >
              <Text className="font-bold text-[16px] text-white">Usar este método</Text>
            </Pressable>
            <Pressable onPress={close} className="h-[46px] mt-1.5 rounded-pill items-center justify-center">
              <Text className="font-bold text-[15px] text-muted">Escolher outro método</Text>
            </Pressable>
          </>
        )}
      </View>
    </View>
  );
}

function ProsContras({
  titulo,
  color,
  icon,
  itens,
}: {
  titulo: string;
  color: string;
  icon: "check" | "alert";
  itens: string[];
}) {
  return (
    <View className="bg-surface rounded-[16px] p-[18px]" style={shadows.card}>
      <View className="flex-row items-center gap-2 mb-3">
        {icon === "check" ? (
          <CheckCircle size={18} color={color} strokeWidth={2} />
        ) : (
          <AlertCircle size={18} color={color} strokeWidth={2} />
        )}
        <Text className="font-bold text-[15px] text-ink">{titulo}</Text>
      </View>
      <View className="gap-2.5">
        {itens.map((t, i) => (
          <View key={i} className="flex-row gap-2.5 items-start">
            <View className="w-1.5 h-1.5 rounded-full mt-[7px]" style={{ backgroundColor: color }} />
            <Text className="flex-1 text-[13.5px] text-ink leading-[19px] font-regular">{t}</Text>
          </View>
        ))}
      </View>
    </View>
  );
}
