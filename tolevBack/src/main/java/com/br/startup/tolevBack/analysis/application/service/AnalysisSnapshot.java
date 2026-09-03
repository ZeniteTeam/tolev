package com.br.startup.tolevBack.analysis.application.service;

import com.br.startup.tolevBack.finance.application.dto.response.FinancialOverviewResponse;
import com.br.startup.tolevBack.finance.application.dto.response.TransactionResponse;
import com.br.startup.tolevBack.progression.application.dto.response.DividaResponse;
import com.br.startup.tolevBack.progression.application.dto.response.ParcelaResponse;
import com.br.startup.tolevBack.users.application.dto.response.PreferenciaFinanceiraResponse;
import com.br.startup.tolevBack.users.application.dto.response.UsuarioResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Retrato financeiro completo do usuário num instante, já com os derivados
 * calculados.
 *
 * <p>Montado uma vez por execução e passado a todos os analisadores. Sem isso
 * cada analisador refaria as mesmas somas — renda mensal e despesa média são
 * usadas por praticamente todos — e, pior, poderiam chegar a números
 * diferentes para o mesmo usuário na mesma análise.
 *
 * @param rendaMensal        renda considerada: a declarada quando existe, senão a observada nas transações
 * @param rendaDeclarada     o que o usuário informou nas preferências
 * @param rendaObservada     média mensal de RECEITA lançada no período
 * @param despesaMensal      média mensal de DESPESA lançada no período
 * @param gastoPorCategoria  gasto do mês corrente por categoria
 * @param mediaPorCategoria  média mensal por categoria nos meses anteriores ao corrente
 * @param comprometimentoMensal soma das parcelas pendentes que vencem no mês corrente
 * @param parcelasAtrasadas  parcelas vencidas e não pagas, por dívida
 */
public record AnalysisSnapshot(
        Long idUsuario,
        LocalDate hoje,
        LocalDate inicioJanela,
        UsuarioResponse usuario,
        PreferenciaFinanceiraResponse preferencias,
        FinancialOverviewResponse overview,
        List<TransactionResponse> transacoes,
        List<DividaResponse> dividas,
        /**
         * Nome da categoria → id estável dela. Categoria do sistema entra
         * positiva, categoria do usuário negativa: as duas tabelas têm ids
         * independentes e o id 3 existe nas duas.
         */
        Map<String, Long> idPorCategoria,

        BigDecimal rendaMensal,
        BigDecimal rendaDeclarada,
        BigDecimal rendaObservada,
        BigDecimal despesaMensal,
        BigDecimal despesaMesCorrente,
        BigDecimal receitaMesCorrente,
        Map<String, BigDecimal> gastoPorCategoria,
        Map<String, BigDecimal> mediaPorCategoria,
        Map<Long, Integer> comprasPorVendedor,
        Map<Long, BigDecimal> gastoPorVendedor,
        Map<Long, String> nomesVendedor,

        BigDecimal comprometimentoMensal,
        BigDecimal totalDividas,
        BigDecimal totalJurosPendentes,
        Map<Long, List<ParcelaResponse>> parcelasAtrasadas,
        BigDecimal valorEmAtraso,
        int diasAtrasoMaximo
) {

    /** Sem renda nem movimentação não há o que analisar — evita gerar análise vazia. */
    public boolean temDadosSuficientes() {
        return Calculo.nz(rendaMensal).signum() > 0
                || !transacoes.isEmpty()
                || !dividas.isEmpty();
    }

    /** Quanto sobra por mês depois de despesas e parcelas. Pode ser negativo. */
    public BigDecimal sobraMensal() {
        return Calculo.nz(rendaMensal)
                .subtract(Calculo.nz(despesaMensal))
                .subtract(Calculo.nz(comprometimentoMensal));
    }

    /** Percentual da renda já comprometido com parcelas de dívida. */
    public BigDecimal comprometimentoPercentual() {
        return Calculo.percentual(comprometimentoMensal, rendaMensal);
    }

    /** Quantos meses de despesa o saldo em conta cobre. */
    public BigDecimal mesesDeReserva() {
        BigDecimal custoMensal = Calculo.nz(despesaMensal).add(Calculo.nz(comprometimentoMensal));
        return Calculo.dividir(saldoDisponivel(), custoMensal);
    }

    public BigDecimal saldoDisponivel() {
        return overview != null ? Calculo.nz(overview.totalSaldo()) : BigDecimal.ZERO;
    }

    /** (receita - despesa) / receita. Negativa quando se gasta mais do que entra. */
    public BigDecimal taxaPoupanca() {
        BigDecimal receita = Calculo.nz(rendaMensal);
        if (receita.signum() == 0) {
            return BigDecimal.ZERO;
        }
        return Calculo.percentual(receita.subtract(Calculo.nz(despesaMensal)), receita);
    }
}
