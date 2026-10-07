import { useNavigation } from "@react-navigation/native";
import { LinearGradient } from "expo-linear-gradient";
import {
  AlertCircle,
  AlertTriangle,
  CalendarCheck,
  ShieldCheck,
  TrendingDown,
  Wallet,
  Zap,
  type LucideIcon,
} from "lucide-react-native";
import { Pressable, Text, View } from "react-native";
import {
  Progress,
  ProjectionAreaChart,
  ProjectionBarChart,
  Stagger,
} from "../../../components";
import type { ProjectionMonth } from "../../../components/ProjectionBarChart";
import { colors, shadows } from "../../../theme";
import type { NivelComprometimento } from "../../../types/graphs";
import { isoToMonthYear, isoToShortMonth } from "../../../util/date";
import { brl } from "../constants/dividas";
import { useDebtProjection } from "../hooks/useDebtProjection";

/** Quantos meses o card de barras mostra. O backend manda até 12. */
const MESES_NO_GRAFICO = 6;

export default function ProjecoesTab() {
  const navigation = useNavigation<any>();
  const { data, isPending } = useDebtProjection();

  // Mesma regra da lista de dívidas: enquanto a primeira busca não volta, o
  // vazio e o carregando são indistinguíveis, e adivinhar dá um pisca.
  if (isPending) return null;
  if (!data || data.dataPrevistaQuitacao == null) return <SemProjecao />;

  const meses: ProjectionMonth[] = data.meses.slice(0, MESES_NO_GRAFICO).map((m) => ({
    label: isoToShortMonth(m.mes),
    divida: m.dividaRestante,
    pagto: m.pagamentoPrevisto,
  }));

  const curva = data.curvaProgresso.map((p) => p.percentualQuitado);

  const temRenda = data.rendaMensal > 0;

  return (
    <Stagger className="pt-[22px]">
      <LinearGradient
        colors={[colors.primary[700], colors.primary[600]]}
        start={{ x: 0, y: 0 }}
        end={{ x: 1, y: 1 }}
        className="rounded-[18px] p-[22px] mb-3.5"
        style={shadows.card}
      >
        <Text className="text-white/[0.85] text-sm font-semibold">Quitação prevista</Text>
        <Text className="text-white text-[30px] font-bold mt-1.5">
          {isoToMonthYear(data.dataPrevistaQuitacao)}
        </Text>
        <View className="flex-row items-center gap-1.5 mt-1.5">
          <Zap size={14} color={colors.primary[300]} strokeWidth={2} />
          <Text className="text-white/[0.9] text-sm font-regular">
            <Text className="font-bold">{animoPara(data.mesesRestantes)} </Text>
            {rotuloMeses(data.mesesRestantes)}
          </Text>
        </View>

        {/* Com um mês só a curva é um ponto: não há subida para mostrar. */}
        {curva.length > 1 && (
          <View className="mt-3.5">
            <ProjectionAreaChart values={curva} height={64} />
          </View>
        )}

        <View className="flex-row flex-wrap gap-y-3.5 mt-3.5 pt-4 border-t border-t-white/[0.18]">
          <Stat label="Dívida total" value={brl(data.totalRestante)} sub="a quitar" />
          <Stat
            label="Pagamento mensal"
            value={brl(data.pagamentoMensalPrevisto)}
            sub="previsto"
          />
          <Stat
            label="Juros a pagar"
            value={brl(data.totalJurosRestante)}
            sub="até a quitação"
            valueColor={colors.primary[300]}
          />
          <Stat
            label="Tempo restante"
            value={`${data.mesesRestantes} ${data.mesesRestantes === 1 ? "mês" : "meses"}`}
            sub="até a última parcela"
          />
        </View>
      </LinearGradient>

      {data.disponivelPorDia != null && (
        <LivrePorDia
          porDia={data.disponivelPorDia}
          sobraMensal={data.sobraMensal ?? 0}
          limiteLazer={data.limiteLazerMensal}
          gastosFixos={data.gastosFixosMensais}
          onCadastrarFixos={() => navigation.navigate("GastosFixos")}
        />
      )}

      {/* Só entra na tela quando há renda para comparar. Sem ela o card viraria
          "0% da renda", que lê como dívida sob controle. */}
      {temRenda && (
        <Comprometimento
          pct={data.comprometimentoRenda}
          nivel={data.nivelComprometimento}
          limitePlano={data.limiteDividasPlano}
          parcelas={data.pagamentoMensalPrevisto}
          renda={data.rendaMensal}
        />
      )}

      {meses.length > 0 && (
        <View className="bg-surface rounded-[18px] p-5 mb-3.5" style={shadows.card}>
          <Text className="font-bold text-[16px] text-ink">
            {meses.length === 1 ? "Próximo mês" : `Próximos ${meses.length} meses`}
          </Text>
          <Text className="text-[12px] text-muted mt-0.5 font-regular mb-[18px]">
            Dívida projetada vs. pagamentos
          </Text>
          <ProjectionBarChart months={meses} height={140} />
          <View className="flex-row gap-4 justify-center mt-3.5">
            <Legend color={colors.primary[500]} label="Dívida restante" />
            <Legend color={colors.coral[500]} label="Pagamento" />
          </View>
        </View>
      )}
    </Stagger>
  );
}

