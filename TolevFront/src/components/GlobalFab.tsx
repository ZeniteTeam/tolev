import { AnimatePresence, MotiView } from "moti";
import { CreditCard, Plus, Receipt, Sparkles, X } from "lucide-react-native";
import { useState } from "react";
import { Pressable, Text, View } from "react-native";
import { shadows } from "../theme";

type Props = {
  onSimular?: () => void;
  onAddDivida?: () => void;
  onAddTransacao?: () => void;
  bottom?: number;
};

/**
 * Speed-dial FAB shown on the main tabs. Expands to "Simular", "Adicionar
 * dívida" and "Adicionar transação", matching the design's global action
 * button. A transação fica por último porque é a ação mais frequente: encosta
 * no botão principal e vira o menor alvo de toque.
 */
export default function GlobalFab({
  onSimular,
  onAddDivida,
  onAddTransacao,
  bottom = 78,
}: Props) {
  const [open, setOpen] = useState(false);

  const act = (fn?: () => void) => {
    setOpen(false);
    fn?.();
  };

  return (
    <>
      {open && (
        <Pressable
          onPress={() => setOpen(false)}
          className="absolute inset-0"
          style={{ backgroundColor: "rgba(0,0,0,0.04)" }}
        />
      )}
      <View className="absolute right-6 items-end" style={{ bottom }}>
        <AnimatePresence>
          {open && (
            <MotiView
              from={{ opacity: 0, translateY: 10 }}
              animate={{ opacity: 1, translateY: 0 }}
              exit={{ opacity: 0, translateY: 10 }}
              style={{ alignItems: "flex-end", gap: 14, marginBottom: 18 }}
            >
              <Action label="Simular" icon={Sparkles} onPress={() => act(onSimular)} />
              <Action label="Adicionar dívida" icon={CreditCard} onPress={() => act(onAddDivida)} />
              <Action
                label="Adicionar transação"
                icon={Receipt}
                onPress={() => act(onAddTransacao)}
              />
            </MotiView>
          )}
        </AnimatePresence>

        <Pressable
          onPress={() => setOpen((o) => !o)}
          className="w-14 h-14 rounded-full bg-coral-500 items-center justify-center active:scale-95"
          style={shadows.cta}
        >
          {open ? (
            <X size={26} color="#fff" strokeWidth={2.5} />
          ) : (
            <Plus size={26} color="#fff" strokeWidth={2.5} />
          )}
        </Pressable>
      </View>
    </>
  );
}

function Action({
  label,
  icon: Icon,
  onPress,
}: {
  label: string;
  icon: typeof Plus;
  onPress: () => void;
}) {
  return (
    <Pressable onPress={onPress} className="flex-row items-center gap-3 active:opacity-90">
      <View className="bg-surface px-3.5 py-2 rounded-pill" style={shadows.card}>
        <Text className="text-[13px] font-bold text-ink">{label}</Text>
      </View>
      {/* 56px box centers the 48px icon under the 56px main FAB */}
      <View className="w-14 items-center">
        <View className="w-12 h-12 rounded-full bg-primary-700 items-center justify-center" style={shadows.card}>
          <Icon size={20} color="#fff" strokeWidth={2} />
        </View>
      </View>
    </Pressable>
  );
}
