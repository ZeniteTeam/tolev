import { LucideIcon } from "lucide-react-native";
import { TextInput, TextInputProps, View } from "react-native";
import { colors } from "../theme";

type Props = {
  icon?: LucideIcon;
  value?: string;
  onChangeText?: (v: string) => void;
  placeholder?: string;
  secureTextEntry?: boolean;
  keyboardType?: TextInputProps["keyboardType"];
  autoCapitalize?: TextInputProps["autoCapitalize"];
  autoComplete?: TextInputProps["autoComplete"];
};

export default function Field({
  icon: Icon,
  value,
  onChangeText,
  placeholder,
  secureTextEntry,
  keyboardType,
  autoCapitalize,
  autoComplete,
}: Props) {
  return (
    <View className="h-12 rounded-[36px] bg-primary-50 flex-row items-center px-[18px] gap-2.5">
      {Icon && <Icon size={20} color={colors.coral[500]} strokeWidth={2} />}
      <TextInput
        className="flex-1 font-regular text-base text-ink py-0"
        value={value}
        onChangeText={onChangeText}
        placeholder={placeholder}
        placeholderTextColor={colors.text.secondary}
        secureTextEntry={secureTextEntry}
        keyboardType={keyboardType}
        autoCapitalize={autoCapitalize}
        autoComplete={autoComplete}
      />
    </View>
  );
}
