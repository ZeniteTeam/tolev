import { ArrowLeft, Bell, HelpCircle, User } from "lucide-react-native";
import { Pressable, StyleSheet, Text, View } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { colors } from "../theme";

type HomeHeaderProps = {
  greeting?: string;
  onAvatar?: () => void;
  onNotifications?: () => void;
};

export function HomeHeader({ greeting = "Maria", onAvatar, onNotifications }: HomeHeaderProps) {
  const insets = useSafeAreaInsets();
  return (
    <View style={[styles.home, { paddingTop: insets.top + 12 }]}>
      <Pressable onPress={onAvatar} style={styles.avatar}>
        <User size={22} color="#fff" strokeWidth={2} />
      </Pressable>
      <View style={{ flex: 1, marginLeft: 12 }}>
        <Text style={styles.hi}>Olá,</Text>
        <Text style={styles.name}>{greeting}</Text>
      </View>
      <View style={styles.rightRow}>
        <Pressable onPress={onNotifications} style={styles.pill}>
          <Bell size={18} color="#fff" strokeWidth={2} />
        </Pressable>
        <Pressable style={styles.pill}>
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
    <View style={[styles.slim, { paddingTop: insets.top + 12 }]}>
      {onBack ? (
        <Pressable onPress={onBack} style={styles.pill}>
          <ArrowLeft size={20} color="#fff" strokeWidth={2} />
        </Pressable>
      ) : (
        <Pressable onPress={onProfile} style={styles.pill}>
          <User size={20} color="#fff" strokeWidth={2} />
        </Pressable>
      )}
      <View style={styles.rightRow}>
        <Pressable onPress={onNotifications} style={styles.pill}>
          <Bell size={18} color="#fff" strokeWidth={2} />
        </Pressable>
        <Pressable style={styles.pill}>
          <HelpCircle size={18} color="#fff" strokeWidth={2} />
        </Pressable>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  home: {
    backgroundColor: colors.primary[700],
    paddingBottom: 18,
    paddingHorizontal: 20,
    flexDirection: "row",
    alignItems: "center",
  },
  slim: {
    backgroundColor: colors.primary[700],
    paddingBottom: 18,
    paddingHorizontal: 20,
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
  },
  avatar: {
    width: 40,
    height: 40,
    borderRadius: 20,
    backgroundColor: "rgba(255,255,255,0.18)",
    alignItems: "center",
    justifyContent: "center",
  },
  pill: {
    width: 36,
    height: 36,
    borderRadius: 18,
    backgroundColor: "rgba(255,255,255,0.18)",
    alignItems: "center",
    justifyContent: "center",
  },
  rightRow: {
    flexDirection: "row",
    gap: 8,
  },
  hi: {
    color: "rgba(255,255,255,0.82)",
    fontFamily: "PlusJakartaSans_400Regular",
    fontSize: 13,
  },
  name: {
    color: "#fff",
    fontFamily: "PlusJakartaSans_700Bold",
    fontSize: 20,
  },
});
