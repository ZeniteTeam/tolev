import {
  Book,
  ChevronRight,
  ChevronUp,
  Dumbbell,
  Edit3,
  Film,
  Heart,
  Home,
  Lightbulb,
  Plus,
  Shield,
  ShoppingCart,
  Tag,
  Tags,
  Truck,
  Tv,
  Wifi,
  type LucideIcon,
} from "lucide-react-native";
import { useState } from "react";
import { Pressable, Text, View } from "react-native";
import { BankFilter, GiftedDonut, Ring } from "../../../components";
import type { BankId } from "../../../components/BankFilter";
import { colors, shadows } from "../../../theme";
import { useSpendingByCategory } from "../../analysis/hooks/useSpendingByCategory";
import {
  percentualClassificado,
  toCategoriaView,
} from "../../analysis/utils/categoria-view";
import { CategoriaGastosDetailed } from "../../menu/components/CategoriaGastos";

type Cat = { label: string; icon: LucideIcon; color: string };

const ANALISE_CATS: Cat[] = [
  { label: "Moradia", icon: Home, color: "#03643F" },
  { label: "Alimentação", icon: ShoppingCart, color: "#1CA474" },
  { label: "Transporte", icon: Truck, color: "#30BCB3" },
  { label: "Lazer", icon: Film, color: "#FE6F50" },
  { label: "Pets", icon: Heart, color: "#9B6BDF" },
  { label: "Educação", icon: Book, color: "#3E7BFA" },
];

type Props = { onOpenCategorias?: () => void };

export default function AnaliseTab({ onOpenCategorias }: Props) {
  const [bank, setBank] = useState<BankId>("all");

  return (
    <View className="pt-[18px]">
      <Text className="text-[13px] text-ink font-semibold mb-2 pl-1">
        Filtrar por banco
      </Text>
      <BankFilter active={bank} onChange={setBank} />

      <DistribuicaoCard />
      <SuasCategoriasButton onOpen={onOpenCategorias} />
      <GastosFixosCard />
      <DicaTolevCard />
      <ClassificacaoCard cats={ANALISE_CATS} />
    </View>
  );
}

function CardSkeleton({ height }: { height: number }) {
  return <View className="bg-[#F1F5F3] rounded-[12px]" style={{ height }} />;
}

function DistribuicaoCard() {
  const { data, isLoading } = useSpendingByCategory();
  const categorias = data ? toCategoriaView(data.pontos) : [];

  return (
    <View className="bg-surface rounded-[18px] p-5 mb-3.5" style={shadows.card}>
      <View className="flex-row justify-between items-start mb-4">
        <View>
          <Text className="font-bold text-[16px] text-ink">
            Distribuição de gastos
          </Text>
          <Text className="text-[12px] text-muted mt-0.5 font-regular">
            Agrupado por categoria · último mês
          </Text>
        </View>
        <View className="flex-row items-center gap-1.5">
          <Edit3 size={14} color={colors.teal[500]} strokeWidth={2} />
          <Text className="text-[12px] font-bold text-teal-500">
            Recategorizar
          </Text>
        </View>
      </View>

      {/* Vazio aqui é estado permanente e legítimo, não erro: quem nunca
          lançou uma despesa não tem o que distribuir. */}
      {isLoading ? (
        <CardSkeleton height={220} />
      ) : categorias.length === 0 ? (
        <Text className="text-[13px] text-muted font-regular">
          Nenhuma despesa lançada neste mês. Registre um gasto para ver a
          distribuição.
        </Text>
      ) : (
        <CategoriaGastosDetailed categorias={categorias} />
      )}
    </View>
  );
}

function SuasCategoriasButton({ onOpen }: { onOpen?: () => void }) {
  return (
    <Pressable
      onPress={onOpen}
      className="bg-surface rounded-[18px] p-[18px] mb-3.5 flex-row items-center gap-3.5 active:opacity-90"
      style={shadows.card}
    >
      <View className="w-11 h-11 rounded-[12px] bg-primary-100 items-center justify-center">
        <Tags size={21} color={colors.primary[700]} strokeWidth={2} />
      </View>
      <View className="flex-1">
        <Text className="font-bold text-[15px] text-ink">Suas categorias</Text>
        <Text className="text-[12px] text-muted mt-0.5 font-regular">
          Criar, editar ou remover categorias
        </Text>
      </View>
      <ChevronRight size={20} color={colors.text.secondary} strokeWidth={2} />
    </Pressable>
  );
}

