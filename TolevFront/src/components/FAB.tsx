import { Plus } from "lucide-react-native";
import { Pressable, StyleSheet } from "react-native";
import { colors, shadows } from "../theme";

type Props = {
  onPress?: () => void;
  bottom?: number;
};

export default function FAB({ onPress, bottom = 96 }: Props) {
  return (
    <Pressable
      onPress={onPress}
      style={({ pressed }) => [
        styles.fab,
        shadows.cta,
        { bottom },
        pressed && { transform: [{ scale: 0.95 }] },
      ]}
    >
      <Plus size={26} color="#fff" strokeWidth={2.5} />
    </Pressable>
  );
}

const styles = StyleSheet.create({
  fab: {
    position: "absolute",
    right: 24,
    width: 56,
    height: 56,
    borderRadius: 28,
    backgroundColor: colors.coral[500],
    alignItems: "center",
    justifyContent: "center",
  },
});
