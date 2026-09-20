package com.br.startup.tolevBack.graphs.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DebtProjectionGraphResponse(
        Long idUsuario,
        LocalDate dataPrevistaQuitacao,
        Integer mesesRestantes,
        BigDecimal totalRestante,
        BigDecimal totalPago,
        BigDecimal percentualPago,
        BigDecimal pagamentoMensalPrevisto,
        BigDecimal totalJurosRestante,
        BigDecimal comprometimentoRenda,   // %
        /**
         * SAUDAVEL / ATENCAO / CRITICO / SEM_RENDA — ver
         * {@code NivelComprometimento}. É o que permite a tela dizer se o
         * número é bom ou ruim em vez de só exibi-lo.
         */
        String nivelComprometimento,
        /**
         * Quanto da renda o plano do usuário reserva para dívidas, em %. É a
         * régua de {@code nivelComprometimento} — o plano dele, não um número
         * de cartilha.
         */
        BigDecimal limiteDividasPlano,
        /** A renda informada pelo usuário, crua. Zero = nunca informada. */
        BigDecimal rendaMensal,
        /** REGRA_50_30_20 / BASE_ZERO / ENVELOPES. */
        String metodoOrcamento,
        /**
         * O teto mensal de gasto livre que o método de orçamento impõe.
         *
         * <p>Nulo quando o método não impõe nenhum: no base zero não existe
         * fatia de lazer pré-definida, o que sobra é o que há para distribuir.
         */
        BigDecimal limiteLazerMensal,
        /** Soma dos gastos fixos declarados. Zero = nenhum cadastrado. */
        BigDecimal gastosFixosMensais,
        /**
         * Renda − gastos fixos − parcelas do mês. Pode ser negativo, e quando é,
         * essa é a informação mais importante da tela.
         *
         * <p>Nulo quando não há renda informada: sem ela não existe sobra nem
         * falta, só ausência de dado — e zero leria como "sobra nada".
         */
        BigDecimal sobraMensal,
        /**
         * Quanto dá para gastar por dia, pelos dias do mês corrente.
         *
         * <p>É o menor entre a sobra real e o teto do plano: o plano é um teto
         * sobre o que se pretende gastar, e a sobra é um teto sobre o que
         * existe. Vale o mais apertado dos dois, e quando a realidade já
         * estourou o plano é ela que manda.
         *
         * <p>É orçamento, não saldo: não oscila conforme o mês avança. Dividir
         * pelos dias restantes faria R$ 300 no último dia virar "R$ 300 por
         * dia".
         */
        BigDecimal disponivelPorDia,
        List<PontoProgresso> curvaProgresso,
        List<MesProjetado> meses
) {
    public record PontoProgresso(String mes, BigDecimal percentualQuitado) {}
    public record MesProjetado(String mes, BigDecimal dividaRestante, BigDecimal pagamentoPrevisto) {}
}