import { useNavigation } from "@react-navigation/native";
import {
  Book,
  Check,
  Edit2,
  Film,
  Heart,
  Home,
  Lock,
  ShoppingCart,
  Tag,
  Trash2,
  Truck,
  type LucideIcon,
} from "lucide-react-native";
import { useState } from "react";
import { Pressable, Text, TextInput, View } from "react-native";
import { Button, PageTitle, Screen } from "../../../components";
import { colors, shadows } from "../../../theme";

type Cat = { label: string; icon: LucideIcon; color: string };

const DEFAULTS: Cat[] = [
  { label: "Moradia", icon: Home, color: "#03643F" },
  { label: "Alimentação", icon: ShoppingCart, color: "#1CA474" },
  { label: "Transporte", icon: Truck, color: "#30BCB3" },
  { label: "Lazer", icon: Film, color: "#FE6F50" },
];

const PALETTE = ["#9B6BDF", "#3E7BFA", "#EC7000", "#CC092F", "#0070AF", "#6B7D75"];

export default function CategoriasScreen() {
  const navigation = useNavigation<any>();
  const [custom, setCustom] = useState<Cat[]>([
    { label: "Pets", icon: Heart, color: "#9B6BDF" },
    { label: "Educação", icon: Book, color: "#3E7BFA" },
  ]);
  const [adding, setAdding] = useState(false);
  const [nova, setNova] = useState("");

  const addCat = () => {
    const name = nova.trim();
    if (!name) return;
    setCustom((c) => [...c, { label: name, icon: Tag, color: PALETTE[c.length % PALETTE.length] }]);
    setNova("");
    setAdding(false);
  };
  const removeCat = (idx: number) => setCustom((c) => c.filter((_, i) => i !== idx));

  return (
    <Screen bottomPad={40}>
      <PageTitle title="Suas categorias" sub="Crie, edite ou remova categorias personalizadas" />

      <Text className="text-[11px] text-muted font-bold tracking-[0.5px] mx-0.5 mb-2.5">
        PADRÃO DO APP
      </Text>
      <View className="bg-surface rounded-[18px] px-[18px] mb-5" style={shadows.card}>
        {DEFAULTS.map((c, i) => {
          const Icon = c.icon;
          return (
            <View
              key={c.label}
              className="flex-row items-center gap-3 py-[13px]"
              style={i !== DEFAULTS.length - 1 ? { borderBottomWidth: 1, borderBottomColor: "#F1F5F3" } : undefined}
            >
              <View className="w-9 h-9 rounded-[9px] items-center justify-center" style={{ backgroundColor: c.color }}>
                <Icon size={17} color="#fff" strokeWidth={2} />
              </View>
              <Text className="flex-1 text-[15px] font-medium text-ink">{c.label}</Text>
              <Lock size={16} color={colors.text.secondary} strokeWidth={2} />
            </View>
          );
        })}
      </View>

      <Text className="text-[11px] text-muted font-bold tracking-[0.5px] mx-0.5 mb-2.5">
        PERSONALIZADAS
      </Text>
      <View className="bg-surface rounded-[18px] px-[18px] mb-4" style={shadows.card}>
        {custom.length === 0 && (
          <Text className="py-[18px] text-[13px] text-muted text-center font-regular">
            Nenhuma categoria personalizada ainda.
          </Text>
        )}
        {custom.map((c, i) => {
          const Icon = c.icon;
          return (
            <View
              key={c.label + i}
              className="flex-row items-center gap-3 py-[13px]"
              style={i !== custom.length - 1 ? { borderBottomWidth: 1, borderBottomColor: "#F1F5F3" } : undefined}
            >
              <View className="w-9 h-9 rounded-[9px] items-center justify-center" style={{ backgroundColor: c.color }}>
                <Icon size={17} color="#fff" strokeWidth={2} />
              </View>
              <Text className="flex-1 text-[15px] font-medium text-ink">{c.label}</Text>
              <View className="flex-row gap-4 items-center">
                <Edit2 size={17} color={colors.teal[500]} strokeWidth={2} />
                <Pressable onPress={() => removeCat(i)}>
                  <Trash2 size={17} color={colors.coral[500]} strokeWidth={2} />
                </Pressable>
              </View>
            </View>
          );
        })}
      </View>

      {adding ? (
        <View className="flex-row gap-2 items-center">
          <TextInput
            autoFocus
            value={nova}
            onChangeText={setNova}
            onSubmitEditing={addCat}
            placeholder="Nome da categoria"
            placeholderTextColor={colors.text.secondary}
            className="flex-1 h-[52px] rounded-pill bg-primary-50 px-5 font-medium text-base text-ink"
          />
          <Pressable
            onPress={addCat}
            className="w-[52px] h-[52px] rounded-full bg-primary-700 items-center justify-center"
          >
            <Check size={20} color="#fff" strokeWidth={2.5} />
          </Pressable>
        </View>
      ) : (
        <Button variant="outline" onPress={() => setAdding(true)}>
          + Nova categoria
        </Button>
      )}

      <Button variant="ghost" onPress={() => navigation.goBack()} style={{ marginTop: 10 }}>
        Voltar
      </Button>
    </Screen>
  );
}