/**
 * O tom de cada veredito. O ícone e a cor mudam junto do texto — quem só
 * escaneia a tela precisa saber se está bem antes de ler a frase.
 */
const VEREDITO: Record<
  Exclude<NivelComprometimento, "SEM_RENDA">,
  { cor: string; bg: string; icon: LucideIcon; titulo: string; frase: (limite: number) => string }
> = {
  SAUDAVEL: {
    cor: colors.teal[500],
    bg: "rgba(48,188,179,0.12)",
    icon: ShieldCheck,
    titulo: "Sob controle",
    frase: (limite) => `Dentro dos ${limite}% que seu plano reserva para dívidas.`,
  },
  ATENCAO: {
    cor: "#E8A317",
    bg: "rgba(232,163,23,0.14)",
    icon: AlertTriangle,
    titulo: "Atenção",
    frase: (limite) => `Acima dos ${limite}% que seu plano reserva para dívidas.`,
  },
  CRITICO: {
    cor: colors.coral[500],
    bg: "rgba(254,111,80,0.12)",
    icon: AlertCircle,
    titulo: "Crítico",
    frase: () => "Mais da metade da sua renda vai para dívida.",
  },
};

/**
 * Quanto da renda vai para dívida, e se isso é bom ou ruim.
 *
 * A porcentagem sozinha não diz nada a quem não tem referência: 38% é muito ou
 * pouco? O veredito responde, e a régua é o plano do próprio usuário — quem
 * reservou 45% para dívidas não recebe alerta em 40%.
 */
function Comprometimento({
  pct,
  nivel,
  limitePlano,
  parcelas,
  renda,
}: {
  pct: number;
  nivel: NivelComprometimento;
  limitePlano: number;
  parcelas: number;
  renda: number;
}) {
  // SEM_RENDA não chega aqui: o card inteiro depende de ter renda.
  const v = VEREDITO[nivel === "SEM_RENDA" ? "SAUDAVEL" : nivel];
  const Icon = v.icon;

  return (
    <View className="bg-surface rounded-[18px] p-5 mb-4" style={shadows.card}>
      <View className="flex-row items-start gap-3.5">
        <View
          className="w-11 h-11 rounded-full items-center justify-center"
          style={{ backgroundColor: v.bg }}
        >
          <Icon size={20} color={v.cor} strokeWidth={2} />
        </View>
        <View className="flex-1">
          <Text className="font-bold text-[16px] text-ink">Comprometimento da renda</Text>
          {/* Os dois valores da conta ficam à vista: é a única forma de o
              usuário saber o que a porcentagem está e não está contando. */}
          <Text className="text-[12px] text-muted mt-0.5 font-regular">
            {brl(parcelas)} de parcelas sobre {brl(renda)} de renda
          </Text>
        </View>
        <Text className="font-bold text-[22px]" style={{ color: v.cor }}>
          {Math.round(pct)}%
        </Text>
      </View>

      <View className="mt-3.5">
        <Progress pct={Math.min(100, pct)} height={10} fillColor={v.cor} />
      </View>

      <View className="flex-row items-center gap-1.5 mt-3">
        <Text className="text-[13px] font-bold" style={{ color: v.cor }}>
          {v.titulo}
        </Text>
        <Text className="text-[12px] text-muted font-regular flex-1">
          {v.frase(Math.round(limitePlano))}
        </Text>
      </View>
    </View>
  );
}

/**
 * Quanto dá para gastar por dia depois dos compromissos do mês.
 *
 * É a soma que o card antigo fingia fazer: renda − gastos fixos − parcelas,
 * espalhada pelos dias do mês. Negativo não é escondido — significa que os
 * compromissos já passam da renda, e essa é a informação mais urgente da tela.
 *
 * Sem gasto fixo cadastrado o número existe mas está incompleto, e o card diz
 * isso em vez de deixar a pessoa acreditar que sobra mais do que sobra.
 */
