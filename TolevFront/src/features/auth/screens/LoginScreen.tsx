import { LinearGradient } from "expo-linear-gradient";
import { Lock, Mail } from "lucide-react-native";
import { useState } from "react";
import { KeyboardAvoidingView, Platform, Pressable, StyleSheet, Text, View } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { Button, Field } from "../../../components";
import { colors } from "../../../theme";

type Props = {
  onLogin: () => void;
};

export default function LoginScreen({ onLogin }: Props) {
  const insets = useSafeAreaInsets();
  const [email, setEmail] = useState("maria@tolev.app");
  const [senha, setSenha] = useState("senha-leve");

  return (
    <View style={styles.root}>
      <LinearGradient
        colors={[colors.primary[700], colors.primary[800]]}
        style={StyleSheet.absoluteFill}
      />
      <View style={[styles.hero, { paddingTop: insets.top + 80 }]}>
        <Text style={styles.wordmark}>TOLEV</Text>
        <Text style={styles.tagline}>Sua experiência financeira</Text>
      </View>

      <KeyboardAvoidingView
        behavior={Platform.OS === "ios" ? "padding" : undefined}
        style={styles.slab}
      >
        <Text style={styles.heading}>Login</Text>

        <View style={{ gap: 14 }}>
          <Field icon={Mail} placeholder="Email" value={email} onChangeText={setEmail} />
          <Field icon={Lock} placeholder="Senha" secureTextEntry value={senha} onChangeText={setSenha} />

          <Pressable>
            <Text style={styles.forgot}>Esqueci a senha</Text>
          </Pressable>

          <Button variant="primary" onPress={onLogin}>Entrar</Button>

          <View style={styles.divider}>
            <View style={styles.line} />
            <Text style={styles.or}>ou</Text>
            <View style={styles.line} />
          </View>

          <Button variant="outline" onPress={onLogin}>Criar conta</Button>
        </View>
      </KeyboardAvoidingView>
    </View>
  );
}

const styles = StyleSheet.create({
  root: {
    flex: 1,
    backgroundColor: colors.primary[600],
  },
  hero: {
    paddingHorizontal: 40,
    paddingBottom: 40,
  },
  wordmark: {
    fontFamily: "PlusJakartaSans_700Bold",
    fontSize: 64,
    letterSpacing: -1,
    color: "#fff",
    lineHeight: 70,
  },
  tagline: {
    color: "#fff",
    fontSize: 18,
    marginTop: 6,
    fontFamily: "PlusJakartaSans_400Regular",
  },
  slab: {
    flex: 1,
    backgroundColor: "#FEFEFE",
    borderTopLeftRadius: 32,
    borderTopRightRadius: 32,
    paddingHorizontal: 32,
    paddingTop: 40,
  },
  heading: {
    fontFamily: "PlusJakartaSans_700Bold",
    fontSize: 34,
    color: colors.primary[700],
    marginBottom: 36,
  },
  forgot: {
    textAlign: "right",
    fontSize: 14,
    color: colors.info[700],
    paddingHorizontal: 6,
    paddingBottom: 8,
    fontFamily: "PlusJakartaSans_500Medium",
  },
  divider: {
    flexDirection: "row",
    alignItems: "center",
    gap: 12,
    marginVertical: 6,
  },
  line: {
    flex: 1,
    height: 1,
    backgroundColor: "rgba(0,0,0,0.34)",
  },
  or: {
    color: colors.text.secondary,
    fontSize: 16,
    fontFamily: "PlusJakartaSans_400Regular",
  },
});
