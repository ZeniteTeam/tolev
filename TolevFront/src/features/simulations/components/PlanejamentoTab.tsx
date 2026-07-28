import {
  AlertTriangle,
  ArrowLeftRight,
  Check,
  ChevronRight,
  Clock,
  Coins,
  Equal,
  Handshake,
  Minus,
  PieChart,
  Scissors,
  Wallet,
  type LucideIcon,
} from "lucide-react-native";
import { Pressable, Text, View } from "react-native";
import { Progress } from "../../../components";
import { colors, shadows } from "../../../theme";
import { METODOS, metodoById, type Metodo, type MetodoId } from "../constants/metodos";
import { usePreferencias } from "../hooks/usePreferencias";
import { useUpdatePreferencias } from "../hooks/useUpdatePreferencias";
import {
  metodoIdFromQuitacao,
  metodoOrcamentoFromId,
  orcamentoIdFromMetodo,
  type OrcamentoId,
  type PreferenciaFinanceiraResponse,
} from "../../../types/preferencias";

type Props = { onOpenMetodo?: (id: MetodoId) => void };

export default function PlanejamentoTab({ onOpenMetodo }: Props) {
  const { data: prefs } = usePreferencias();
  const aplicado: MetodoId = prefs ? metodoIdFromQuitacao(prefs.metodoQuitacao) : "avalanche";

  return (
    <View className="pt-[18px]">
      <PlanSection title="Método de quitação" sub="Como priorizar o pagamento das suas dívidas" />
      <View className="gap-3">
        {METODOS.map((m) => (
          <MetodoCard
            key={m.id}
            m={m}
            aplicado={aplicado === m.id}
            onPress={() => onOpenMetodo?.(m.id)}
          />
        ))}
      </View>

      <View className="flex-row items-center gap-3 my-8">
        <View className="flex-1 h-px bg-line-soft" />
        <Text className="text-[11px] text-muted font-bold tracking-[0.6px]">
          FERRAMENTAS DE PLANEJAMENTO
        </Text>
        <View className="flex-1 h-px bg-line-soft" />
      </View>

      <PlanSection title="Libere seu fluxo de caixa" sub="Formas de sobrar mais dinheiro no mês" />
      <FluxoCaixa />

      <PlanSection
        title="Método de orçamento"
        sub="Escolha como distribuir sua renda"
        className="mt-7"
      />
      <Orcamento prefs={prefs} />

      <PlanSection
        title="Seus planos ativos"
        sub="Acompanhe o andamento mês a mês"
        className="mt-7"
      />
      <PlanosAtivos aplicado={aplicado} />
    </View>
  );
}

function PlanSection({ title, sub, className = "" }: { title: string; sub?: string; className?: string }) {
  return (
    <View className={`mb-3.5 pl-0.5 ${className}`}>
      <Text className="font-bold text-[18px] text-ink">{title}</Text>
      {sub && <Text className="text-[12px] text-muted mt-0.5 font-regular">{sub}</Text>}
    </View>
  );
}

function MetodoCard({ m, aplicado, onPress }: { m: Metodo; aplicado: boolean; onPress?: () => void }) {
  const Icon = m.icon;
  return (
    <Pressable
      onPress={onPress}
      className="bg-surface rounded-[16px] p-[18px] flex-row items-center gap-3.5 active:opacity-90"
      style={[shadows.card, aplicado && { borderWidth: 2, borderColor: m.color }]}
    >
      <View
        className="w-[46px] h-[46px] rounded-[13px] items-center justify-center"
        style={{ backgroundColor: m.color }}
      >
        <Icon size={22} color="#fff" strokeWidth={2} />
      </View>
      <View className="flex-1">
        <View className="flex-row items-center gap-2">
          <Text className="font-bold text-[16px] text-ink">{m.nome}</Text>
          {aplicado && (
            <View className="px-2 py-0.5 rounded-pill" style={{ backgroundColor: m.color + "1A" }}>
              <Text className="text-[10px] font-bold" style={{ color: m.color }}>
                ATIVO
              </Text>
            </View>
          )}
        </View>
        <Text className="text-[12px] text-muted mt-0.5 font-regular">{m.tagline}</Text>
      </View>
      <ChevronRight size={20} color={colors.text.secondary} strokeWidth={2} />
    </Pressable>
  );
}

