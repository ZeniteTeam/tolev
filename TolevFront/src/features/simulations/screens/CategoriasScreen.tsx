import { useNavigation } from "@react-navigation/native";
import { Check, Edit2, Lock, Trash2, X } from "lucide-react-native";
import { useState } from "react";
import { ActivityIndicator, Alert, Pressable, Text, TextInput, View } from "react-native";
import { Button, PageTitle, Screen, Stagger } from "../../../components";
import { colors, shadows } from "../../../theme";
import type { CategoriaResponse, TipoCategoriaGasto } from "../../../types/transacao";
import { getApiErrorMessage } from "../../../util/apiError";
import { CATEGORIA_COR_PADRAO, categoriaIcon } from "../../transactions/constants/transacoes";
import {
  useCreateCategoria,
  useDeleteCategoria,
  useUpdateCategoria,
} from "../../transactions/hooks/useCategoriaMutations";
import { useCategorias } from "../../transactions/hooks/useCategorias";

/** Cores oferecidas às categorias novas, na ordem em que vão sendo usadas. */
const PALETTE = ["#9B6BDF", "#3E7BFA", "#EC7000", "#CC092F", "#0070AF", "#30BCB3"];

const TIPOS: { id: TipoCategoriaGasto; label: string }[] = [
  { id: "DESPESA", label: "Despesa" },
  { id: "RECEITA", label: "Receita" },
];

