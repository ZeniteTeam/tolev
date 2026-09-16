import { useMemo, useState } from "react";
import { Pressable, Text, View } from "react-native";
import Svg, { Defs, LinearGradient, Path, Stop } from "react-native-svg";
import { ChartReveal } from "../../../components";
import { colors, shadows } from "../../../theme";
import { brl } from "../../debts/constants/dividas";
import { useDividas } from "../../debts/hooks/useDividas";
import { duracaoEmTexto, simularQuitacao } from "../../debts/utils/amortizacao";
import { metodoIdFromQuitacao } from "../../../types/preferencias";
import { metodoById } from "../constants/metodos";
import { usePreferencias } from "../hooks/usePreferencias";

const APORTES = [50, 100, 200, 500];

/** Teto do eixo quando a dívida não quita: 10 anos já conta a história. */
const HORIZONTE_MAX = 121;

/**
 * E se você pagasse um pouco mais por mês?
 *
 * As duas curvas são a mesma simulação rodada duas vezes — só o aporte muda.
 * Ver a linha cheia encostar no zero antes da tracejada é o argumento: o ganho
 * não é proporcional ao aporte, porque cada real a mais corta juros que nunca
 * chegam a nascer.
 */
export default function AporteExtraCard() {
  const { dividas, isPending } = useDividas();
  const { data: prefs } = usePreferencias();

  const salvo = prefs?.aporteExtraMensal ?? 0;
  const [aporte, setAporte] = useState<number | null>(null);
  const escolhido = aporte ?? (salvo > 0 ? salvo : 100);

  const metodo = metodoById(prefs ? metodoIdFromQuitacao(prefs.metodoQuitacao) : "avalanche");

  const ativas = useMemo(() => dividas.filter((d) => d.saldo > 0), [dividas]);

  const base = useMemo(
    () => simularQuitacao(ativas, 0, metodo.ordenar),
    [ativas, metodo],
  );
  const comAporte = useMemo(
    () => simularQuitacao(ativas, escolhido, metodo.ordenar),
    [ativas, escolhido, metodo],
  );

  if (isPending || ativas.length === 0) return null;

  // A simulação precisa de parcela e juros para andar; sem isso as duas curvas
  // seriam idênticas e o card não diria nada.
  if (base.curva.length < 2) return null;

  const opcoes = Array.from(new Set(salvo > 0 ? [...APORTES, salvo] : APORTES)).sort((a, b) => a - b);

  // Só dá para comparar juros entre dois cenários que terminam. Num cenário que
  // não quita, o total de juros depende de onde a simulação parou — subtrair um
  // do outro produziria um número grande e sem significado nenhum.
  const comparavel = base.meses != null && comAporte.meses != null;
  const jurosEconomizados = comparavel ? Math.max(0, base.jurosTotais - comAporte.jurosTotais) : 0;
  const mesesGanhos = comparavel ? base.meses! - comAporte.meses! : null;

  return (
    <View className="bg-surface rounded-[18px] p-5" style={shadows.card}>
      <Text className="font-bold text-base text-ink">E se você pagasse mais por mês?</Text>
      <Text className="text-[12px] text-muted mt-0.5 font-regular">
        Pelo método {metodo.nome}, sobre suas dívidas de hoje
      </Text>

      <View className="flex-row gap-2 mt-4 mb-[18px]">
        {opcoes.map((v) => {
          const ativo = v === escolhido;
          return (
            <Pressable
              key={v}
              onPress={() => setAporte(v)}
              className="flex-1 h-9 rounded-pill items-center justify-center active:opacity-85"
              style={{ backgroundColor: ativo ? colors.primary[700] : colors.primary[50] }}
            >
              <Text
                className="font-bold text-[12px]"
                style={{ color: ativo ? "#fff" : colors.text.secondary }}
              >
                +{brl(v)}
              </Text>
            </Pressable>
          );
        })}
      </View>

      <CurvasQuitacao base={base.curva} comAporte={comAporte.curva} />

      <View className="flex-row gap-4 justify-center mt-3">
        <Legenda color={colors.border.default} label="Como está hoje" tracejada />
        <Legenda color={colors.primary[500]} label={`Com +${brl(escolhido)}/mês`} />
      </View>

      {comparavel ? (
        <View className="flex-row mt-4 pt-4 border-t border-t-line-soft gap-3.5">
          <View className="flex-1">
            <Text className="text-[11px] text-muted font-regular">Fica livre em</Text>
            <Text className="font-bold text-[17px] text-ink mt-0.5">
              {duracaoEmTexto(comAporte.meses)}
            </Text>
            {mesesGanhos != null && mesesGanhos > 0 && (
              <Text className="text-[11px] text-primary-700 font-semibold mt-0.5">
                {duracaoEmTexto(mesesGanhos)} antes
              </Text>
            )}
          </View>
          <View className="w-px bg-line-soft" />
          <View className="flex-1">
            <Text className="text-[11px] text-muted font-regular">Juros que deixa de pagar</Text>
            <Text className="font-bold text-[17px] text-teal-500 mt-0.5">
              {brl(jurosEconomizados)}
            </Text>
            <Text className="text-[11px] text-muted font-regular mt-0.5">no total</Text>
          </View>
        </View>
      ) : (
        <View className="mt-4 pt-4 border-t border-t-line-soft">
          <Text className="text-[13px] text-ink leading-[19px] font-regular">
            {comAporte.meses != null ? (
              <>
                Hoje suas parcelas não cobrem nem os juros, e a dívida cresce sozinha. Com{" "}
                <Text className="font-bold">+{brl(escolhido)} por mês</Text> ela passa a ser
                quitável em <Text className="font-bold">{duracaoEmTexto(comAporte.meses)}</Text>.
              </>
            ) : (
              <>
                Nem com <Text className="font-bold">+{brl(escolhido)} por mês</Text> as parcelas
                cobrem os juros — o saldo continua subindo. Tente um valor maior, ou renegocie a
                dívida de juro mais alto antes de aportar.
              </>
            )}
          </Text>
        </View>
      )}
    </View>
  );
}