function GastosFixosCard() {
  const fixos = [
    { label: "Aluguel", valor: 1200, icon: Home },
    { label: "Internet", valor: 120, icon: Wifi },
    { label: "Academia", valor: 90, icon: Dumbbell },
    { label: "Streaming", valor: 50, icon: Tv },
    { label: "Seguro auto", valor: 180, icon: Shield },
  ];
  const total = fixos.reduce((s, f) => s + f.valor, 0);
  return (
    <View className="bg-surface rounded-[18px] p-5 mb-3.5" style={shadows.card}>
      <View className="flex-row justify-between items-start mb-4">
        <View>
          <Text className="font-bold text-[16px] text-ink">Gastos fixos</Text>
          <Text className="text-[12px] text-muted mt-0.5 font-regular">
            Recorrentes todo mês
          </Text>
        </View>
        <View className="items-end">
          <Text className="text-[11px] text-muted font-regular">Total</Text>
          <Text className="font-bold text-[16px] text-primary-700">
            R$ {total.toLocaleString("pt-BR")}
          </Text>
        </View>
      </View>
      {fixos.map((f, i) => {
        const Icon = f.icon;
        return (
          <View
            key={f.label}
            className="flex-row items-center gap-3 py-[11px]"
            style={
              i !== fixos.length - 1
                ? { borderBottomWidth: 1, borderBottomColor: "#F1F5F3" }
                : undefined
            }
          >
            <View className="w-[34px] h-[34px] rounded-[9px] bg-primary-100 items-center justify-center">
              <Icon size={16} color={colors.primary[700]} strokeWidth={2} />
            </View>
            <Text className="flex-1 text-[14px] font-medium text-ink">
              {f.label}
            </Text>
            <Text className="text-[14px] font-bold text-ink">
              R$ {f.valor.toLocaleString("pt-BR")}
            </Text>
          </View>
        );
      })}
    </View>
  );
}

function DicaTolevCard() {
  return (
    <View className="bg-primary-25 rounded-[18px] px-5 py-[18px] mb-3.5 flex-row gap-3.5 items-start">
      <Ring style={{ backgroundColor: "#fff" }}>
        <Lightbulb size={22} color={colors.primary[700]} strokeWidth={2} />
      </Ring>
      <View className="flex-1">
        <Text className="text-[12px] text-primary-700 font-bold tracking-[0.4px]">
          DICA TOLEV
        </Text>
        <Text className="text-[14px] text-ink leading-5 mt-1.5 font-regular">
          Seus gastos com{" "}
          <Text className="text-coral-500 font-bold">Lazer</Text> subiram 18%
          neste mês. Classificar as transações pendentes deixa sua análise mais
          precisa.
        </Text>
      </View>
    </View>
  );
}

