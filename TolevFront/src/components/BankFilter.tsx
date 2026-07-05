import { Pressable, ScrollView, Text, View } from "react-native";
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

/** Human-readable bank name for a given id (used when persisting a debt). */
export function bankName(id: BankId): string {
  return BANKS.find((b) => b.id === id)?.name ?? "Outros";
}

type Props = {
  active: BankId;
  onChange: (b: BankId) => void;
};

export default function BankFilter({ active, onChange }: Props) {
  return (
    <ScrollView
      horizontal
      showsHorizontalScrollIndicator={false}
      contentContainerClassName="px-1 py-2 gap-3.5"
    >
      {BANKS.map((b) => {
        const isActive = active === b.id;
        return (
          <Pressable key={b.id} onPress={() => onChange(b.id)} className="items-center gap-1.5 w-14">
            <View
              className="w-11 h-11 rounded-full items-center justify-center"
              style={[
                { backgroundColor: b.color },
                isActive && { borderWidth: 2, borderColor: colors.coral[500] },
              ]}
            >
              <Text style={{ color: b.textColor }} className="font-bold text-[14px]">
                {b.short}
              </Text>
            </View>
            <Text className={`text-xs ${isActive ? "text-primary-700 font-bold" : "text-muted font-medium"}`}>
              {b.name}
            </Text>
          </Pressable>
        );
      })}
    </ScrollView>
  );
}
