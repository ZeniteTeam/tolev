import { Pressable, StyleSheet, Text, View } from "react-native";
import { colors } from "../theme";

type Props = {
  active: string;
  onChange: (k: string) => void;
  options?: string[];
};

export default function PeriodFilter({ active, onChange, options = ["1s", "1m", "3m", "6m", "1a", "3a"] }: Props) {
  return (
    <View style={styles.row}>
      {options.map((k) => {
        const isActive = active === k;
        return (
          <Pressable
            key={k}
            onPress={() => onChange(k)}
            style={[styles.item, isActive && styles.active]}
          >
            <Text style={[styles.label, isActive && { color: "#fff" }]}>{k}</Text>
          </Pressable>
        );
      })}
    </View>
  );
}

const styles = StyleSheet.create({
  row: {
    flexDirection: "row",
    backgroundColor: "#F1F5F3",
    borderRadius: 999,
    padding: 4,
    gap: 2,
  },
  item: {
    flex: 1,
    paddingVertical: 8,
    borderRadius: 999,
    alignItems: "center",
  },
  active: {
    backgroundColor: colors.coral[500],
  },
  label: {
    fontSize: 13,
    color: colors.text.secondary,
    fontFamily: "PlusJakartaSans_600SemiBold",
  },
});