function FluxoCaixa() {
  const opcoes: { icon: LucideIcon; titulo: string; desc: string }[] = [
    { icon: Handshake, titulo: "Renegociar dívidas", desc: "Negocie taxas menores ou prazos maiores direto com o credor." },
    { icon: ArrowLeftRight, titulo: "Portabilidade de crédito", desc: "Transfira a dívida para um banco que cobre juros menores." },
    { icon: Scissors, titulo: "Cortar assinaturas", desc: "Cancele serviços recorrentes que você quase não usa." },
    { icon: Coins, titulo: "Antecipar recebíveis", desc: "Adiante valores a receber para abater juros altos." },
  ];
  return (
    <View className="bg-surface rounded-[18px] p-2" style={shadows.card}>
      {opcoes.map((o, i) => {
        const Icon = o.icon;
        return (
          <Pressable
            key={o.titulo}
            className="flex-row items-center gap-3.5 px-3 py-3.5 active:opacity-90"
            style={i !== opcoes.length - 1 ? { borderBottomWidth: 1, borderBottomColor: "#F1F5F3" } : undefined}
          >
            <View className="w-10 h-10 rounded-[11px] bg-primary-100 items-center justify-center">
              <Icon size={19} color={colors.primary[700]} strokeWidth={2} />
            </View>
            <View className="flex-1">
              <Text className="text-[14px] font-semibold text-ink">{o.titulo}</Text>
              <Text className="text-[12px] text-muted mt-0.5 leading-[16px] font-regular">{o.desc}</Text>
            </View>
            <ChevronRight size={18} color={colors.text.secondary} strokeWidth={2} />
          </Pressable>
        );
      })}
    </View>
  );
}

function Orcamento({ prefs }: { prefs?: PreferenciaFinanceiraResponse }) {
  const updatePreferencias = useUpdatePreferencias();
  const sel: OrcamentoId = prefs ? orcamentoIdFromMetodo(prefs.metodoOrcamento) : "503020";

  const metodos: { id: OrcamentoId; nome: string; icon: LucideIcon; desc: string }[] = [
    { id: "503020", nome: "Regra 50/30/20", icon: PieChart, desc: "Divide a renda em três fatias: fixos, quitação de dívidas e lazer. Aceita variações conforme seu momento." },
    { id: "zero", nome: "Orçamento base zero", icon: Equal, desc: "Cada real da renda recebe uma função. Receita menos despesas alocadas deve dar exatamente zero." },
    { id: "envelope", nome: "Envelopes", icon: Wallet, desc: "Separe o dinheiro em envelopes digitais por categoria. Ideal para gastos que costumam estourar." },
  ];

  const escolher = (id: OrcamentoId) => {
    if (id === sel) return;
    updatePreferencias.mutate({ metodoOrcamento: metodoOrcamentoFromId(id) });
  };

  return (
    <View className="gap-3">
      {metodos.map((m) => {
        const active = sel === m.id;
        const Icon = m.icon;
        return (
          <Pressable
            key={m.id}
            onPress={() => escolher(m.id)}
            className="bg-surface rounded-[16px] p-[18px]"
            style={[shadows.card, active && { borderWidth: 2, borderColor: colors.primary[700] }]}
          >
            <View className="flex-row items-center gap-3">
              <View
                className="w-10 h-10 rounded-[11px] items-center justify-center"
                style={{ backgroundColor: active ? colors.primary[700] : colors.primary[100] }}
              >
                <Icon size={19} color={active ? "#fff" : colors.primary[700]} strokeWidth={2} />
              </View>
              <Text className="flex-1 font-bold text-[15px] text-ink">{m.nome}</Text>
              <View
                className="w-[22px] h-[22px] rounded-full items-center justify-center"
                style={
                  active
                    ? { backgroundColor: colors.primary[700] }
                    : { borderWidth: 2, borderColor: colors.border.default }
                }
              >
                {active && <Check size={14} color="#fff" strokeWidth={2.5} />}
              </View>
            </View>
            {active && (
              <>
                <Text className="text-[13px] text-muted leading-[20px] mt-3 font-regular">{m.desc}</Text>
                {m.id === "503020" && (
                  <Bar503020
                    fixos={prefs?.percFixos ?? 50}
                    dividas={prefs?.percDividas ?? 30}
                    lazer={prefs?.percLazer ?? 20}
                  />
                )}
              </>
            )}
          </Pressable>
        );
      })}
    </View>
  );
}

