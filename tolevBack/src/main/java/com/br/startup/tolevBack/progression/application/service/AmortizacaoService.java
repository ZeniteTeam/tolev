package com.br.startup.tolevBack.progression.application.service;

import com.br.startup.tolevBack.progression.internal.enums.RegimeJuros;
import com.br.startup.tolevBack.progression.internal.enums.SistemaAmortizacao;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Monta a tabela de parcelas de uma dívida a partir do valor contratado, do
 * número de parcelas, da taxa mensal, do sistema de amortização e do regime de
 * juros informados pelo usuário.
 *
 * <p>O <b>sistema</b> define como o principal é amortizado:
 * <ul>
 *   <li>{@code PRICE} — a parcela total é constante;</li>
 *   <li>{@code SAC} — a amortização é constante ({@code PV / n}).</li>
 * </ul>
 *
 * <p>O <b>regime</b> define sobre o que os juros incidem:
 * <ul>
 *   <li>{@code COMPOSTO} — sobre o saldo devedor do período, que cai a cada parcela;</li>
 *   <li>{@code SIMPLES} — sempre sobre o valor original contratado, então os juros
 *       são iguais em todas as parcelas ({@code PV · i}).</li>
 * </ul>
 *
 * <p>Como no regime simples os juros não acompanham o saldo devedor, SAC e PRICE
 * produzem a mesma tabela nesse regime — a diferença entre os dois só aparece com
 * juros compostos. Com taxa zero, qualquer combinação vira {@code PV / n}.
 *
 * <p>Antes de montar a tabela, o principal passa pelo ajuste de primeiro período:
 * quase nunca a 1ª parcela vence exatamente 30 dias depois da liberação, e esses
 * dias a mais (ou a menos) rendem juros. Daí sai o saldo devedor ajustado
 * {@code PV_adj = PV₀ · (1+i)^((d−30)/30)}, com {@code d} = dias entre a liberação
 * e o primeiro vencimento. Quando {@code d = 30} o fator vale 1 e a fórmula vira
 * a tabela PRICE clássica, então o ajuste serve para os dois casos.
 */
@Service
public class AmortizacaoService {

    /** Escala usada no dinheiro persistido (centavos). */
    private static final int ESCALA = 2;
    private static final MathContext MC = MathContext.DECIMAL128;
    private static final BigDecimal CEM = BigDecimal.valueOf(100);
    private static final BigDecimal DIAS_DO_PERIODO = BigDecimal.valueOf(30);

    /**
     * Teto para o intervalo entre liberação e 1º vencimento. Acima disso não é
     * carência, é data digitada errada — e capitalizar por anos explodiria o
     * valor da parcela. Nesse caso o ajuste é ignorado.
     */
    private static final long MAX_DIAS_CARENCIA = 400;

    /** Uma linha da tabela: quanto abate do saldo, quanto é juros e quando vence. */
    public record ParcelaCalculada(
            int numero,
            BigDecimal amortizacao,
            BigDecimal juros,
            BigDecimal valorTotal,
            LocalDate vencimento
    ) {}

