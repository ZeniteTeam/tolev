import { Pressable, View } from "react-native";
import { colors } from "../theme";

type Props = {
  count: number;
  active: number;
  onChange?: (i: number) => void;
};

export default function DotPagination({ count, active, onChange }: Props) {
  return (
    <View style={{ flexDirection: "row", justifyContent: "center", gap: 8, paddingVertical: 10 }}>
      {Array.from({ length: count }).map((_, i) => (
        <Pressable
          key={i}
          onPress={() => onChange?.(i)}
          style={{
            width: active === i ? 24 : 8,
            height: 8,
            borderRadius: 999,
            backgroundColor: active === i ? colors.coral[500] : "#D0D2D1",
          }}
        />
      ))}
    </View>
  );
}
