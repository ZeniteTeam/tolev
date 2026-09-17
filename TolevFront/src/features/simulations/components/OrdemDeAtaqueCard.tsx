import { Text, View } from "react-native";
import { ChartReveal } from "../../../components";
import { colors, shadows } from "../../../theme";
import { metodoIdFromQuitacao } from "../../../types/preferencias";
import { brl, parcelasEmAberto, type DividaView } from "../../debts/constants/dividas";
import { useDividas } from "../../debts/hooks/useDividas";
import { metodoById, type Criterio, type Metodo } from "../constants/metodos";
import { usePreferencias } from "../hooks/usePreferencias";

type Props = {
  /** Atraso da entrada, para o card chegar depois do que vem antes dele. */
  delay?: number;
};

/** Juros que a dívida cobra neste mês: o número da próxima parcela em aberto. */
function jurosDoMes(d: DividaView): number {
  const proxima = parcelasEmAberto(d)[0];
  if (proxima) return proxima.juros;
  // Dívida sem cronograma: cai na conta direta sobre o saldo.
  return d.saldo * (d.juros / 100);
}

/** Quanto sai do bolso por mês por causa dela. */
function parcelaDoMes(d: DividaView): number {
  return parcelasEmAberto(d)[0]?.valor ?? d.min;
}

type Linha = { divida: DividaView; peso: number; rotulo: string };

/**
 * Como cada critério lê a mesma lista de dívidas. O gráfico é o argumento
 * visual do método escolhido — mostrar "onde dá pra economizar mais" para quem
 * escolheu bola de neve seria defender a ordem oposta à que o app propõe.
 */
const LEITURAS: Record<
  Criterio,
  {
    titulo: string;
    sub: string;
    /** O que a barra mede. Maior peso = barra mais cheia. */
    peso: (d: DividaView) => number;
    rotulo: (d: DividaView) => string;
    /** Ordem em que o método ataca: a primeira linha é a próxima da fila. */
    ordenar: (a: Linha, b: Linha) => number;
    destaque: string;
    rodape: (linhas: Linha[]) => React.ReactNode;
  }
> = {
  juros: {
    titulo: "Onde dá pra economizar mais",
    sub: "Custo mensal de cada dívida, só em juros",
    peso: jurosDoMes,
    rotulo: (d) => brl(jurosDoMes(d)),
    ordenar: (a, b) => b.peso - a.peso,
    destaque: colors.coral[500],
    rodape: (linhas) => {
      const totalMes = linhas.reduce((s, l) => s + l.peso, 0);
      return (
        <>
          Juntas, suas dívidas custam <Text className="font-bold">{brl(totalMes)}</Text> por mês só
          em juros, ou <Text className="font-bold">{brl(totalMes * 12)}</Text> no ano. Atacar a{" "}
          {linhas[0].divida.nome} primeiro é o que corta mais rápido essa conta.
        </>
      );
    },
  },
  saldo: {
    titulo: "Qual dívida sai primeiro",
    sub: "Saldo de cada dívida, da menor para a maior",
    peso: (d) => d.saldo,
    rotulo: (d) => brl(d.saldo),
    ordenar: (a, b) => a.peso - b.peso,
    destaque: colors.primary[700],
    rodape: (linhas) => {
      const primeira = linhas[0].divida;
      return (
        <>
          A {primeira.nome} é o menor saldo:{" "}
          <Text className="font-bold">{brl(primeira.saldo)}</Text>. É a que some da lista primeiro,
          e os <Text className="font-bold">{brl(parcelaDoMes(primeira))}</Text> por mês dela passam
          a empurrar a próxima da fila.
        </>
      );
    },
  },
  emocional: {
    titulo: "O que mais pesa na sua cabeça",
    sub: "Peso emocional de cada dívida, do maior para o menor",
    peso: (d) => d.emocional,
    rotulo: (d) => `peso ${d.emocional} de 5`,
    ordenar: (a, b) => b.peso - a.peso,
    destaque: colors.teal[500],
    rodape: (linhas) => (
      <>
        A {linhas[0].divida.nome} é a que mais pesa. O tsunami começa por ela mesmo que não seja a
        mais cara nem a menor: aqui o alívio vem antes da economia.
      </>
    ),
  },
};

/**
 * A ordem em que suas dívidas devem cair, segundo o método de quitação
 * escolhido.
 *
 * O gráfico muda de critério junto com o método porque ele existe para
 * justificar a fila que o app propõe: em avalanche é o custo mensal em juros,
 * em bola de neve é o saldo, em tsunami é o peso emocional. O mesmo ranking
 * para os três diria que escolher o método não muda nada.
 */
export default function OrdemDeAtaqueCard({ delay = 0 }: Props) {
  const { dividas, isPending } = useDividas();
  const { data: prefs } = usePreferencias();

  const metodo: Metodo = metodoById(
    prefs ? metodoIdFromQuitacao(prefs.metodoQuitacao) : "avalanche",
  );
  const leitura = LEITURAS[metodo.criterio];

  if (isPending) return null;

  const linhas: Linha[] = dividas
    .filter((d) => d.saldo > 0)
    .map((d) => ({ divida: d, peso: leitura.peso(d), rotulo: leitura.rotulo(d) }))
    .filter((l) => l.peso > 0)
    .sort(leitura.ordenar);

  // Com uma dívida só não há ordem nenhuma para justificar.
  if (linhas.length < 2) return null;

  const maior = Math.max(...linhas.map((l) => l.peso));

  return (
    <View className="bg-surface rounded-[18px] p-5" style={shadows.card}>
      <View className="flex-row items-start justify-between gap-3">
        <View className="flex-1">
          <Text className="font-bold text-base text-ink">{leitura.titulo}</Text>
          <Text className="text-[12px] text-muted mt-0.5 font-regular">{leitura.sub}</Text>
        </View>
        <View className="px-2.5 py-1 rounded-pill" style={{ backgroundColor: metodo.color + "1A" }}>
          <Text className="text-[10px] font-bold" style={{ color: metodo.color }}>
            {metodo.nome.toUpperCase()}
          </Text>
        </View>
      </View>

      <ChartReveal delay={delay}>
        <View className="gap-3.5 mt-[18px]">
          {linhas.map((l, i) => (
            <View key={l.divida.id}>
              <View className="flex-row items-center justify-between mb-1.5">
                <View className="flex-row items-center gap-2 flex-1 pr-3">
                  <View
                    className="w-2.5 h-2.5 rounded-[3px]"
                    style={{ backgroundColor: l.divida.bankColor }}
                  />
                  <Text className="text-[13px] text-ink font-semibold flex-1" numberOfLines={1}>
                    {l.divida.nome}
                  </Text>
                </View>
                <Text
                  className="text-[13px] font-bold"
                  style={{ color: i === 0 ? leitura.destaque : colors.text.primary }}
                >
                  {l.rotulo}
                  {metodo.criterio === "juros" && (
                    <Text className="text-[11px] text-muted font-regular">/mês</Text>
                  )}
                </Text>
              </View>
              <View className="h-2 rounded-pill bg-primary-50 overflow-hidden">
                <View
                  className="h-full rounded-pill"
                  style={{
                    width: `${Math.max(4, (l.peso / maior) * 100)}%`,
                    backgroundColor: i === 0 ? leitura.destaque : colors.primary[300],
                  }}
                />
              </View>
            </View>
          ))}
        </View>
      </ChartReveal>

      <View className="mt-4 pt-4 border-t border-t-line-soft">
        <Text className="text-[13px] text-ink leading-[19px] font-regular">
          {leitura.rodape(linhas)}
        </Text>
      </View>
    </View>
  );
}
