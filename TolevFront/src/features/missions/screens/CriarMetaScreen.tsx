import { zodResolver } from "@hookform/resolvers/zod";
import { useNavigation } from "@react-navigation/native";
import { Calendar, Gift, Star } from "lucide-react-native";
import { Controller, useForm } from "react-hook-form";
import { Alert, Pressable, Text, TextInput, View } from "react-native";
import { Button, Field, PageTitle, Screen } from "../../../components";
import { useAuthStore } from "../../../store/authStore";
import { colors, shadows } from "../../../theme";
import type { MetaRequest } from "../../../types/meta";
import { parseCurrencyToNumber } from "../../../util/currency";
import { brDateToIso } from "../../../util/date";
import { CATEGORIAS } from "../constants/categorias";
import { useCreateMeta } from "../hooks/useCreateMeta";
import { metaFormSchema, type MetaFormValues } from "../schema/metaSchema";
import { use, useState } from "react";

export default function CriarMetaScreen() {
  const navigation = useNavigation<any>();
  const userId = useAuthStore((s) => s.userId);
  const createMeta = useCreateMeta();

  const {
    control,
    handleSubmit,
    formState: { errors },
  } = useForm<MetaFormValues>({
    resolver: zodResolver(metaFormSchema),
    defaultValues: {
      nomeMeta: "",
      categoria: "GERAL",
      motivacaoMeta: "",
      valorMeta: "",
      dataLimite: undefined,
      valorDedicado: "",
      recompensa: "",
      commitment: 3,
    },
  });

  function onSubmit(values: MetaFormValues) {
    if (userId == null) {
      Alert.alert("Sessão", "Usuário não identificado. Faça login novamente.");
      return;
    }

    const request: MetaRequest = {
      idUsuario: userId,
      nomeMeta: values.nomeMeta.trim(),
      valorMeta: parseCurrencyToNumber(values.valorMeta),
      valorDedicado: parseCurrencyToNumber(values.valorDedicado),
      status: "ATIVA",
      tipo: "ECONOMIA",
      categoria: values.categoria,
      dataLimite: values.dataLimite,
      recompensa: values.recompensa?.trim() ? values.recompensa.trim() : null,
      motivacaoMeta: values.motivacaoMeta?.trim() ? values.motivacaoMeta.trim() : null,
    };

    createMeta.mutate(request, {
      onSuccess: () => navigation.goBack(),
      onError: () =>
        Alert.alert("Erro", "Não foi possível criar a meta. Tente novamente."),
    });
  }

  const [valorTotal, setValorTotal] = useState('');
  const [data, setData] = useState('');

  const formatDateText = (text: string) => {
    let clean = text.replace(/\D/g, '');

    if (clean.length >= 2) {
      let day = parseInt(clean.slice(0, 2), 10);
      if (day > 31) day = 31;
      if (day === 0) day = 1;
      clean = String(day).padStart(2, '0') + clean.slice(2);
    }

    if (clean.length >= 4) {
      let month = parseInt(clean.slice(2, 4), 10);
      if (month > 12) month = 12;
      if (month === 0) month = 1;
      clean = clean.slice(0, 2) + String(month).padStart(2, '0') + clean.slice(4);
    }

    // 4. Structural positioning slice (DD/MM/YYYY)
    if (clean.length <= 2) return clean;
    if (clean.length <= 4) return `${clean.slice(0, 2)}/${clean.slice(2)}`;
    return `${clean.slice(0, 2)}/${clean.slice(2, 4)}/${clean.slice(4, 8)}`;
  };

  return (
    <Screen bottomPad={48}>
      <PageTitle
        title="Criar nova meta"
        sub="Defina seu próximo objetivo e o caminho até ele"
      />

      <FormSection label="Nome da meta" error={errors.nomeMeta?.message}>
        <Controller
          control={control}
          name="nomeMeta"
          render={({ field: { value, onChange } }) => (
            <Field
              placeholder="Ex.: Comprar um carro"
              value={value}
              onChangeText={onChange}
            />
          )}
        />
      </FormSection>

      <FormSection label="Categoria">
        <Controller
          control={control}
          name="categoria"
          render={({ field: { value, onChange } }) => (
            <View className="flex-row flex-wrap gap-2">
              {CATEGORIAS.map((c) => {
                const isActive = value === c.id;
                const Icon = c.icon;
                return (
                  <Pressable
                    key={c.id}
                    onPress={() => onChange(c.id)}
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
          )}
        />
      </FormSection>

      <FormSection
        label="Por que essa meta é importante?"
        error={errors.motivacaoMeta?.message}
      >
        <View className="bg-white rounded-lg p-3.5 min-h-[90px]" style={shadows.card}>
          <Controller
            control={control}
            name="motivacaoMeta"
            render={({ field: { value, onChange } }) => (
              <TextInput
                multiline
                placeholder="Ex.: Quero proporcionar mais conforto para minha família"
                placeholderTextColor={colors.text.secondary}
                value={value}
                onChangeText={onChange}
                className="font-regular text-md text-ink min-h-[70px]"
                style={{ textAlignVertical: "top" }}
              />
            )}
          />
        </View>
      </FormSection>

      <FormSection label="Valor total da meta" error={errors.valorMeta?.message}>
        <Controller
          control={control}
          name="valorMeta"
          render={({ field: { value, onChange } }) => (
            <Field
              placeholder="R$ 0,00"
              value={value}
              onChangeText={(v) => {
                setValorTotal(v)
                onChange(v)
              }}
              keyboardType="numeric"
            />
          )}
        />
      </FormSection>

      <FormSection label="Data limite" error={errors.dataLimite?.message}>
        <Controller
          control={control}
          name="dataLimite"
          render={({ field: { value, onChange } }) => {

            const displayValue = value instanceof Date
              ? value.toLocaleDateString('pt-BR')
              : value;

            return (
              <Field
                icon={Calendar}
                placeholder="DD/MM/AAAA"
                value={displayValue}
                onChangeText={(v) => {
                  const format = formatDateText(v);

                  if (format.length === 10) {
                    const [day, month, year] = format.split('/').map(Number);
                    const dateObject = new Date(year, month - 1, day);
                    onChange(dateObject);
                  } else {
                    onChange(format);
                  }

                  setData(format);
                }}
                keyboardType="numeric"
              />
            );
          }}
        />
      </FormSection>

      <FormSection label="Valor a ser dedicado na meta" error={errors.valorMeta?.message}>
        <Controller
          control={control}
          name="valorDedicado"
          render={({ field: { value, onChange } }) => (
            <Field
              placeholder="R$ 0,00"
              value={value}
              onChangeText={onChange}
              keyboardType="numeric"
            />
          )}
        />
        {/* <Text>
          Para concluir essa meta dentro do prazo de {data}
        </Text> */}
      </FormSection>

      <FormSection label="Recompensa ao concluir (opcional)" error={errors.recompensa?.message}>
        <Controller
          control={control}
          name="recompensa"
          render={({ field: { value, onChange } }) => (
            <Field
              icon={Gift}
              placeholder="Ex.: Jantar especial em família"
              value={value}
              onChangeText={onChange}
            />
          )}
        />
      </FormSection>

      <View className="gap-2 mt-2">
        <Button
          variant="primary"
          onPress={createMeta.isPending ? undefined : handleSubmit(onSubmit)}
        >
          {createMeta.isPending ? "Criando..." : "Criar meta"}
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
  error,
  children,
}: {
  label: string;
  error?: string;
  children: React.ReactNode;
}) {
  return (
    <View className="mb-[18px]">
      <Text className="text-sm text-ink font-semibold mb-2 pl-1">{label}</Text>
      {children}
      {error ? (
        <Text className="text-[12px] text-coral-500 font-regular mt-1.5 pl-1">{error}</Text>
      ) : null}
    </View>
  );
}