function CurvasQuitacao({ base, comAporte }: { base: number[]; comAporte: number[] }) {
  const [width, setWidth] = useState(0);
  const height = 132;

  const horizonte = Math.min(Math.max(base.length, comAporte.length), HORIZONTE_MAX);
  // O teto é o maior saldo de qualquer das curvas, não o saldo de hoje: num
  // cenário que não quita a linha sobe acima do ponto de partida e sairia do
  // desenho se a escala fosse fixada no início.
  const maxSaldo = Math.max(
    1,
    ...base.slice(0, horizonte),
    ...comAporte.slice(0, horizonte),
  );

  const toX = (i: number) => (i / Math.max(1, horizonte - 1)) * width;
  const toY = (v: number) => 6 + (1 - v / maxSaldo) * (height - 12);

  const caminho = (curva: number[]) =>
    curva
      .slice(0, horizonte)
      .map((v, i) => `${i === 0 ? "M" : "L"} ${toX(i).toFixed(1)} ${toY(v).toFixed(1)}`)
      .join(" ");

  const areaComAporte =
    caminho(comAporte) +
    ` L ${toX(Math.min(comAporte.length, horizonte) - 1).toFixed(1)} ${height - 6}` +
    ` L 0 ${height - 6} Z`;

  return (
    <ChartReveal>
      <View onLayout={(e) => setWidth(Math.round(e.nativeEvent.layout.width))}>
        {width > 0 && (
          <Svg width={width} height={height}>
            <Defs>
              <LinearGradient id="aporte-fill" x1="0" y1="0" x2="0" y2="1">
                <Stop offset="0%" stopColor={colors.primary[500]} stopOpacity={0.24} />
                <Stop offset="100%" stopColor={colors.primary[500]} stopOpacity={0} />
              </LinearGradient>
            </Defs>

            <Path d={areaComAporte} fill="url(#aporte-fill)" />
            <Path
              d={caminho(base)}
              stroke={colors.border.default}
              strokeWidth={2}
              strokeDasharray="5 4"
              fill="none"
            />
            <Path
              d={caminho(comAporte)}
              stroke={colors.primary[500]}
              strokeWidth={2.5}
              fill="none"
              strokeLinecap="round"
            />
          </Svg>
        )}
      </View>
    </ChartReveal>
  );
}

function Legenda({
  color,
  label,
  tracejada,
}: {
  color: string;
  label: string;
  tracejada?: boolean;
}) {
  return (
    <View className="flex-row items-center gap-1.5">
      <View
        className="w-4 h-0.5 rounded-pill"
        style={{ backgroundColor: color, opacity: tracejada ? 0.7 : 1 }}
      />
      <Text className="text-[12px] text-muted font-regular">{label}</Text>
    </View>
  );
}
