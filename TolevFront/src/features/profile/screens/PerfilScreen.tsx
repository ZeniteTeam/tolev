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
import { Pressable, StyleSheet, Text, View } from "react-native";
import { Ring, Screen } from "../../../components";
import { colors, shadows } from "../../../theme";

type Props = {
  onLogout?: () => void;
};

export default function PerfilScreen({ onLogout }: Props) {
  const navigation = useNavigation<any>();
  return (
    <Screen bottomPad={64}>
      <View style={styles.avatarBlock}>
        <View style={[styles.avatar, shadows.card]}>
          <User size={56} color={colors.primary[700]} strokeWidth={1.6} />
        </View>
        <Text style={styles.name}>Maria Silva</Text>
        <Text style={styles.email}>maria.silva@email.com</Text>
        <View style={styles.premium}>
          <Star size={14} color={colors.primary[700]} fill={colors.primary[700]} />
          <Text style={styles.premiumText}>Premium</Text>
        </View>
      </View>

      <View style={{ flexDirection: "row", gap: 10, marginBottom: 22 }}>
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
        style={styles.logout}
      >
        <LogOut size={18} color={colors.coral[500]} strokeWidth={2} />
        <Text style={styles.logoutText}>Sair da conta</Text>
      </Pressable>
    </Screen>
  );
}

function StatTile({ value, label }: { value: string; label: string }) {
  return (
    <View style={[styles.statTile, shadows.card]}>
      <Text style={styles.statValue}>{value}</Text>
      <Text style={styles.statLabel}>{label}</Text>
    </View>
  );
}

function Group({ title, children }: { title: string; children: React.ReactNode }) {
  return (
    <View style={{ marginBottom: 22 }}>
      <Text style={styles.groupTitle}>{title}</Text>
      <View style={[styles.groupBody, shadows.card]}>{children}</View>
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
    <View style={[styles.navRow, !last && styles.navRowDivider]}>
      <Ring style={{ width: 36, height: 36, borderRadius: 18 }}>
        <Icon size={18} color={colors.primary[700]} strokeWidth={2} />
      </Ring>
      <View style={{ flex: 1 }}>
        <Text style={styles.navTitle}>{title}</Text>
        {sub && <Text style={styles.navSub}>{sub}</Text>}
      </View>
      <ChevronRight size={18} color={colors.text.secondary} strokeWidth={2} />
    </View>
  );
}

const styles = StyleSheet.create({
  avatarBlock: { alignItems: "center", paddingTop: 8, paddingBottom: 22 },
  avatar: {
    width: 96,
    height: 96,
    borderRadius: 48,
    backgroundColor: colors.primary[100],
    alignItems: "center",
    justifyContent: "center",
    marginBottom: 14,
  },
  name: { fontFamily: "PlusJakartaSans_700Bold", fontSize: 22, color: colors.text.primary },
  email: { fontSize: 13, color: colors.text.secondary, marginTop: 4, fontFamily: "PlusJakartaSans_400Regular" },
  premium: {
    flexDirection: "row",
    alignItems: "center",
    gap: 6,
    backgroundColor: colors.primary[100],
    paddingHorizontal: 14,
    paddingVertical: 6,
    borderRadius: 999,
    marginTop: 10,
  },
  premiumText: { color: colors.primary[700], fontFamily: "PlusJakartaSans_600SemiBold", fontSize: 12 },
  statTile: { flex: 1, backgroundColor: "#fff", borderRadius: 14, paddingVertical: 14, paddingHorizontal: 8, alignItems: "center" },
  statValue: { fontFamily: "PlusJakartaSans_700Bold", fontSize: 22, color: colors.primary[700] },
  statLabel: { fontSize: 11, color: colors.text.secondary, marginTop: 4, fontFamily: "PlusJakartaSans_400Regular" },
  groupTitle: {
    fontSize: 11,
    color: colors.text.secondary,
    textTransform: "uppercase",
    letterSpacing: 0.6,
    fontFamily: "PlusJakartaSans_600SemiBold",
    marginBottom: 8,
    paddingLeft: 4,
  },
  groupBody: {
    backgroundColor: "#fff",
    borderRadius: 16,
    overflow: "hidden",
  },
  navRow: { flexDirection: "row", alignItems: "center", gap: 14, paddingVertical: 14, paddingHorizontal: 16 },
  navRowDivider: { borderBottomWidth: 1, borderBottomColor: "#F1F5F3" },
  navTitle: { fontFamily: "PlusJakartaSans_600SemiBold", fontSize: 15, color: colors.text.primary },
  navSub: { fontSize: 12, color: colors.text.secondary, marginTop: 2, fontFamily: "PlusJakartaSans_400Regular" },
  logout: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
    gap: 8,
    paddingVertical: 14,
  },
  logoutText: { color: colors.coral[500], fontFamily: "PlusJakartaSans_600SemiBold", fontSize: 15 },
});
