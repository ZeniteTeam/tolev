import type { DividaView } from "../constants/dividas";

/** Trava final: 50 anos é tempo de sobra para qualquer cenário que quita. */
const LIMITE_MESES = 600;

/**
 * Meses seguidos de saldo crescendo que definem um cenário insustentável.
 *
 * Não é um chute de conveniência: neste modelo nada melhora sozinho — parcela,
 * taxa e aporte são fixos. Se o total não cedeu em um ano inteiro, não cede
 * nunca, e continuar somando juros compostos só produziria números absurdos.
 */
const MESES_DE_ALTA_PARA_DESISTIR = 12;

export type Quitacao = {
  /** Meses até zerar tudo, ou `null` quando o cenário não quita. */
  meses: number | null;
  /** Saldo devedor total ao fim de cada mês. O índice 0 é hoje. */
  curva: number[];
  /** Juros pagos no caminho. Só é comparável quando `meses` não é nulo. */
  jurosTotais: number;
  /**
   * O saldo cresce em vez de cair: as parcelas não cobrem nem os juros. É o
   * caso do rotativo do cartão, e é a informação mais importante que esta
   * simulação pode devolver.
   */
  divergente: boolean;
};

type Saldo = { saldo: number; taxa: number; parcela: number };

/**
 * Simula a quitação mês a mês, com rolagem: quando uma dívida acaba, a parcela
 * que ela consumia passa a reforçar a próxima da fila. É o que faz a curva
 * acelerar no fim, e é o mesmo comportamento nos dois cenários comparados — a
 * diferença entre eles fica sendo só o aporte.
 *
 * A ordem de ataque é fixada no início, e não recalculada a cada mês: é a ordem
 * que o app mostra ao usuário quando explica o método.
 */
export function simularQuitacao(
  dividas: DividaView[],
  aporteExtra: number,
  ordenar: (ds: DividaView[]) => DividaView[],
): Quitacao {
  const fila = ordenar(dividas.filter((d) => d.saldo > 0));

  const estado: Saldo[] = fila.map((d) => ({
    saldo: d.saldo,
    taxa: d.juros / 100,
    parcela: Math.max(0, d.min),
  }));

  const totalInicial = estado.reduce((s, e) => s + e.saldo, 0);
  const curva: number[] = [totalInicial];
  let jurosTotais = 0;
  let mesesEmAlta = 0;

  if (estado.length === 0) {
    return { meses: 0, curva, jurosTotais, divergente: false };
  }

  for (let mes = 1; mes <= LIMITE_MESES; mes++) {
    // 1. Juros do mês incidem sobre o saldo que sobrou do mês anterior.
    for (const e of estado) {
      if (e.saldo <= 0) continue;
      const juros = e.saldo * e.taxa;
      e.saldo += juros;
      jurosTotais += juros;
    }

    // 2. Cada dívida viva recebe a própria parcela. O que a parcela de uma
    //    dívida já quitada deixou de consumir vira reforço, junto do aporte.
    let reforco = aporteExtra;
    for (const e of estado) {
      if (e.saldo <= 0) {
        reforco += e.parcela;
        continue;
      }
      const pago = Math.min(e.parcela, e.saldo);
      e.saldo -= pago;
      if (e.parcela > pago) reforco += e.parcela - pago;
    }

    // 3. O reforço inteiro vai para a primeira da fila que ainda está de pé;
    //    se sobrar (ela quitou no caminho), escorre para a seguinte.
    for (const e of estado) {
      if (reforco <= 0) break;
      if (e.saldo <= 0) continue;
      const pago = Math.min(reforco, e.saldo);
      e.saldo -= pago;
      reforco -= pago;
    }

    const total = estado.reduce((s, e) => s + Math.max(0, e.saldo), 0);
    const anterior = curva[curva.length - 1];
    curva.push(total);

    if (total <= 0.01) return { meses: mes, curva, jurosTotais, divergente: false };

    mesesEmAlta = total >= anterior ? mesesEmAlta + 1 : 0;
    if (mesesEmAlta >= MESES_DE_ALTA_PARA_DESISTIR) {
      return { meses: null, curva, jurosTotais, divergente: true };
    }
  }

  return { meses: null, curva, jurosTotais, divergente: false };
}

/** Formata uma duração em meses como "1 ano e 2 meses". */
export function duracaoEmTexto(meses: number | null): string {
  if (meses == null) return "—";
  if (meses <= 0) return "agora";
  const anos = Math.floor(meses / 12);
  const restantes = meses % 12;
  const partes: string[] = [];
  if (anos > 0) partes.push(`${anos} ano${anos > 1 ? "s" : ""}`);
  if (restantes > 0) partes.push(`${restantes} ${restantes > 1 ? "meses" : "mês"}`);
  return partes.join(" e ");
}
