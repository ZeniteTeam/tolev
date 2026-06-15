import { Plus } from "lucide-react-native";
import { Pressable } from "react-native";
import { shadows } from "../theme";

type Props = {
  onPress?: () => void;
  bottom?: number;
};

export default function FAB({ onPress, bottom = 96 }: Props) {
  return (
    <Pressable
      onPress={onPress}
      className="absolute right-6 w-14 h-14 rounded-full bg-coral-500 items-center justify-center active:scale-95"
      style={[shadows.cta, { bottom }]}
    >
      <Plus size={26} color="#fff" strokeWidth={2.5} />
    </Pressable>
  );
}