function Bar503020({ fixos, dividas, lazer }: { fixos: number; dividas: number; lazer: number }) {
  const seg = [
    { pct: fixos, label: "Fixos", color: "#03643F" },
    { pct: dividas, label: "Dívidas", color: "#30BCB3" },
    { pct: lazer, label: "Lazer", color: "#FE6F50" },
  ];
  return (
    <View className="mt-3.5">
      <View className="flex-row h-[30px] rounded-lg overflow-hidden">
        {seg.map((s) => (
          <View
            key={s.label}
            className="items-center justify-center"
            style={{ width: `${s.pct}%`, backgroundColor: s.color }}
          >
            <Text className="text-white text-[12px] font-bold">{s.pct}%</Text>
          </View>
        ))}
      </View>
      <View className="flex-row flex-wrap gap-4 mt-2.5">
        {seg.map((s) => (
          <View key={s.label} className="flex-row items-center gap-1.5">
            <View className="w-[9px] h-[9px] rounded-[3px]" style={{ backgroundColor: s.color }} />
            <Text className="text-[12px] text-muted font-regular">{s.label}</Text>
          </View>
        ))}
      </View>
    </View>
  );
}

function PlanosAtivos({ aplicado }: { aplicado: MetodoId }) {
  const m = metodoById(aplicado);
  const Icon = m.icon;
  const meses: { m: string; ok: boolean | null; now?: boolean }[] = [
    { m: "Jun", ok: true },
    { m: "Jul", ok: true },
    { m: "Ago", ok: true },
    { m: "Set", ok: false, now: true },
    { m: "Out", ok: null },
    { m: "Nov", ok: null },
  ];

  return (
    <View className="gap-3.5">
      <View className="bg-surface rounded-[18px] p-[18px]" style={shadows.card}>
        <View className="flex-row items-center gap-3 mb-4">
          <View
            className="w-10 h-10 rounded-[11px] items-center justify-center"
            style={{ backgroundColor: m.color }}
          >
            <Icon size={19} color="#fff" strokeWidth={2} />
          </View>
          <View className="flex-1">
            <Text className="font-bold text-[15px] text-ink">Plano {m.nome}</Text>
            <Text className="text-[12px] text-muted font-regular">Quitação de dívidas</Text>
          </View>
          <View className="items-end">
            <Text className="text-[11px] text-muted font-regular">Conclusão</Text>
            <Text className="text-[14px] font-bold text-primary-700">Dez/2026</Text>
          </View>
        </View>

        <View className="flex-row justify-between mb-1.5">
          <Text className="text-[12px] text-muted font-regular">Progresso mensal</Text>
          <Text className="text-[12px] font-bold text-teal-500">40%</Text>
        </View>
        <Progress pct={40} height={8} />

        <View className="flex-row gap-1.5 mt-4">
          {meses.map((x) => (
            <View key={x.m} className="flex-1 items-center">
              <View
                className="w-full h-[34px] rounded-[9px] items-center justify-center"
                style={{
                  backgroundColor:
                    x.ok === true ? colors.primary[100] : x.now ? "#FEE7E0" : "#F1F5F3",
                }}
              >
                {x.ok === true ? (
                  <Check size={15} color={colors.primary[700]} strokeWidth={2} />
                ) : x.now ? (
                  <Clock size={15} color={colors.coral[500]} strokeWidth={2} />
                ) : (
                  <Minus size={15} color={colors.text.secondary} strokeWidth={2} />
                )}
              </View>
              <Text className="text-[10px] text-muted mt-1 font-regular">{x.m}</Text>
            </View>
          ))}
        </View>
      </View>

      <View className="rounded-[16px] px-[18px] py-4 flex-row gap-3 items-start" style={{ backgroundColor: "#FEF3EE" }}>
        <View className="w-[34px] h-[34px] rounded-[10px] bg-coral-500 items-center justify-center">
          <AlertTriangle size={17} color="#fff" strokeWidth={2} />
        </View>
        <View className="flex-1">
          <Text className="text-[13px] font-bold" style={{ color: "#B33A20" }}>
            Ajuste recomendado
          </Text>
          <Text className="text-[13px] text-ink leading-[18px] mt-0.5 font-regular">
            Você pagou o mínimo em setembro. Aporte <Text className="font-bold">R$ 180</Text> extra
            para voltar ao ritmo e manter Dez/2026.
          </Text>
        </View>
      </View>
    </View>
  );
}
