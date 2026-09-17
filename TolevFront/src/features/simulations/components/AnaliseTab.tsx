import {
  ChevronRight,
  ChevronUp,
  Dumbbell,
  Edit3,
  Home,
  Lightbulb,
  Plus,
  Shield,
  Tag,
  Tags,
  Tv,
  Wifi,
  type LucideIcon,
} from "lucide-react-native";
import { useState } from "react";
import { Alert, Pressable, Text, View } from "react-native";
import { GiftedDonut, PeriodFilter, Ring, Stagger } from "../../../components";
import { colors, motion, shadows } from "../../../theme";
import type { PeriodoGastos } from "../../../types/graphs";
import type { CategoriaResponse, TransacaoResponse } from "../../../types/transacao";
import { getApiErrorMessage } from "../../../util/apiError";
import { formatCurrencyBRL } from "../../../util/currency";
import { isoToBrDate } from "../../../util/date";
import { useGastosPorCategoria } from "../../analysis/hooks/useGastosPorCategoria";
import { CATEGORIA_COR_PADRAO } from "../../transactions/constants/transacoes";
import { useCategorias } from "../../transactions/hooks/useCategorias";
import {
  useClassificarTransacao,
  useTransacoesAClassificar,
} from "../../transactions/hooks/useClassificacao";
import BancoUsuarioFilter from "../../extrato/components/BancoUsuarioFilter";
import ImportarExtratoCard from "../../extrato/components/ImportarExtratoCard";
import SeusBancosCard from "../../extrato/components/SeusBancosCard";
import { useBancosUsuario } from "../../extrato/hooks/useBancosUsuario";
import {
  percentualClassificado,
  toCategoriaView,
} from "../../analysis/utils/categoria-view";
import {
  mesesDe,
  OPCOES_PERIODO,
  rotuloFaixa,
  rotuloPeriodo,
  type OpcaoPeriodo,
} from "../../analysis/utils/periodo";
import { CategoriaGastosDetailed } from "../../menu/components/CategoriaGastos";

type Props = {
  onOpenCategorias?: () => void;
  onImportarExtrato?: () => void;
  onAcompanharExtrato?: () => void;
};

export default function AnaliseTab({
  onOpenCategorias,
  onImportarExtrato,
  onAcompanharExtrato,
}: Props) {
  const { bancos } = useBancosUsuario();
  // `null` = todos. Se o banco filtrado sumir (importação desfeita), o filtro
  // volta sozinho para "todos" em vez de mostrar uma tela vazia sem explicação.
  const [idBanco, setIdBanco] = useState<number | null>(null);
  const bancoAtivo = bancos.some((b) => b.idBanco === idBanco) ? idBanco : null;
  // Um período para a aba inteira: dois cards lendo a mesma janela não têm como
  // se contradizer, e é a mesma query key — uma requisição só.
  const [periodo, setPeriodo] = useState<OpcaoPeriodo>("1m");
  const meses = mesesDe(periodo);

  return (
    <Stagger className="pt-[18px]">
      <BancoUsuarioFilter bancos={bancos} ativo={bancoAtivo} onChange={setIdBanco} />

      <View className="mb-3.5">
        <PeriodFilter
          active={periodo}
          onChange={(k) => setPeriodo(k as OpcaoPeriodo)}
          options={[...OPCOES_PERIODO]}
        />
      </View>

      <ImportarExtratoCard
        onImportar={() => onImportarExtrato?.()}
        onAcompanhar={() => onAcompanharExtrato?.()}
      />

      <DistribuicaoCard idBanco={bancoAtivo} meses={meses} />
      <SeusBancosCard />
      <SuasCategoriasButton onOpen={onOpenCategorias} />
      <GastosFixosCard />
      <DicaTolevCard />
      <ClassificacaoCard idBanco={bancoAtivo} meses={meses} />
    </Stagger>
  );
}

/**
 * Diz que janela o card está mostrando — e, quando ela não é a escolhida, que
 * isso foi uma troca e dá para desfazer.
 */
function LegendaPeriodo({
  meses,
  seguindoExtrato,
  faixaExtrato,
  onVerPeriodoEscolhido,
}: {
  meses: number;
  seguindoExtrato: boolean;
  faixaExtrato: { inicio: string; fim: string } | null;
  onVerPeriodoEscolhido: () => void;
}) {
  if (seguindoExtrato && faixaExtrato) {
    return (
      <View className="flex-row items-center gap-2 mt-0.5">
        <Text className="text-[12px] text-muted font-regular">
          {rotuloFaixa(faixaExtrato.inicio, faixaExtrato.fim)} · do seu extrato
        </Text>
        <Pressable onPress={onVerPeriodoEscolhido} hitSlop={6}>
          <Text className="text-[12px] font-bold text-teal-500">
            ver {rotuloPeriodo(meses)}
          </Text>
        </Pressable>
      </View>
    );
  }

  return (
    <Text className="text-[12px] text-muted mt-0.5 font-regular">
      Agrupado por categoria · {rotuloPeriodo(meses)}
    </Text>
  );
}