function ClassificacaoCard({ cats }: { cats: Cat[] }) {
  const [open, setOpen] = useState(false);
  const [picker, setPicker] = useState<number | null>(null);
  const [assigned, setAssigned] = useState<Record<number, string>>({});

  // Mesma query key do DistribuicaoCard, então é a mesma requisição: os dois
  // cards leem uma resposta só e não têm como se contradizer na tela.
  const { data, isLoading } = useSpendingByCategory();
  const pct = data
    ? percentualClassificado(data.totalTransacoes, data.transacoesSemCategoria)
    : null;
  const semCategoria = data?.transacoesSemCategoria ?? 0;

  // MOCK: a lista de pendentes abaixo continua fixa. Recategorizar exige um
  // PATCH /transactions/{id} que ainda não existe.
  const transacoes = [
    { desc: "PIX recebido — João", valor: "R$ 120,00" },
    { desc: "Compra — 4412****", valor: "R$ 78,90" },
    { desc: "Débito automático", valor: "R$ 45,00" },
  ];
  const catBy = (label: string) => cats.find((c) => c.label === label);
  const assign = (idx: number, label: string) => {
    setAssigned((a) => ({ ...a, [idx]: label }));
    setPicker(null);
  };

  return (
    <View className="bg-surface rounded-[18px] p-5 mb-3.5" style={shadows.card}>
      <Text className="font-bold text-[16px] text-ink mb-4">
        Classificação de gastos
      </Text>

      {isLoading ? (
        <CardSkeleton height={108} />
      ) : pct === null ? (
        <Text className="text-[13px] text-muted font-regular">
          Nenhuma despesa lançada neste mês para classificar.
        </Text>
      ) : (
        <View className="flex-row items-center gap-5">
          <GiftedDonut
            data={[
              { value: pct, color: colors.teal[500] },
              { value: 100 - pct, color: "#FEC9BB" },
            ]}
            size={108}
            stroke={12}
            center={
              <View className="items-center">
                <Text className="text-[22px] font-bold text-ink">{pct}%</Text>
                <Text className="text-[10px] text-muted font-regular">
                  classificado
                </Text>
              </View>
            }
          />
          <View className="flex-1 gap-3">
            <View className="flex-row items-center gap-2.5">
              <View className="w-2.5 h-2.5 rounded-[3px] bg-teal-500" />
              <Text className="flex-1 text-[13px] text-ink font-regular">
                Classificados
              </Text>
              <Text className="text-[13px] font-bold text-teal-500">
                {pct}%
              </Text>
            </View>
            <View className="flex-row items-center gap-2.5">
              <View
                className="w-2.5 h-2.5 rounded-[3px]"
                style={{ backgroundColor: "#FEC9BB" }}
              />
              <Text className="flex-1 text-[13px] text-ink font-regular">
                Não classificados
              </Text>
              <Text className="text-[13px] font-bold text-coral-500">
                {100 - pct}%
              </Text>
            </View>
            <Text className="text-[11px] text-muted leading-[15px] font-regular">
              {semCategoria === 0
                ? "Todas as suas transações estão categorizadas."
                : `${semCategoria} ${semCategoria === 1 ? "transação precisa" : "transações precisam"} da sua ajuda para serem categorizadas.`}
            </Text>
          </View>
        </View>
      )}

      <Pressable
        onPress={() => setOpen((o) => !o)}
        className="mt-4 h-11 rounded-pill bg-primary-100 flex-row items-center justify-center gap-2 active:opacity-90"
      >
        {open ? (
          <ChevronUp size={16} color={colors.primary[700]} strokeWidth={2} />
        ) : (
          <Tag size={16} color={colors.primary[700]} strokeWidth={2} />
        )}
        <Text className="font-bold text-[14px] text-primary-700">
          {open ? "Ocultar pendentes" : "Classificar manualmente"}
        </Text>
      </Pressable>

      {open && (
        <View className="mt-3.5 gap-2.5">
          {transacoes.map((t, i) => {
            const cat = assigned[i] ? catBy(assigned[i]) : null;
            const CatIcon = cat?.icon;
            return (
              <View
                key={i}
                className="bg-primary-50 rounded-[12px] px-3.5 py-3"
              >
                <View className="flex-row items-center gap-3">
                  <View className="flex-1">
                    <Text
                      className="text-[13px] font-semibold text-ink"
                      numberOfLines={1}
                    >
                      {t.desc}
                    </Text>
                    <Text className="text-[12px] text-muted mt-0.5 font-regular">
                      {t.valor}
                    </Text>
                  </View>
                  {cat && CatIcon ? (
                    <Pressable
                      onPress={() => setPicker(picker === i ? null : i)}
                      className="flex-row items-center gap-1.5 px-3 py-1.5 rounded-pill"
                      style={{ backgroundColor: cat.color }}
                    >
                      <CatIcon size={13} color="#fff" strokeWidth={2} />
                      <Text className="text-[12px] font-bold text-white">
                        {cat.label}
                      </Text>
                    </Pressable>
                  ) : (
                    <Pressable
                      onPress={() => setPicker(picker === i ? null : i)}
                      className="flex-row items-center gap-1.5 px-3 py-1.5 rounded-pill bg-coral-500"
                    >
                      {picker === i ? (
                        <ChevronUp size={13} color="#fff" strokeWidth={2} />
                      ) : (
                        <Plus size={13} color="#fff" strokeWidth={2} />
                      )}
                      <Text className="text-[12px] font-bold text-white">
                        Categoria
                      </Text>
                    </Pressable>
                  )}
                </View>

                {picker === i && (
                  <View
                    className="flex-row flex-wrap gap-2 mt-3 pt-3"
                    style={{ borderTopWidth: 1, borderTopColor: "#E1EAE5" }}
                  >
                    {cats.map((c) => {
                      const active = assigned[i] === c.label;
                      return (
                        <Pressable
                          key={c.label}
                          onPress={() => assign(i, c.label)}
                          className="flex-row items-center gap-1.5 px-3 py-1.5 rounded-pill"
                          style={{
                            backgroundColor: active ? c.color : "#fff",
                            borderWidth: 1.5,
                            borderColor: c.color,
                          }}
                        >
                          <View
                            className="w-2 h-2 rounded-[3px]"
                            style={{
                              backgroundColor: active ? "#fff" : c.color,
                            }}
                          />
                          <Text
                            className="text-[12px] font-semibold"
                            style={{
                              color: active ? "#fff" : colors.text.primary,
                            }}
                          >
                            {c.label}
                          </Text>
                        </Pressable>
                      );
                    })}
                  </View>
                )}
              </View>
            );
          })}
        </View>
      )}
    </View>
  );
}
