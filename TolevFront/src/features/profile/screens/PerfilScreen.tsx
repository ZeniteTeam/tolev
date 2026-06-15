import { useNavigation } from "@react-navigation/native";
import {
  Bell,
  ChevronRight,
  CreditCard,
  Globe,
  HelpCircle,
  Info,
  Lock,
  LogOut,
  MessageCircle,
  Shield,
  Star,
  User,
  type LucideIcon,
} from "lucide-react-native";
import { Pressable, Text, View } from "react-native";
import { Ring, Screen } from "../../../components";
import { colors, shadows } from "../../../theme";

type Props = {
  onLogout?: () => void;
};

export default function PerfilScreen({ onLogout }: Props) {
  const navigation = useNavigation<any>();
  return (
    <Screen bottomPad={64}>
      <View className="items-center pt-2 pb-[22px]">
        <View className="w-24 h-24 rounded-full bg-primary-100 items-center justify-center mb-3.5" style={shadows.card}>
          <User size={56} color={colors.primary[700]} strokeWidth={1.6} />
        </View>
        <Text className="font-bold text-[22px] text-ink">Maria Silva</Text>
        <Text className="text-sm text-muted mt-1 font-regular">maria.silva@email.com</Text>
        <View className="flex-row items-center gap-1.5 bg-primary-100 px-3.5 py-1.5 rounded-pill mt-2.5">
          <Star size={14} color={colors.primary[700]} fill={colors.primary[700]} />
          <Text className="text-primary-700 font-semibold text-[12px]">Premium</Text>
        </View>
      </View>

      <View className="flex-row gap-2.5 mb-[22px]">
        <StatTile value="720" label="Pontos" />
        <StatTile value="3" label="Metas ativas" />
        <StatTile value="7m" label="Para zerar" />
      </View>

      <Group title="Conta">
        <NavRow icon={User} title="Dados pessoais" sub="Nome, e-mail, telefone" />
        <NavRow icon={Lock} title="Senha & segurança" sub="Trocar senha, biometria" />
        <NavRow icon={CreditCard} title="Open Finance" sub="Contas conectadas" last />
      </Group>

      <Group title="Preferências">
        <NavRow icon={Bell} title="Notificações" sub="Alertas, lembretes" />
        <NavRow icon={Shield} title="Privacidade" sub="Dados, permissões" />
        <NavRow icon={Globe} title="Idioma" sub="Português (Brasil)" last />
      </Group>

      <Group title="Suporte">
        <NavRow icon={HelpCircle} title="Central de ajuda" />
        <NavRow icon={MessageCircle} title="Fale conosco" />
        <NavRow icon={Info} title="Sobre o Tolev" last />
      </Group>

      <Pressable
        onPress={() => {
          onLogout?.();
          navigation.getParent()?.reset({ index: 0, routes: [{ name: "Login" }] });
        }}
        className="flex-row items-center justify-center gap-2 py-3.5"
      >
        <LogOut size={18} color={colors.coral[500]} strokeWidth={2} />
        <Text className="text-coral-500 font-semibold text-[15px]">Sair da conta</Text>
      </Pressable>
    </Screen>
  );
}

function StatTile({ value, label }: { value: string; label: string }) {
  return (
    <View className="flex-1 bg-white rounded-[14px] py-3.5 px-2 items-center" style={shadows.card}>
      <Text className="font-bold text-[22px] text-primary-700">{value}</Text>
      <Text className="text-[11px] text-muted mt-1 font-regular">{label}</Text>
    </View>
  );
}

function Group({ title, children }: { title: string; children: React.ReactNode }) {
  return (
    <View className="mb-[22px]">
      <Text className="text-[11px] text-muted uppercase tracking-[0.6px] font-semibold mb-2 pl-1">{title}</Text>
      <View className="bg-white rounded-lg overflow-hidden" style={shadows.card}>{children}</View>
    </View>
  );
}

function NavRow({
  icon: Icon,
  title,
  sub,
  last,
}: {
  icon: LucideIcon;
  title: string;
  sub?: string;
  last?: boolean;
}) {
  return (
    <View className={`flex-row items-center gap-3.5 py-3.5 px-4 ${!last ? "border-b border-b-[#F1F5F3]" : ""}`}>
      <Ring style={{ width: 36, height: 36, borderRadius: 18 }}>
        <Icon size={18} color={colors.primary[700]} strokeWidth={2} />
      </Ring>
      <View className="flex-1">
        <Text className="font-semibold text-[15px] text-ink">{title}</Text>
        {sub && <Text className="text-[12px] text-muted mt-0.5 font-regular">{sub}</Text>}
      </View>
      <ChevronRight size={18} color={colors.text.secondary} strokeWidth={2} />
    </View>
  );
}
