import { Pressable, StyleSheet, Text, View } from "react-native";
import { colors } from "../theme";

type Item = { key: string; label: string };

type Props = {
  items: Item[];
  active: string;
  onChange: (k: string) => void;
};

export default function TabsUnderline({ items, active, onChange }: Props) {
  return (
    <View style={styles.row}>
      {items.map((it) => {
        const isActive = active === it.key;
        return (
          <Pressable key={it.key} onPress={() => onChange(it.key)} style={styles.tab}>
            <Text style={[styles.label, isActive && styles.activeLabel]}>{it.label}</Text>
            <View style={[styles.underline, isActive && styles.activeUnderline]} />
          </Pressable>
        );
      })}
    </View>
  );
}

const styles = StyleSheet.create({
  row: {
    flexDirection: "row",
    borderBottomWidth: 1,
    borderBottomColor: "#F1F5F3",
  },
  tab: {
    flex: 1,
    alignItems: "center",
    paddingVertical: 12,
    gap: 8,
  },
  label: {
    fontFamily: "PlusJakartaSans_600SemiBold",
    fontSize: 15,
    color: colors.text.secondary,
  },
  activeLabel: {
    color: colors.primary[700],
  },
  underline: {
    height: 3,
    width: 36,
    borderRadius: 999,
    backgroundColor: "transparent",
  },
  activeUnderline: {
    backgroundColor: colors.coral[500],
  },
});
