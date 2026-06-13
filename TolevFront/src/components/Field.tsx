import { LucideIcon } from "lucide-react-native";
import { StyleSheet, TextInput, TextInputProps, View } from "react-native";
import { colors } from "../theme";

type Props = {
  icon?: LucideIcon;
  value?: string;
  onChangeText?: (v: string) => void;
  placeholder?: string;
  secureTextEntry?: boolean;
  keyboardType?: TextInputProps["keyboardType"];
};

export default function Field({ icon: Icon, value, onChangeText, placeholder, secureTextEntry, keyboardType }: Props) {
  return (
    <View style={styles.wrap}>
      {Icon && <Icon size={20} color={colors.coral[500]} strokeWidth={2} />}
      <TextInput
        style={styles.input}
        value={value}
        onChangeText={onChangeText}
        placeholder={placeholder}
        placeholderTextColor={colors.text.secondary}
        secureTextEntry={secureTextEntry}
        keyboardType={keyboardType}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  wrap: {
    height: 48,
    borderRadius: 36,
    backgroundColor: colors.primary[50],
    flexDirection: "row",
    alignItems: "center",
    paddingHorizontal: 18,
    gap: 10,
  },
  input: {
    flex: 1,
    fontFamily: "PlusJakartaSans_400Regular",
    fontSize: 16,
    color: colors.text.primary,
    paddingVertical: 0,
  },
});