export default function CategoriasScreen() {
  const navigation = useNavigation<any>();
  // Sem filtro de tipo: aqui a pessoa administra o catálogo inteiro, não está
  // lançando nada.
  const { categorias, isPending, isError } = useCategorias();
  const criar = useCreateCategoria();
  const renomear = useUpdateCategoria();
  const remover = useDeleteCategoria();

  const [adding, setAdding] = useState(false);
  const [nova, setNova] = useState("");
  const [novoTipo, setNovoTipo] = useState<TipoCategoriaGasto>("DESPESA");
  const [editandoId, setEditandoId] = useState<number | null>(null);
  const [rascunho, setRascunho] = useState("");

  const doSistema = categorias.filter((c) => c.origem === "SISTEMA");
  const doUsuario = categorias.filter((c) => c.origem === "USUARIO");

  const falhou = (err: unknown, fallback: string) =>
    Alert.alert("Erro", getApiErrorMessage(err, fallback));

  function addCat() {
    const nome = nova.trim();
    if (!nome || criar.isPending) return;
    criar.mutate(
      { nome, tipo: novoTipo, cor: PALETTE[doUsuario.length % PALETTE.length] },
      {
        onSuccess: () => {
          setNova("");
          setNovoTipo("DESPESA");
          setAdding(false);
        },
        onError: (err) => falhou(err, "Não foi possível criar a categoria."),
      },
    );
  }

  function salvarNome(c: CategoriaResponse) {
    const nome = rascunho.trim();
    if (!nome || nome === c.nome) {
      setEditandoId(null);
      return;
    }
    renomear.mutate(
      { id: c.id, nome, cor: c.cor, tipo: (c.tipo ?? "DESPESA") as TipoCategoriaGasto },
      {
        onSuccess: () => setEditandoId(null),
        onError: (err) => falhou(err, "Não foi possível renomear a categoria."),
      },
    );
  }

  function confirmarRemocao(c: CategoriaResponse) {
    Alert.alert(
      `Remover ${c.nome}?`,
      "Ela sai da lista de escolha. Os gastos já classificados nela continuam como estão.",
      [
        { text: "Cancelar", style: "cancel" },
        {
          text: "Remover",
          style: "destructive",
          onPress: () =>
            remover.mutate(c.id, {
              onError: (err) => falhou(err, "Não foi possível remover a categoria."),
            }),
        },
      ],
    );
  }

  if (isPending) {
    return (
      <Screen bottomPad={40}>
        <PageTitle title="Suas categorias" sub="Crie, edite ou remova categorias personalizadas" />
        <View className="py-16 items-center">
          <ActivityIndicator color={colors.primary[700]} />
        </View>
      </Screen>
    );
  }

  return (
    <Screen bottomPad={40}>
      <PageTitle title="Suas categorias" sub="Crie, edite ou remova categorias personalizadas" />

      {isError && (
        <View className="bg-surface rounded-[18px] px-5 py-6 mb-4" style={shadows.card}>
          <Text className="text-sm text-muted font-regular text-center leading-[19px]">
            Não foi possível carregar suas categorias. Verifique sua conexão e tente de novo.
          </Text>
        </View>
      )}

      <Text className="text-[11px] text-muted font-bold tracking-[0.5px] mx-0.5 mb-2.5">
        PADRÃO DO APP
      </Text>
      <Stagger className="bg-surface rounded-[18px] px-[18px] mb-5" style={shadows.card}>
        {doSistema.map((c, i) => (
          <Linha key={c.id} categoria={c} last={i === doSistema.length - 1}>
            <Lock size={16} color={colors.text.secondary} strokeWidth={2} />
          </Linha>
        ))}
      </Stagger>

      <Text className="text-[11px] text-muted font-bold tracking-[0.5px] mx-0.5 mb-2.5">
        PERSONALIZADAS
      </Text>
      <Stagger className="bg-surface rounded-[18px] px-[18px] mb-4" style={shadows.card}>
        {doUsuario.length === 0 && (
          <Text className="py-[18px] text-[13px] text-muted text-center font-regular">
            Nenhuma categoria personalizada ainda.
          </Text>
        )}
        {doUsuario.map((c, i) => (
          <Linha
            key={c.id}
            categoria={c}
            last={i === doUsuario.length - 1}
            editando={editandoId === c.id}
            rascunho={rascunho}
            onChangeRascunho={setRascunho}
            onSubmitRascunho={() => salvarNome(c)}
          >
            {editandoId === c.id ? (
              <View className="flex-row gap-4 items-center">
                <Pressable onPress={() => setEditandoId(null)} hitSlop={8}>
                  <X size={17} color={colors.text.secondary} strokeWidth={2} />
                </Pressable>
                <Pressable onPress={() => salvarNome(c)} hitSlop={8}>
                  <Check size={18} color={colors.primary[700]} strokeWidth={2.5} />
                </Pressable>
              </View>
            ) : (
              <View className="flex-row gap-4 items-center">
                <Pressable
                  onPress={() => {
                    setEditandoId(c.id);
                    setRascunho(c.nome);
                  }}
                  hitSlop={8}
                >
                  <Edit2 size={17} color={colors.teal[500]} strokeWidth={2} />
                </Pressable>
                <Pressable onPress={() => confirmarRemocao(c)} hitSlop={8}>
                  <Trash2 size={17} color={colors.coral[500]} strokeWidth={2} />
                </Pressable>
              </View>
            )}
          </Linha>
        ))}
      </Stagger>

      {adding ? (
        <View className="gap-3">
          <View className="flex-row gap-2">
            {TIPOS.map((t) => {
              const active = novoTipo === t.id;
              return (
                <Pressable
                  key={t.id}
                  onPress={() => setNovoTipo(t.id)}
                  className="flex-1 h-11 rounded-pill items-center justify-center"
                  style={[
                    { backgroundColor: active ? colors.primary[100] : colors.surface },
                    active ? { borderWidth: 2, borderColor: colors.primary[700] } : shadows.card,
                  ]}
                >
                  <Text
                    className="text-[13px] font-semibold"
                    style={{ color: active ? colors.primary[700] : colors.text.secondary }}
                  >
                    {t.label}
                  </Text>
                </Pressable>
              );
            })}
          </View>
          <View className="flex-row gap-2 items-center">
            <TextInput
              autoFocus
              value={nova}
              onChangeText={setNova}
              onSubmitEditing={addCat}
              maxLength={40}
              placeholder="Nome da categoria"
              placeholderTextColor={colors.text.secondary}
              className="flex-1 h-[52px] rounded-pill bg-primary-50 px-5 font-medium text-base text-ink"
            />
            <Pressable
              onPress={addCat}
              disabled={criar.isPending || nova.trim().length === 0}
              className="w-[52px] h-[52px] rounded-full bg-primary-700 items-center justify-center"
              style={criar.isPending || nova.trim().length === 0 ? { opacity: 0.5 } : undefined}
            >
              {criar.isPending ? (
                <ActivityIndicator color="#fff" />
              ) : (
                <Check size={20} color="#fff" strokeWidth={2.5} />
              )}
            </Pressable>
          </View>
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

function Linha({
  categoria,
  last,
  editando,
  rascunho,
  onChangeRascunho,
  onSubmitRascunho,
  children,
}: {
  categoria: CategoriaResponse;
  last: boolean;
  editando?: boolean;
  rascunho?: string;
  onChangeRascunho?: (v: string) => void;
  onSubmitRascunho?: () => void;
  children: React.ReactNode;
}) {
  const Icon = categoriaIcon(categoria.nome);
  const cor = categoria.cor ?? CATEGORIA_COR_PADRAO;

  return (
    <View
      className="flex-row items-center gap-3 py-[13px]"
      style={last ? undefined : { borderBottomWidth: 1, borderBottomColor: "#F1F5F3" }}
    >
      <View
        className="w-9 h-9 rounded-[9px] items-center justify-center"
        style={{ backgroundColor: cor }}
      >
        <Icon size={17} color="#fff" strokeWidth={2} />
      </View>

      {editando ? (
        <TextInput
          autoFocus
          value={rascunho}
          onChangeText={onChangeRascunho}
          onSubmitEditing={onSubmitRascunho}
          maxLength={40}
          className="flex-1 text-[15px] font-medium text-ink"
        />
      ) : (
        <View className="flex-1">
          <Text className="text-[15px] font-medium text-ink">{categoria.nome}</Text>
          {categoria.tipo === "RECEITA" && (
            <Text className="text-[11px] text-muted font-regular">receita</Text>
          )}
        </View>
      )}

      {children}
    </View>
  );
}
