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
import { Pressable, Text, TextInput, View } from "react-native";
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
        <View className="flex-row flex-wrap gap-2">
          {CATEGORIAS.map((c) => {
            const isActive = categoria === c.id;
            const Icon = c.icon;
            return (
              <Pressable
                key={c.id}
                onPress={() => setCategoria(c.id)}
                className={`w-[23%] bg-white rounded-[14px] py-3.5 px-1.5 items-center ${isActive ? "bg-primary-100 border-2 border-primary-700" : ""}`}
                style={!isActive ? shadows.card : undefined}
              >
                <Icon
                  size={22}
                  color={isActive ? colors.primary[700] : colors.text.secondary}
                  strokeWidth={2}
                />
                <Text className={`text-[11px] mt-1.5 font-semibold ${isActive ? "text-primary-700" : "text-muted"}`}>
                  {c.name}
                </Text>
              </Pressable>
            );
          })}
        </View>
      </FormSection>

      <FormSection label="Por que essa meta é importante?">
        <View className="bg-white rounded-lg p-3.5 min-h-[90px]" style={shadows.card}>
          <TextInput
            multiline
            placeholder="Ex.: Quero proporcionar mais conforto para minha família"
            placeholderTextColor={colors.text.secondary}
            value={motivo}
            onChangeText={setMotivo}
            className="font-regular text-md text-ink min-h-[70px]"
            style={{ textAlignVertical: "top" }}
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
        <View className="bg-white rounded-lg py-[18px] px-5 flex-row items-center justify-between" style={shadows.card}>
          <View className="flex-row gap-1.5">
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
          <Text className="text-coral-500 font-bold text-sm">
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

      <View className="gap-2 mt-2">
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
    <View className="mb-[18px]">
      <Text className="text-sm text-ink font-semibold mb-2 pl-1">{label}</Text>
      {children}
    </View>
  );
}
