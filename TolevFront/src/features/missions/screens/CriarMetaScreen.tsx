import { useNavigation } from "@react-navigation/native";
import {
  Calendar,
  Car,
  Gift,
  GraduationCap,
  Heart,
  Home,
  Monitor,
  Plane,
  Star,
  Target,
  type LucideIcon,
} from "lucide-react-native";
import { useState } from "react";
import { Pressable, StyleSheet, Text, TextInput, View } from "react-native";
import { Button, Field, PageTitle, Screen } from "../../../components";
import { colors, shadows } from "../../../theme";

type CategoriaId =
  | "target"
  | "car"
  | "home"
  | "plane"
  | "monitor"
  | "graduation"
  | "heart"
  | "gift";

const CATEGORIAS: { id: CategoriaId; name: string; icon: LucideIcon }[] = [
  { id: "target", name: "Geral", icon: Target },
  { id: "car", name: "Veículo", icon: Car },
  { id: "home", name: "Casa", icon: Home },
  { id: "plane", name: "Viagem", icon: Plane },
  { id: "monitor", name: "Tecnologia", icon: Monitor },
  { id: "graduation", name: "Educação", icon: GraduationCap },
  { id: "heart", name: "Saúde", icon: Heart },
  { id: "gift", name: "Outros", icon: Gift },
];

const COMMITMENT_LABELS = ["Baixo", "Leve", "Moderado", "Alto", "Excelente"];

export default function CriarMetaScreen() {
  const navigation = useNavigation<any>();
  const [titulo, setTitulo] = useState("");
  const [motivo, setMotivo] = useState("");
  const [valor, setValor] = useState("");
  const [dataFim, setDataFim] = useState("");
  const [categoria, setCategoria] = useState<CategoriaId>("target");
  const [recompensa, setRecompensa] = useState("");
  const [commitment, setCommitment] = useState(3);

  return (
    <Screen bottomPad={48}>
      <PageTitle
        title="Criar nova meta"
        sub="Defina seu próximo objetivo e o caminho até ele"
      />

      <FormSection label="Nome da meta">
        <Field
          placeholder="Ex.: Comprar um carro"
          value={titulo}
          onChangeText={setTitulo}
        />
      </FormSection>

      <FormSection label="Categoria">
        <View style={styles.catGrid}>
          {CATEGORIAS.map((c) => {
            const isActive = categoria === c.id;
            const Icon = c.icon;
            return (
              <Pressable
                key={c.id}
                onPress={() => setCategoria(c.id)}
                style={[
                  styles.catItem,
                  isActive ? styles.catItemActive : shadows.card,
                ]}
              >
                <Icon
                  size={22}
                  color={isActive ? colors.primary[700] : colors.text.secondary}
                  strokeWidth={2}
                />
                <Text
                  style={[
                    styles.catLabel,
                    isActive && { color: colors.primary[700] },
                  ]}
                >
                  {c.name}
                </Text>
              </Pressable>
            );
          })}
        </View>
      </FormSection>

      <FormSection label="Por que essa meta é importante?">
        <View style={[styles.textarea, shadows.card]}>
          <TextInput
            multiline
            placeholder="Ex.: Quero proporcionar mais conforto para minha família"
            placeholderTextColor={colors.text.secondary}
            value={motivo}
            onChangeText={setMotivo}
            style={styles.textareaInput}
          />
        </View>
      </FormSection>

      <FormSection label="Valor total">
        <Field
          placeholder="R$ 0,00"
          value={valor}
          onChangeText={setValor}
          keyboardType="numeric"
        />
      </FormSection>

      <FormSection label="Data limite">
        <Field
          icon={Calendar}
          placeholder="DD/MM/AAAA"
          value={dataFim}
          onChangeText={setDataFim}
        />
      </FormSection>

      <FormSection label="Nível de comprometimento mensal">
        <View style={[styles.starCard, shadows.card]}>
          <View style={{ flexDirection: "row", gap: 6 }}>
            {[1, 2, 3, 4, 5].map((n) => (
              <Pressable key={n} onPress={() => setCommitment(n)}>
                <Star
                  size={28}
                  color={colors.coral[500]}
                  fill={n <= commitment ? colors.coral[500] : "transparent"}
                  strokeWidth={2}
                />
              </Pressable>
            ))}
          </View>
          <Text style={styles.commitmentLabel}>
            {COMMITMENT_LABELS[commitment - 1]}
          </Text>
        </View>
      </FormSection>

      <FormSection label="Recompensa ao concluir (opcional)">
        <Field
          icon={Gift}
          placeholder="Ex.: Jantar especial em família"
          value={recompensa}
          onChangeText={setRecompensa}
        />
      </FormSection>

      <View style={{ gap: 8, marginTop: 8 }}>
        <Button variant="primary" onPress={() => navigation.goBack()}>
          Criar meta
        </Button>
        <Button variant="ghost" onPress={() => navigation.goBack()}>
          Cancelar
        </Button>
      </View>
    </Screen>
  );
}

function FormSection({
  label,
  children,
}: {
  label: string;
  children: React.ReactNode;
}) {
  return (
    <View style={{ marginBottom: 18 }}>
      <Text style={styles.fieldLabel}>{label}</Text>
      {children}
    </View>
  );
}

const styles = StyleSheet.create({
  fieldLabel: {
    fontSize: 13,
    color: colors.text.primary,
    fontFamily: "PlusJakartaSans_600SemiBold",
    marginBottom: 8,
    paddingLeft: 4,
  },
  catGrid: {
    flexDirection: "row",
    flexWrap: "wrap",
    gap: 8,
  },
  catItem: {
    width: "23%",
    backgroundColor: "#fff",
    borderRadius: 14,
    paddingVertical: 14,
    paddingHorizontal: 6,
    alignItems: "center",
  },
  catItemActive: {
    backgroundColor: colors.primary[100],
    borderWidth: 2,
    borderColor: colors.primary[700],
  },
  catLabel: {
    fontSize: 11,
    marginTop: 6,
    fontFamily: "PlusJakartaSans_600SemiBold",
    color: colors.text.secondary,
  },
  textarea: {
    backgroundColor: "#fff",
    borderRadius: 16,
    padding: 14,
    minHeight: 90,
  },
  textareaInput: {
    fontFamily: "PlusJakartaSans_400Regular",
    fontSize: 15,
    color: colors.text.primary,
    textAlignVertical: "top",
    minHeight: 70,
  },
  starCard: {
    backgroundColor: "#fff",
    borderRadius: 16,
    paddingVertical: 18,
    paddingHorizontal: 20,
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
  },
  commitmentLabel: {
    color: colors.coral[500],
    fontFamily: "PlusJakartaSans_700Bold",
    fontSize: 13,
  },
});
