import { Pressable, ScrollView, StyleSheet, Text, View } from "react-native";
import { colors } from "../theme";

export type BankId =
  | "all" | "nubank" | "itau" | "bradesco" | "santander"
  | "bb" | "caixa" | "inter" | "c6" | "picpay";

const BANKS: { id: BankId; name: string; short: string; color: string; textColor: string }[] = [
  { id: "all", name: "Todos", short: "✱", color: colors.primary[700], textColor: "#fff" },
  { id: "nubank", name: "Nubank", short: "N", color: "#820AD1", textColor: "#fff" },
  { id: "itau", name: "Itaú", short: "i", color: "#EC7000", textColor: "#fff" },
  { id: "bradesco", name: "Bradesco", short: "B", color: "#CC092F", textColor: "#fff" },
  { id: "santander", name: "Santander", short: "S", color: "#EC0000", textColor: "#fff" },
  { id: "bb", name: "B. Brasil", short: "BB", color: "#FFEF38", textColor: "#003876" },
  { id: "caixa", name: "Caixa", short: "C", color: "#0070AF", textColor: "#FF9E1B" },
  { id: "inter", name: "Inter", short: "I", color: "#FF7A00", textColor: "#fff" },
  { id: "c6", name: "C6", short: "C6", color: "#111111", textColor: "#fff" },
  { id: "picpay", name: "PicPay", short: "P", color: "#11C76F", textColor: "#fff" },
];

type Props = {
  active: BankId;
  onChange: (b: BankId) => void;
};

export default function BankFilter({ active, onChange }: Props) {
  return (
    <ScrollView
      horizontal
      showsHorizontalScrollIndicator={false}
      contentContainerStyle={styles.scroll}
    >
      {BANKS.map((b) => (
        <Pressable key={b.id} onPress={() => onChange(b.id)} style={styles.item}>
          <View
            style={[
              styles.logo,
              { backgroundColor: b.color },
              active === b.id && { borderWidth: 2, borderColor: colors.coral[500] },
            ]}
          >
            <Text style={{ color: b.textColor, fontFamily: "PlusJakartaSans_700Bold", fontSize: 14 }}>
              {b.short}
            </Text>
          </View>
          <Text style={[styles.name, active === b.id && { color: colors.primary[700], fontFamily: "PlusJakartaSans_700Bold" }]}>
            {b.name}
          </Text>
        </Pressable>
      ))}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  scroll: {
    paddingHorizontal: 4,
    paddingVertical: 8,
    gap: 14,
  },
  item: {
    alignItems: "center",
    gap: 6,
    width: 56,
  },
  logo: {
    width: 44,
    height: 44,
    borderRadius: 22,
    alignItems: "center",
    justifyContent: "center",
  },
  name: {
    fontSize: 11,
    color: colors.text.secondary,
    fontFamily: "PlusJakartaSans_500Medium",
  },
});
