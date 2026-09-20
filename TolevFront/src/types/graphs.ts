/**
 * Espelha o módulo `graphs` do backend (graphs/application/dto/response/*).
 * LocalDate chega como "yyyy-MM-dd" e BigDecimal como número JSON.
 */

export interface CategoriaPonto {
  /**
   * Positivo = categoria do sistema, negativo = categoria do usuário, null =
   * sem categoria. O sinal importa: as duas tabelas do backend têm ids
   * independentes, então o id 3 existe nas duas. Use como identidade, nunca
   * como número.
   */
  idCategoria: number | null;
  nome: string;
  cor: string | null;
  valor: number;
  /** 0–100, já calculado pelo backend com duas casas. */
  percentual: number;
}

/**
 * A janela do gráfico, nas duas formas que o endpoint aceita.
 *
 * `meses` só sabe descrever período que termina hoje. Gasto importado de
 * extrato mora no passado, então a tela precisa poder pedir a faixa exata — e
 * nomeá-la corretamente ("jul/2026") em vez de "últimos 3 meses".
 */
export type PeriodoGastos =
  | { meses: number }
  | { inicio: string; fim: string };

/** GET /graphs/spending-by-category */
export interface SpendingByCategoryResponse {
  idUsuario: number;
  inicio: string;
  fim: string;
  totalDespesas: number;
  /** Despesas da janela — é o denominador do "classificado". */
  totalTransacoes: number;
  /**
   * Quantas dessas ainda pedem categoria: sem categoria nenhuma OU em "Outros".
   * É o mesmo conjunto da lista "para classificar" — "Outros" significa "decido
   * depois", então conta como não classificada.
   */
  transacoesAClassificar: number;
  /** Já vem ordenado por `valor`, decrescente. Desenhe na ordem que chegar. */
  pontos: CategoriaPonto[];
}

/**
 * O veredito sobre o comprometimento da renda.
 *
 * Duas réguas: acima de 50% é `CRITICO` venha o plano que vier, e abaixo disso
 * a pergunta é se passou do que o próprio usuário reservou para dívidas.
 */
export type NivelComprometimento = "SAUDAVEL" | "ATENCAO" | "CRITICO" | "SEM_RENDA";

export type MetodoOrcamento = "REGRA_50_30_20" | "BASE_ZERO" | "ENVELOPES";

/** Um ponto da curva de quitação: quanto da dívida contratada já foi paga. */
export interface PontoProgresso {
  /** "yyyy-MM" — o backend não formata mês, a tela formata. */
  mes: string;
  /** 0–100, com duas casas. */
  percentualQuitado: number;
}

/** Um mês do gráfico de barras empilhadas. */
export interface MesProjetado {
  /** "yyyy-MM". */
  mes: string;
  /** Saldo devedor DEPOIS do pagamento deste mês. */
  dividaRestante: number;
  /** Soma das parcelas que vencem neste mês. Zero é um valor legítimo. */
  pagamentoPrevisto: number;
}

/**
 * GET /graphs/debt-projection
 *
 * Tudo vem do cronograma de parcelas. Sem parcela em aberto não há data para
 * prever: `dataPrevistaQuitacao` volta null, e é esse o sinal do estado vazio —
 * os números todos vêm zerados junto, então não dá para distinguir pelo total.
 */
export interface DebtProjectionResponse {
  idUsuario: number;
  dataPrevistaQuitacao: string | null;
  mesesRestantes: number;
  totalRestante: number;
  totalPago: number;
  /** 0–100. */
  percentualPago: number;
  /** As parcelas do próximo mês inteiro — o mês corrente já pode ter sido pago. */
  pagamentoMensalPrevisto: number;
  totalJurosRestante: number;
  /** % da renda mensal comprometida. Zero quando o usuário não informou renda. */
  comprometimentoRenda: number;
  /** O veredito sobre `comprometimentoRenda` — é o que permite avisar. */
  nivelComprometimento: NivelComprometimento;
  /**
   * Quanto da renda o plano do usuário reserva para dívidas, em %. É a régua
   * do aviso: o plano dele, não um número de cartilha.
   */
  limiteDividasPlano: number;
  /** A renda informada, crua. Zero = o usuário nunca informou. */
  rendaMensal: number;
  metodoOrcamento: MetodoOrcamento;
  /**
   * Teto mensal de gasto livre que o método impõe. `null` quando ele não impõe
   * nenhum — no base zero o que sobra é justamente o que há para distribuir.
   */
  limiteLazerMensal: number | null;
  /** Soma dos gastos fixos cadastrados. Zero = nenhum. */
  gastosFixosMensais: number;
  /**
   * Renda − gastos fixos − parcelas do mês. Negativo é um resultado legítimo,
   * e quando acontece é a informação mais importante da tela.
   *
   * `null` sem renda informada: aí não há sobra nem falta, só ausência de
   * dado, e zero leria como "não sobra nada".
   */
  sobraMensal: number | null;
  /**
   * Quanto dá para gastar por dia: o menor entre a sobra real e o teto do
   * plano, pelos dias do mês. Orçamento, não saldo — não oscila conforme o mês
   * avança. `null` pelo mesmo motivo que `sobraMensal`.
   */
  disponivelPorDia: number | null;
  /** Do mês corrente até a quitação, amostrada em no máximo 24 pontos. */
  curvaProgresso: PontoProgresso[];
  /** Até 12 meses, sem buracos: mês sem parcela vem com pagamento zero. */
  meses: MesProjetado[];
}
