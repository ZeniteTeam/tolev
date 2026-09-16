import { MotiView } from "moti";
import { useState } from "react";
import { Pressable, Text, View } from "react-native";
import { colors, motion, radius, spacing } from "../theme";

type Item = { key: string; label: string };

type Props = {
  items: Item[];
  active: string;
  onChange: (k: string) => void;
};

/** Largura do traço (o `w-9` de antes) e o `py-3` que o afasta da borda. */
const TRACO = 36;
const FUNDO = spacing[3];

export default function TabsUnderline({ items, active, onChange }: Props) {
  const [largura, setLargura] = useState(0);
  // -1 vira 0: se a aba ativa não estiver na lista, o traço fica na primeira em
  // vez de sair pela esquerda.
  const indice = Math.max(0, items.findIndex((i) => i.key === active));
  const larguraAba = items.length > 0 ? largura / items.length : 0;
  const x = indice * larguraAba + (larguraAba - TRACO) / 2;

  return (
    <View
      className="border-b border-b-[#F1F5F3]"
      onLayout={(e) => setLargura(e.nativeEvent.layout.width)}
    >
      <View className="flex-row">
        {items.map((it) => {
          const isActive = active === it.key;
          return (
            <Pressable
              key={it.key}
              onPress={() => onChange(it.key)}
              className="flex-1 items-center py-3 gap-2"
            >
              <Text
                className={`font-semibold text-md ${isActive ? "text-primary-700" : "text-muted"}`}
              >
                {it.label}
              </Text>
              {/* O traço deslizante passa por cima, mas a altura precisa ficar
                  reservada aqui — senão a linha inteira pula ao trocar de aba. */}
              <View style={{ height: 3 }} />
            </Pressable>
          );
        })}
      </View>

      {largura > 0 && (
        <MotiView
          animate={{ translateX: x }}
          transition={{
            type: "timing",
            duration: motion.duration.fast,
            easing: motion.easing.inOut,
          }}
          style={{
            position: "absolute",
            left: 0,
            bottom: FUNDO,
            width: TRACO,
            height: 3,
            borderRadius: radius.pill,
            backgroundColor: colors.coral[500],
          }}
        />
      )}
    </View>
  );
}
