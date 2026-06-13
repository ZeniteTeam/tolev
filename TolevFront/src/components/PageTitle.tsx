import { StyleSheet, Text, View } from "react-native";
import { colors } from "../theme";

type Props = {
  title: string;
  sub?: string;
};

export default function PageTitle({ title, sub }: Props) {
  return (
    <View style={styles.wrap}>
      <Text style={styles.title}>{title}</Text>
      {sub && <Text style={styles.sub}>{sub}</Text>}
    </View>
  );
}

const styles = StyleSheet.create({
  wrap: {
    marginBottom: 22,
  },
  title: {
    fontFamily: "PlusJakartaSans_700Bold",
    fontSize: 24,
    color: colors.text.primary,
    lineHeight: 28,
  },
  sub: {
    fontFamily: "PlusJakartaSans_400Regular",
    fontSize: 13,
    color: colors.text.secondary,
    marginTop: 4,
  },
});