    /**
     * @param principal          valor contratado da dívida
     * @param quantidade         número total de parcelas
     * @param taxaMensalPercent  juros mensal em % (ex.: {@code 2.5} para 2,5% a.m.)
     * @param sistema            {@code PRICE} ou {@code SAC}; nulo assume {@code PRICE}
     * @param regime             {@code SIMPLES} ou {@code COMPOSTO}; nulo assume {@code COMPOSTO}
     * @param dataLiberacao      quando o valor foi liberado; nulo dispensa o ajuste de 1º período
     * @param primeiroVencimento vencimento da 1ª parcela; nulo assume o mês seguinte a hoje
     * @return a tabela completa, ou lista vazia se não houver parcelas a gerar
     */
    public List<ParcelaCalculada> calcular(
            BigDecimal principal,
            int quantidade,
            BigDecimal taxaMensalPercent,
            SistemaAmortizacao sistema,
            RegimeJuros regime,
            LocalDate dataLiberacao,
            LocalDate primeiroVencimento
    ) {
        if (quantidade <= 0) {
            return List.of();
        }

        BigDecimal pv = principal != null ? principal.setScale(ESCALA, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        if (pv.signum() <= 0) {
            return List.of();
        }

        BigDecimal taxa = taxaMensalPercent != null
                ? taxaMensalPercent.divide(CEM, MC)
                : BigDecimal.ZERO;

        if (taxa.signum() < 0) {
            taxa = BigDecimal.ZERO;
        }

        SistemaAmortizacao sistemaEfetivo = sistema != null ? sistema : SistemaAmortizacao.PRICE;

        RegimeJuros regimeEfetivo = regime != null ? regime : RegimeJuros.COMPOSTO;

        LocalDate base = primeiroVencimento != null ? primeiroVencimento : LocalDate.now().plusMonths(1);

        // Os dias entre a liberação e a 1ª parcela rendem juros antes mesmo de a
        // tabela começar: é sobre o saldo já ajustado que ela é montada.
        BigDecimal pvAjustado = ajustarPrimeiroPeriodo(pv, taxa, regimeEfetivo, dataLiberacao, base);

        List<ParcelaCalculada> parcelas =
                sistemaEfetivo == SistemaAmortizacao.SAC || regimeEfetivo == RegimeJuros.SIMPLES
                        ? amortizacaoConstante(pvAjustado, quantidade, taxa, regimeEfetivo)
                        : price(pvAjustado, quantidade, taxa);

        return comVencimentos(parcelas, base);
    }

    /**
     * Saldo devedor ajustado: {@code PV₀ · (1+i)^((d−30)/30)} nos juros compostos e
     * o equivalente linear {@code PV₀ · (1 + i·(d−30)/30)} nos simples, onde não há
     * capitalização. Com {@code d = 30}, ou sem a data de liberação, devolve o
     * próprio principal.
     */
    private BigDecimal ajustarPrimeiroPeriodo(
            BigDecimal pv,
            BigDecimal taxa,
            RegimeJuros regime,
            LocalDate dataLiberacao,
            LocalDate primeiroVencimento) {
        if (dataLiberacao == null || taxa.signum() == 0) {
            return pv;
        }

        long dias = ChronoUnit.DAYS.between(dataLiberacao, primeiroVencimento);
        if (dias <= 0 || dias > MAX_DIAS_CARENCIA) {
            return pv;
        }

        // (d − 30) / 30: positivo alonga o primeiro período, negativo encurta.
        BigDecimal expoente = BigDecimal.valueOf(dias)
                .subtract(DIAS_DO_PERIODO)
                .divide(DIAS_DO_PERIODO, MC);
        if (expoente.signum() == 0) {
            return pv;
        }

        if (regime == RegimeJuros.SIMPLES) {
            BigDecimal fator = BigDecimal.ONE.add(taxa.multiply(expoente, MC));
            return fator.signum() <= 0 ? pv : pv.multiply(fator, MC).setScale(ESCALA, RoundingMode.HALF_UP);
        }

        // Expoente fracionário: BigDecimal.pow só aceita inteiros, e a precisão de
        // double é folgada para um fator que multiplica centavos.
        double fator = Math.pow(1 + taxa.doubleValue(), expoente.doubleValue());
        if (!Double.isFinite(fator) || fator <= 0) {
            return pv;
        }
        return pv.multiply(BigDecimal.valueOf(fator), MC).setScale(ESCALA, RoundingMode.HALF_UP);
    }

    /**
     * PRICE com juros compostos: parcela fixa {@code PMT = PV·i / (1 − (1+i)^−n)}.
     * Os juros de cada mês incidem sobre o saldo devedor e o restante da parcela
     * amortiza o principal.
     */
    private List<ParcelaCalculada> price(BigDecimal pv, int quantidade, BigDecimal taxa) {
        if (taxa.signum() == 0) {
            return semJuros(pv, quantidade);
        }

        // fator = (1 + i)^n
        BigDecimal fator = BigDecimal.ONE.add(taxa).pow(quantidade, MC);

        BigDecimal pmt = pv.multiply(taxa, MC)
                .multiply(fator, MC)
                .divide(fator.subtract(BigDecimal.ONE), MC)
                .setScale(ESCALA, RoundingMode.HALF_UP);

        List<ParcelaCalculada> parcelas = new ArrayList<>(quantidade);
        BigDecimal saldo = pv;

        for (int numero = 1; numero <= quantidade; numero++) {
            if (numero == quantidade) {
                // A última parcela liquida o saldo restante, absorvendo os
                // arredondamentos das anteriores.
                BigDecimal juros = saldo.multiply(taxa, MC).setScale(ESCALA, RoundingMode.HALF_UP);
                parcelas.add(new ParcelaCalculada(numero, saldo, juros, saldo.add(juros), null));
                break;
            }
            BigDecimal juros = saldo.multiply(taxa, MC).setScale(ESCALA, RoundingMode.HALF_UP);
            BigDecimal amortizacao = pmt.subtract(juros);
            saldo = saldo.subtract(amortizacao);
            parcelas.add(new ParcelaCalculada(numero, amortizacao, juros, pmt, null));
        }
        return parcelas;
    }

    /**
     * Amortização constante ({@code PV / n}). Cobre o SAC (juros sobre o saldo
     * devedor, parcela decrescente) e qualquer sistema no regime simples (juros
     * fixos sobre o principal, parcela constante).
     */
    private List<ParcelaCalculada> amortizacaoConstante(
            BigDecimal pv, int quantidade, BigDecimal taxa, RegimeJuros regime) {
        if (taxa.signum() == 0) {
            return semJuros(pv, quantidade);
        }

        BigDecimal amortizacao = pv.divide(BigDecimal.valueOf(quantidade), ESCALA, RoundingMode.HALF_UP);
        BigDecimal jurosFixo = pv.multiply(taxa, MC).setScale(ESCALA, RoundingMode.HALF_UP);

        List<ParcelaCalculada> parcelas = new ArrayList<>(quantidade);
        BigDecimal saldo = pv;

        for (int numero = 1; numero <= quantidade; numero++) {
            BigDecimal juros = regime == RegimeJuros.SIMPLES
                    ? jurosFixo
                    : saldo.multiply(taxa, MC).setScale(ESCALA, RoundingMode.HALF_UP);
            // A última parcela leva o resto da divisão para fechar no principal.
            BigDecimal abate = numero == quantidade ? saldo : amortizacao;
            saldo = saldo.subtract(abate);
            parcelas.add(new ParcelaCalculada(numero, abate, juros, abate.add(juros), null));
        }
        return parcelas;
    }

    /** Sem juros a dívida é só o principal dividido em parcelas iguais. */
    private List<ParcelaCalculada> semJuros(BigDecimal pv, int quantidade) {
        BigDecimal amortizacao = pv.divide(BigDecimal.valueOf(quantidade), ESCALA, RoundingMode.HALF_UP);

        List<ParcelaCalculada> parcelas = new ArrayList<>(quantidade);
        BigDecimal saldo = pv;
        for (int numero = 1; numero <= quantidade; numero++) {
            BigDecimal abate = numero == quantidade ? saldo : amortizacao;
            saldo = saldo.subtract(abate);
            parcelas.add(new ParcelaCalculada(numero, abate, BigDecimal.ZERO, abate, null));
        }
        return parcelas;
    }

    /** Carimba os vencimentos: 1ª parcela na data informada, as demais mês a mês. */
    private List<ParcelaCalculada> comVencimentos(List<ParcelaCalculada> parcelas, LocalDate primeiroVencimento) {
        List<ParcelaCalculada> comData = new ArrayList<>(parcelas.size());
        for (ParcelaCalculada p : parcelas) {
            comData.add(new ParcelaCalculada(
                    p.numero(),
                    p.amortizacao(),
                    p.juros(),
                    p.valorTotal(),
                    primeiroVencimento.plusMonths(p.numero() - 1L)
            ));
        }
        return comData;
    }
}