function LivrePorDia({
  porDia,
  sobraMensal,
  limiteLazer,
  gastosFixos,
  onCadastrarFixos,
}: {
  porDia: number;
  sobraMensal: number;
  limiteLazer: number | null;
  gastosFixos: number;
  onCadastrarFixos: () => void;
}) {
  const negativo = porDia < 0;
  const semFixos = gastosFixos <= 0;
  const cor = negativo ? colors.coral[500] : colors.teal[500];
  // Quando o plano aperta mais que o dinheiro, o número não é "o que sobra" —
  // é o que o usuário decidiu se permitir. Dizer isso evita que ele ache que o
  // app está escondendo dinheiro.
  const limitadoPeloPlano = limiteLazer != null && !negativo && limiteLazer < sobraMensal;

  return (
    <View className="bg-surface rounded-[18px] p-5 mb-3.5" style={shadows.card}>
      <View className="flex-row items-start gap-3.5">
        <View
          className="w-11 h-11 rounded-full items-center justify-center"
          style={{ backgroundColor: negativo ? "rgba(254,111,80,0.12)" : "rgba(48,188,179,0.12)" }}
        >
          {negativo ? (
            <TrendingDown size={20} color={cor} strokeWidth={2} />
          ) : (
            <Wallet size={20} color={cor} strokeWidth={2} />
          )}
        </View>
        <View className="flex-1">
          <Text className="font-bold text-[16px] text-ink">
            {negativo ? "Faltam por dia" : "Livre por dia"}
          </Text>
          <Text className="text-[12px] text-muted mt-0.5 font-regular">
            {negativo
              ? "seus compromissos passam da renda"
              : limitadoPeloPlano
                ? "pelo seu plano de orçamento"
                : "depois dos gastos fixos e das parcelas"}
          </Text>
        </View>
        <Text className="font-bold text-[24px]" style={{ color: cor }}>
          {brl(Math.abs(porDia))}
        </Text>
      </View>

      <View className="mt-3.5 pt-3.5 border-t border-t-[#F1F5F3]">
        <Text className="text-[12px] text-muted font-regular">
          {brl(Math.abs(sobraMensal))} {negativo ? "de falta" : "de sobra"} no mês
          {limitadoPeloPlano && `, mas seu plano reserva ${brl(limiteLazer!)} para lazer`}
        </Text>
      </View>

      {semFixos && (
        <Pressable
          onPress={onCadastrarFixos}
          className="flex-row items-center gap-1.5 mt-2.5 active:opacity-70"
        >
          <Text className="text-[12px] text-muted font-regular">
            Sem gastos fixos cadastrados — este número está otimista.
          </Text>
          <Text className="text-[12px] font-bold text-teal-500">Cadastrar</Text>
        </Pressable>
      )}
    </View>
  );
}

/**
 * Quitado, ou nunca endividado. Os dois chegam aqui sem data prevista, e a
 * diferença entre eles não muda o que a tela tem a dizer.
 */
function SemProjecao() {
  return (
    <Stagger className="items-center pt-16 px-4">
      <View className="w-16 h-16 rounded-[20px] items-center justify-center bg-primary-50 mb-4">
        <CalendarCheck size={28} color={colors.primary[700]} strokeWidth={2} />
      </View>
      <Text className="font-bold text-[18px] text-ink text-center">Nada a projetar</Text>
      <Text className="text-[14px] text-muted text-center leading-[21px] mt-2 font-regular">
        Sem parcela em aberto não há data de quitação para prever. Cadastre uma dívida para
        acompanhar a projeção mês a mês.
      </Text>
    </Stagger>
  );
}

/** O tom muda com o tamanho do caminho — prometer "passa voando" em 40 meses soa falso. */
function animoPara(meses: number): string {
  if (meses <= 6) return "Passa voando!";
  if (meses <= 18) return "Dá pra ver o fim!";
  return "Um mês de cada vez.";
}

function rotuloMeses(meses: number): string {
  if (meses <= 0) return "Quitação neste mês";
  return meses === 1 ? "Falta 1 mês" : `Faltam ${meses} meses`;
}

function Stat({
  label,
  value,
  sub,
  valueColor,
}: {
  label: string;
  value: string;
  sub: string;
  valueColor?: string;
}) {
  return (
    <View className="w-1/2 pr-3">
      <Text className="text-[11px] text-white/[0.78] mb-1 font-regular">{label}</Text>
      <Text
        className="text-white font-bold text-[16px]"
        style={valueColor ? { color: valueColor } : undefined}
      >
        {value}
      </Text>
      <Text className="text-[11px] text-white/[0.7] mt-0.5 font-regular">{sub}</Text>
    </View>
  );
}

function Legend({ color, label }: { color: string; label: string }) {
  return (
    <View className="flex-row items-center gap-1.5">
      <View className="w-2.5 h-2.5 rounded-[3px]" style={{ backgroundColor: color }} />
      <Text className="text-[12px] text-muted font-regular">{label}</Text>
    </View>
  );
}
