import { useState } from "react";
import {
    ActivityIndicator,
    KeyboardAvoidingView,
    Modal,
    Platform,
    Pressable,
    Text,
    View,
} from "react-native";
import { Button, Field } from "../../../components";
import { shadows } from "../../../theme";
import { parseCurrencyToNumber } from "../../../util/currency";
import { useAddValueToMeta } from "../hooks/useAddValueToMeta";

type Props = {
  visible: boolean;
  metaId: number;
  onClose: () => void;
};

export default function InsertValueModal({ visible, metaId, onClose }: Props) {
  const [rawValue, setRawValue] = useState("");
  const { mutate, isPending, isError, reset } = useAddValueToMeta();

  const handleClose = () => {
    setRawValue("");
    reset();
    onClose();
  };

  const handleAdd = () => {
    const value = parseCurrencyToNumber(rawValue);
    if (value <= 0) return;

    mutate({ id: metaId, value }, { onSuccess: handleClose });
  };

  return (
    <Modal
      visible={visible}
      transparent
      animationType="fade"
      onRequestClose={handleClose}
    >
      <Pressable
        className="flex-1 bg-black/40 justify-center px-6"
        onPress={handleClose}
      >
        <KeyboardAvoidingView
          behavior={Platform.OS === "ios" ? "padding" : undefined}
        >
          <Pressable
            className="bg-white rounded-[18px] p-6 gap-4"
            style={shadows.deep}
            onPress={(e) => e.stopPropagation()}
          >
            <View className="gap-1">
              <Text className="font-bold text-[18px] text-ink">
                Adicionar valor a meta
              </Text>
              <Text className="text-sm text-muted font-regular">
                Insira um valor ao qual você adicionou a meta recentemente
              </Text>
            </View>

            <Field
              value={rawValue}
              onChangeText={setRawValue}
              placeholder="R$ 0,00"
              keyboardType="numeric"
            />

            {isError && (
              <Text className="text-coral-500 text-[13px] font-regular">
                Não foi possível adicionar o valor. Tente novamente.
              </Text>
            )}

            <Button
              variant="primary"
              onPress={handleAdd}
              style={{ width: "100%" }}
            >
              {isPending ? <ActivityIndicator color="#fff" /> : "Adicionar"}
            </Button>
          </Pressable>
        </KeyboardAvoidingView>
      </Pressable>
    </Modal>
  );
}
