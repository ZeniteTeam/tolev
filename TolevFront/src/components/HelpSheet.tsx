import { X } from "lucide-react-native";
import { Modal, Pressable, ScrollView, Text, View } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { colors } from "../theme";

export type HelpContent = {
  /** Bold link rendered above the primary button. */
  label?: string;
  title: string;
  /** Opening paragraph — what the field means, in plain words. */
  body: string;
  /** Concrete places to look for the number, one per line. */
  ondeEncontrar?: string[];
  /** Closing reassurance, e.g. "Não achou? Dá pra ajustar depois." */
  footer?: string;
};

type Props = {
  content: HelpContent;
  visible: boolean;
  onClose: () => void;
};

/** Bottom sheet that explains where a piece of information comes from. */
export default function HelpSheet({ content, visible, onClose }: Props) {
  const insets = useSafeAreaInsets();

  return (
    <Modal visible={visible} transparent animationType="slide" onRequestClose={onClose}>
      <Pressable className="flex-1 justify-end" style={{ backgroundColor: "rgba(30,42,37,0.45)" }} onPress={onClose}>
        {/* Stop taps inside the sheet from closing it. */}
        <Pressable
          className="bg-surface rounded-t-sheet px-6 pt-3"
          style={{ paddingBottom: insets.bottom + 22, maxHeight: "80%" }}
          onPress={(e) => e.stopPropagation()}
        >
          <View className="self-center w-10 h-1 rounded-pill bg-line-soft mb-5" />

          <View className="flex-row items-start gap-3 mb-4">
            <Text className="flex-1 font-bold text-xl text-ink">{content.title}</Text>
            <Pressable onPress={onClose} hitSlop={10} className="mt-0.5 active:opacity-60">
              <X size={22} color={colors.text.secondary} strokeWidth={2.4} />
            </Pressable>
          </View>

          <ScrollView showsVerticalScrollIndicator={false}>
            <Text className="text-md text-muted font-regular">{content.body}</Text>

            {content.ondeEncontrar?.length ? (
              <View className="mt-5 gap-3">
                {content.ondeEncontrar.map((item, i) => (
                  <View key={i} className="flex-row gap-3">
                    <View
                      className="w-6 h-6 rounded-full items-center justify-center mt-0.5"
                      style={{ backgroundColor: colors.primary[100] }}
                    >
                      <Text className="text-xs font-bold text-primary-700">{i + 1}</Text>
                    </View>
                    <Text className="flex-1 text-sm text-ink font-regular leading-[20px]">{item}</Text>
                  </View>
                ))}
              </View>
            ) : null}

            {content.footer ? (
              <View className="mt-5 rounded-lg px-4 py-3.5" style={{ backgroundColor: colors.primary[25] }}>
                <Text className="text-sm text-primary-700 font-medium leading-[19px]">{content.footer}</Text>
              </View>
            ) : null}
          </ScrollView>

          <Pressable
            onPress={onClose}
            className="h-[52px] rounded-pill items-center justify-center mt-6 active:scale-[0.99]"
            style={{ backgroundColor: colors.primary[700] }}
          >
            <Text className="font-bold text-base" style={{ color: colors.surface }}>
              Entendi
            </Text>
          </Pressable>
        </Pressable>
      </Pressable>
    </Modal>
  );
}
