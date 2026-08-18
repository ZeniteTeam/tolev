package com.br.startup.tolevBack.analysis.internal.enums;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Catálogo dos achados que uma análise sabe detectar.
 *
 * <p>Cada constante é a identidade estável de um achado: é ela que vai na coluna
 * {@code regra} de {@code tb_analise_impacto} e {@code tb_recomendacoes}, e é por
 * ela que se conta "esse mesmo problema apareceu em 3 dias distintos".
 *
 * <p>Os templates são o texto garantido: quando o Gemini está configurado ele
 * reescreve isso em linguagem mais natural, mas os números vêm sempre daqui.
 * Placeholders no formato <code>{chave}</code> são preenchidos com o mapa de
 * dados que o analisador anexou ao achado.
 */
public enum RegraAnalise {

    // ---------------------------------------------------------------- consumo

    GASTO_CATEGORIA_ACIMA_DA_MEDIA(
            TipoRecomendacao.ECONOMIA, TipoImpacto.COMPORTAMENTO, new BigDecimal("2.00"),
            "{categoria} está puxando mais que o normal",
            "Você gastou R$ {gastoMes} em {categoria} neste mês, contra uma média de "
                    + "R$ {mediaMes} nos meses anteriores. São R$ {excedente} a mais por mês, "
                    + "ou R$ {excedenteAnual} no ano se o ritmo continuar."),

    CONCENTRACAO_EXCESSIVA_CATEGORIA(
            TipoRecomendacao.ECONOMIA, TipoImpacto.COMPORTAMENTO, new BigDecimal("3.00"),
            "{categoria} concentra {percentual}% dos seus gastos",
            "De cada R$ 100 que você gasta, R$ {percentual} vão para {categoria}. "
                    + "Uma única categoria pesando tanto deixa o orçamento sem folga para imprevistos."),

    GASTO_RECORRENTE_VENDEDOR(
            TipoRecomendacao.HABITO, TipoImpacto.COMPORTAMENTO, new BigDecimal("2.00"),
            "{vendedor}: {vezes} compras neste mês",
            "Você comprou {vezes} vezes em {vendedor} neste mês, somando R$ {total}. "
                    + "Gastos pequenos e repetidos custam R$ {totalAnual} por ano."),

    TAXA_POUPANCA_NEGATIVA(
            TipoRecomendacao.ALERTA, TipoImpacto.FINANCEIRO, new BigDecimal("4.00"),
            "Você está gastando mais do que ganha",
            "Suas despesas somaram R$ {despesa} contra R$ {receita} de receita no período. "
                    + "São R$ {deficit} por mês saindo da reserva ou virando dívida nova."),

    // ------------------------------------------------------- saúde financeira

    COMPROMETIMENTO_RENDA_ALTO(
            TipoRecomendacao.ALERTA, TipoImpacto.FINANCEIRO, new BigDecimal("4.00"),
            "Suas parcelas comprometem {percentual}% da renda",
            "R$ {parcelas} dos seus R$ {renda} mensais já estão comprometidos com parcelas. "
                    + "Acima de 30% qualquer imprevisto vira dívida nova."),

    SEM_RESERVA_EMERGENCIA(
            TipoRecomendacao.HABITO, TipoImpacto.FINANCEIRO, new BigDecimal("3.00"),
            "Sua reserva cobre {meses} mês(es) de despesa",
            "Você tem R$ {saldo} guardado e gasta R$ {despesaMensal} por mês. "
                    + "O recomendado são 6 meses de despesa — hoje seriam R$ {metaReserva}."),

    ORCAMENTO_DIVIDAS_ESTOURADO(
            TipoRecomendacao.ALERTA, TipoImpacto.FINANCEIRO, new BigDecimal("3.00"),
            "As dívidas passaram do teto que você definiu",
            "Você reservou {percDividas}% da renda (R$ {tetoDividas}) para dívidas, "
                    + "mas as parcelas somam R$ {parcelas} por mês."),

    // ---------------------------------------------------------- inadimplência

