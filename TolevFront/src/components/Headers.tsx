import { ArrowLeft, Bell, HelpCircle, User } from "lucide-react-native";
import { Pressable, Text, View } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { useAuthStore } from "../store/authStore";

type HomeHeaderProps = {
  /** Sobrescreve o nome do usuário logado; use só em telas de exemplo. */
  greeting?: string;
  onAvatar?: () => void;
  onNotifications?: () => void;
};

/** Só o primeiro nome: o cabeçalho tem uma linha e o nome completo estoura. */
function primeiroNome(nome: string): string {
  return nome.trim().split(/\s+/)[0];
}

export function HomeHeader({ greeting, onAvatar, onNotifications }: HomeHeaderProps) {
  const insets = useSafeAreaInsets();
  const user = useAuthStore((s) => s.user);
  const nome = greeting ?? user?.nome ?? user?.nomeUsuario ?? "";
  const saudacao = nome ? primeiroNome(nome) : "";
  return (
    <View className="bg-primary-700 pb-[18px] px-5 flex-row items-center" style={{ paddingTop: insets.top + 12 }}>
      <Pressable onPress={onAvatar} className="w-10 h-10 rounded-full bg-white/[0.18] items-center justify-center">
        <User size={22} color="#fff" strokeWidth={2} />
      </Pressable>
      <View className="flex-1 ml-3">
        <Text className="text-white/[0.82] font-regular text-sm">Olá,</Text>
        <Text className="text-white font-bold text-lg" numberOfLines={1}>
          {saudacao || "seja bem-vindo"}
        </Text>
      </View>
      <View className="flex-row gap-2">
        <Pressable onPress={onNotifications} className="w-9 h-9 rounded-full bg-white/[0.18] items-center justify-center">
          <Bell size={18} color="#fff" strokeWidth={2} />
        </Pressable>
        <Pressable className="w-9 h-9 rounded-full bg-white/[0.18] items-center justify-center">
          <HelpCircle size={18} color="#fff" strokeWidth={2} />
        </Pressable>
      </View>
    </View>
  );
}

type SlimProps = {
  onBack?: () => void;
  onProfile?: () => void;
  onNotifications?: () => void;
};

export function SlimHeader({ onBack, onProfile, onNotifications }: SlimProps) {
  const insets = useSafeAreaInsets();
  return (
    <View
      className="bg-primary-700 pb-[18px] px-5 flex-row items-center justify-between"
      style={{ paddingTop: insets.top + 12 }}
    >
      {onBack ? (
        <Pressable onPress={onBack} className="w-9 h-9 rounded-full bg-white/[0.18] items-center justify-center">
          <ArrowLeft size={20} color="#fff" strokeWidth={2} />
        </Pressable>
      ) : (
        <Pressable onPress={onProfile} className="w-9 h-9 rounded-full bg-white/[0.18] items-center justify-center">
          <User size={20} color="#fff" strokeWidth={2} />
        </Pressable>
      )}
      <View className="flex-row gap-2">
        <Pressable onPress={onNotifications} className="w-9 h-9 rounded-full bg-white/[0.18] items-center justify-center">
          <Bell size={18} color="#fff" strokeWidth={2} />
        </Pressable>
        <Pressable className="w-9 h-9 rounded-full bg-white/[0.18] items-center justify-center">
          <HelpCircle size={18} color="#fff" strokeWidth={2} />
        </Pressable>
      </View>
    </View>
  );
}