function CardSkeleton({ height }: { height: number }) {
  return <View className="bg-[#F1F5F3] rounded-[12px]" style={{ height }} />;
}

function DistribuicaoCard({ idBanco, meses }: { idBanco: number | null; meses: number }) {
  const { data, isLoading, seguindoExtrato, faixaExtrato, verPeriodoEscolhido } =
    useGastosPorCategoria(meses, idBanco);
  const categorias = data ? toCategoriaView(data.pontos) : [];

  return (
    <View className="bg-surface rounded-[18px] p-5 mb-3.5" style={shadows.card}>
      <View className="flex-row justify-between items-start mb-4">
        <View className="flex-1 pr-3">
          <Text className="font-bold text-[16px] text-ink">
            Distribuição de gastos
          </Text>
          <LegendaPeriodo
            meses={meses}
            seguindoExtrato={seguindoExtrato}
            faixaExtrato={faixaExtrato}
            onVerPeriodoEscolhido={verPeriodoEscolhido}
          />
        </View>
        <View className="flex-row items-center gap-1.5">
          <Edit3 size={14} color={colors.teal[500]} strokeWidth={2} />
          <Text className="text-[12px] font-bold text-teal-500">
            Recategorizar
          </Text>
        </View>
      </View>

      {/* Vazio aqui é estado permanente e legítimo, não erro: quem nunca
          lançou uma despesa não tem o que distribuir. Se houvesse gasto
          importado em outro período, o card já teria trocado de janela sozinho
          em vez de chegar aqui. */}
      {isLoading ? (
        <CardSkeleton height={220} />
      ) : categorias.length === 0 ? (
        <Text className="text-[13px] text-muted font-regular">
          Nenhuma despesa no {rotuloPeriodo(meses)}. Registre um gasto ou importe
          um extrato para ver a distribuição.
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
      <Stagger>
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
      </Stagger>
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

function ClassificacaoCard({ idBanco, meses }: { idBanco: number | null; meses: number }) {
  const [open, setOpen] = useState(false);
  const [picker, setPicker] = useState<number | null>(null);

  // Mesma query key do DistribuicaoCard, então é a mesma requisição: os dois
  // cards leem uma resposta só e não têm como se contradizer na tela — inclusive
  // quando a janela troca para a do extrato.
  const { data, isLoading, seguindoExtrato, faixaExtrato, verPeriodoEscolhido } =
    useGastosPorCategoria(meses, idBanco);
  const pct = data
    ? percentualClassificado(data.totalTransacoes, data.transacoesAClassificar)
    : null;
  const aClassificar = data?.transacoesAClassificar ?? 0;

  // A janela vem do próprio gráfico, já resolvida pelo backend. Recontar os
  // meses aqui daria outra faixa justamente no caso em que a tela trocou para o
  // período do extrato — e a lista contradiria o número logo acima dela.
  const janela: PeriodoGastos = data ? { inicio: data.inicio, fim: data.fim } : { meses };
  // Só busca depois de abrir: quem nunca abre a lista não paga por uma
  // requisição que ninguém vai ler.
  const { data: pendentes, isLoading: pendentesLoading } = useTransacoesAClassificar(
    janela,
    idBanco,
    open && data != null,
  );
  const { categorias } = useCategorias("DESPESA");
  const classificar = useClassificarTransacao();

  const fila = pendentes ?? [];

  function escolher(idTransacao: number, categoria: CategoriaResponse) {
    setPicker(null);
    classificar.mutate(
      { id: idTransacao, categoria },
      {
        onError: (err) =>
          Alert.alert(
            "Erro",
            getApiErrorMessage(err, "Não foi possível classificar essa transação."),
          ),
      },
    );
  }

  return (
    <View className="bg-surface rounded-[18px] p-5 mb-3.5" style={shadows.card}>
      <View className="mb-4">
        <Text className="font-bold text-[16px] text-ink">
          Classificação de gastos
        </Text>
        <LegendaPeriodo
          meses={meses}
          seguindoExtrato={seguindoExtrato}
          faixaExtrato={faixaExtrato}
          onVerPeriodoEscolhido={verPeriodoEscolhido}
        />
      </View>

      {isLoading ? (
        <CardSkeleton height={108} />
      ) : pct === null ? (
        <Text className="text-[13px] text-muted font-regular">
          Nenhuma despesa no {rotuloPeriodo(meses)} para classificar.
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
            delay={motion.stagger.step}
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
              {aClassificar === 0
                ? "Todas as suas transações estão categorizadas."
                : `${aClassificar} ${aClassificar === 1 ? "transação precisa" : "transações precisam"} da sua ajuda para serem categorizadas.`}
            </Text>
          </View>
        </View>
      )}

      {/* Aparece sempre que há despesa na janela, e não só quando existe transação
          sem categoria: um gasto em "Outros" está categorizado para a conta do
          donut e continua esperando uma decisão de verdade. */}
      {pct !== null && (
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
            {open
              ? "Ocultar lista"
              : aClassificar > 0
              ? "Classificar manualmente"
              : "Revisar classificação"}
          </Text>
        </Pressable>
      )}

      {open && (
        <View className="mt-3.5">
          {pendentesLoading ? (
            <CardSkeleton height={64} />
          ) : fila.length === 0 ? (
            <Text className="text-[13px] text-muted font-regular text-center py-3">
              Nada esperando decisão por aqui.
            </Text>
          ) : (
            <Stagger className="gap-2.5">
              {fila.map((t) => (
                <PendenteRow
                  key={t.id}
                  transacao={t}
                  categorias={categorias}
                  aberto={picker === t.id}
                  onToggle={() => setPicker(picker === t.id ? null : t.id)}
                  onEscolher={(c) => escolher(t.id, c)}
                />
              ))}
            </Stagger>
          )}
        </View>
      )}
    </View>
  );
}

/**
 * Uma despesa esperando decisão. O que identifica o gasto para quem está
 * classificando é o nome do lugar; a descrição do extrato, quando é tudo o que
 * existe, entra no lugar dele.
 *
 * A que já está em "Outros" mostra a categoria atual no botão, para a ação ler
 * como trocar e não como definir do zero.
 */
function PendenteRow({
  transacao,
  categorias,
  aberto,
  onToggle,
  onEscolher,
}: {
  transacao: TransacaoResponse;
  categorias: CategoriaResponse[];
  aberto: boolean;
  onToggle: () => void;
  onEscolher: (categoria: CategoriaResponse) => void;
}) {
  const titulo = transacao.nomeVendedor ?? transacao.descricao ?? "Transação sem descrição";
  const quando = isoToBrDate(transacao.dataTransacao);
  const atual = transacao.nomeCategoria;
  const corAtual = transacao.corCategoria ?? CATEGORIA_COR_PADRAO;
  const idAtual = transacao.idCategoriaGastoSistema ?? transacao.idCategoriaGastoUsuario;

  return (
    <View className="bg-primary-50 rounded-[12px] px-3.5 py-3">
      <View className="flex-row items-center gap-3">
        <View className="flex-1">
          <Text className="text-[13px] font-semibold text-ink" numberOfLines={1}>
            {titulo}
          </Text>
          <Text className="text-[12px] text-muted mt-0.5 font-regular">
            {formatCurrencyBRL(transacao.valor, true)}
            {quando ? ` · ${quando}` : ""}
          </Text>
        </View>
        <Pressable
          onPress={onToggle}
          className="flex-row items-center gap-1.5 px-3 py-1.5 rounded-pill active:opacity-90"
          style={{ backgroundColor: atual ? corAtual : colors.coral[500] }}
        >
          {aberto ? (
            <ChevronUp size={13} color="#fff" strokeWidth={2} />
          ) : atual ? (
            <Edit3 size={13} color="#fff" strokeWidth={2} />
          ) : (
            <Plus size={13} color="#fff" strokeWidth={2} />
          )}
          <Text className="text-[12px] font-bold text-white" numberOfLines={1}>
            {atual ?? "Categoria"}
          </Text>
        </Pressable>
      </View>

      {aberto && (
        <View
          className="flex-row flex-wrap gap-2 mt-3 pt-3"
          style={{ borderTopWidth: 1, borderTopColor: "#E1EAE5" }}
        >
          {categorias.length === 0 ? (
            <Text className="text-[12px] text-muted font-regular">
              Nenhuma categoria de despesa cadastrada.
            </Text>
          ) : (
            categorias.map((c) => {
              const cor = c.cor ?? CATEGORIA_COR_PADRAO;
              const ativa = c.id === idAtual && origemDa(transacao) === c.origem;
              return (
                <Pressable
                  key={`${c.origem}-${c.id}`}
                  onPress={() => onEscolher(c)}
                  className="flex-row items-center gap-1.5 px-3 py-1.5 rounded-pill active:opacity-80"
                  style={{
                    backgroundColor: ativa ? cor : "#fff",
                    borderWidth: 1.5,
                    borderColor: cor,
                  }}
                >
                  <View
                    className="w-2 h-2 rounded-[3px]"
                    style={{ backgroundColor: ativa ? "#fff" : cor }}
                  />
                  <Text
                    className="text-[12px] font-semibold"
                    style={{ color: ativa ? "#fff" : colors.text.primary }}
                  >
                    {c.nome}
                  </Text>
                </Pressable>
              );
            })
          )}
        </View>
      )}
    </View>
  );
}

/** De qual tabela veio a categoria da transação — `null` quando não tem nenhuma. */
function origemDa(t: TransacaoResponse): "SISTEMA" | "USUARIO" | null {
  if (t.idCategoriaGastoSistema != null) return "SISTEMA";
  if (t.idCategoriaGastoUsuario != null) return "USUARIO";
  return null;
}