    PARCELA_EM_ATRASO(
            TipoRecomendacao.ALERTA, TipoImpacto.DIVIDA, new BigDecimal("4.00"),
            "{divida}: {parcelasAtrasadas} parcela(s) em atraso",
            "Há R$ {valorAtrasado} vencidos em {divida}, o mais antigo há {diasAtraso} dias. "
                    + "Multa e juros de mora já somam R$ {encargos} e crescem a cada dia."),

    RISCO_INADIMPLENCIA_ALTO(
            TipoRecomendacao.ALERTA, TipoImpacto.RISCO, new BigDecimal("5.00"),
            "Risco alto de não conseguir pagar as próximas parcelas",
            "Com {comprometimento}% da renda comprometida e reserva de {mesesReserva} mês(es), "
                    + "a chance de atrasar algum pagamento nos próximos meses é de {probabilidade}%."),

    DIVIDA_JUROS_ALTO(
            TipoRecomendacao.ECONOMIA, TipoImpacto.DIVIDA, new BigDecimal("3.00"),
            "{divida} é a dívida que mais custa: {juros}% ao mês",
            "Essa dívida sozinha gera R$ {jurosAnuais} de juros por ano. "
                    + "Pelo método {metodo}, é ela que deve receber qualquer dinheiro extra primeiro."),

    // ---------------------------------------------------------------- previsão

    SALDO_PROJETADO_NEGATIVO(
            TipoRecomendacao.ALERTA, TipoImpacto.FINANCEIRO, new BigDecimal("4.00"),
            "Seu saldo deve fechar o mês negativo",
            "Sobram R$ {saldoAtual} em conta e ainda vencem R$ {compromissos} até o fim do mês. "
                    + "A projeção fecha em R$ {saldoProjetado}."),

    APORTE_EXTRA_ACELERA_QUITACAO(
            TipoRecomendacao.INVESTIMENTO, TipoImpacto.DIVIDA, new BigDecimal("2.00"),
            "R$ {aporte} por mês antecipam sua quitação",
            "Sobrando R$ {sobra} por mês, direcionar R$ {aporte} para {divida} economiza "
                    + "R$ {economiaJuros} em juros até o fim do contrato.");

    private final TipoRecomendacao tipoRecomendacao;
    private final TipoImpacto tipoImpacto;
    private final BigDecimal dificuldade;
    private final String tituloTemplate;
    private final String descricaoTemplate;

    RegraAnalise(TipoRecomendacao tipoRecomendacao, TipoImpacto tipoImpacto, BigDecimal dificuldade,
                 String tituloTemplate, String descricaoTemplate) {
        this.tipoRecomendacao = tipoRecomendacao;
        this.tipoImpacto = tipoImpacto;
        this.dificuldade = dificuldade;
        this.tituloTemplate = tituloTemplate;
        this.descricaoTemplate = descricaoTemplate;
    }

    public TipoRecomendacao tipoRecomendacao() {
        return tipoRecomendacao;
    }

    public TipoImpacto tipoImpacto() {
        return tipoImpacto;
    }

    /** Quão difícil é para o usuário seguir a recomendação, de 1 (trivial) a 5 (duro). */
    public BigDecimal dificuldade() {
        return dificuldade;
    }

    public String titulo(Map<String, String> dados) {
        return preencher(tituloTemplate, dados);
    }

    public String descricao(Map<String, String> dados) {
        return preencher(descricaoTemplate, dados);
    }

    /**
     * Placeholder sem valor correspondente fica como está, em vez de virar
     * "null" no meio da frase — o texto continua legível e o buraco aparece
     * no log em vez de na tela do usuário.
     */
    private static String preencher(String template, Map<String, String> dados) {
        if (dados == null || dados.isEmpty()) {
            return template;
        }
        String texto = template;
        for (Map.Entry<String, String> dado : dados.entrySet()) {
            if (dado.getValue() != null) {
                texto = texto.replace("{" + dado.getKey() + "}", dado.getValue());
            }
        }
        return texto;
    }
}
