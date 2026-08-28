package com.br.startup.tolevBack.analysis.application.service;

import com.br.startup.tolevBack.analysis.internal.config.AnalysisProperties;
import com.br.startup.tolevBack.finance.application.dto.response.CategoryResponse;
import com.br.startup.tolevBack.finance.application.dto.response.FinancialOverviewResponse;
import com.br.startup.tolevBack.finance.application.dto.response.TransactionResponse;
import com.br.startup.tolevBack.finance.integration.api.FinanceIntegrationApi;
import com.br.startup.tolevBack.finance.internal.enums.TipoTransacao;
import com.br.startup.tolevBack.progression.application.dto.response.DividaResponse;
import com.br.startup.tolevBack.progression.application.dto.response.ParcelaResponse;
import com.br.startup.tolevBack.progression.integration.api.ProgressionIntegrationApi;
import com.br.startup.tolevBack.progression.internal.enums.StatusParcela;
import com.br.startup.tolevBack.users.application.dto.response.PreferenciaFinanceiraResponse;
import com.br.startup.tolevBack.users.application.dto.response.UsuarioResponse;
import com.br.startup.tolevBack.users.integration.api.UserIntegrationApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Junta, num único retrato, tudo que os analisadores precisam saber sobre o
 * usuário.
 *
 * <p>Toda leitura passa pelas integration APIs dos outros módulos — a análise
 * nunca toca repositório de finance, progression ou users.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AnalysisSnapshotAssembler {

    private final FinanceIntegrationApi financeApi;
    private final ProgressionIntegrationApi progressionApi;
    private final UserIntegrationApi userApi;
    private final AnalysisProperties properties;

    public AnalysisSnapshot montar(Long idUsuario) {
        LocalDate hoje = LocalDate.now();
        LocalDate inicioJanela = hoje.minusDays(properties.janelaDias());

        UsuarioResponse usuario = buscar(() -> userApi.getUserById(idUsuario), null, "usuário");
        PreferenciaFinanceiraResponse preferencias =
                buscar(() -> userApi.getPreferencias(idUsuario), null, "preferências");
        FinancialOverviewResponse overview =
                buscar(() -> financeApi.getFinancialOverview(idUsuario), null, "visão financeira");
        List<TransactionResponse> transacoes = buscar(
                () -> financeApi.getTransactionsByUserAndPeriod(idUsuario, inicioJanela, hoje),
                List.of(), "transações");
        List<DividaResponse> dividas =
                buscar(() -> progressionApi.getDividasByUser(idUsuario), List.of(), "dívidas");
        List<CategoryResponse> categorias =
                buscar(() -> financeApi.getCategories(idUsuario), List.of(), "categorias");

        YearMonth mesCorrente = YearMonth.from(hoje);

        BigDecimal receitaMesCorrente = somarNoMes(transacoes, TipoTransacao.RECEITA, mesCorrente);
        BigDecimal despesaMesCorrente = somarNoMes(transacoes, TipoTransacao.DESPESA, mesCorrente);

        // O mês corrente fica de fora da média: comparar um mês pela metade com
        // meses fechados sempre acusaria "gastando menos".
        BigDecimal rendaObservada = mediaMensalMesesFechados(transacoes, TipoTransacao.RECEITA, mesCorrente);
        BigDecimal despesaMensal = mediaMensalMesesFechados(transacoes, TipoTransacao.DESPESA, mesCorrente);

        BigDecimal rendaDeclarada = preferencias != null
                ? Calculo.nz(preferencias.rendaMensal())
                : BigDecimal.ZERO;

        // A renda declarada manda: é a que o usuário usou para montar o orçamento
        // dele. A observada só entra quando ele não declarou nada — é melhor que
        // zero, mas subestima quem não lança receita no app.
        BigDecimal rendaMensal = rendaDeclarada.signum() > 0 ? rendaDeclarada : rendaObservada;

        Map<String, BigDecimal> gastoPorCategoria = new LinkedHashMap<>();
        Map<String, BigDecimal> mediaPorCategoria = new LinkedHashMap<>();
        Map<String, Long> idPorCategoria = new HashMap<>();
        acumularPorCategoria(transacoes, mesCorrente, gastoPorCategoria, mediaPorCategoria, idPorCategoria);

        Map<Long, Integer> comprasPorVendedor = new HashMap<>();
        Map<Long, BigDecimal> gastoPorVendedor = new HashMap<>();
        Map<Long, String> nomesVendedor = new HashMap<>();
        acumularPorVendedor(transacoes, mesCorrente, comprasPorVendedor, gastoPorVendedor, nomesVendedor);

        DadosDasDividas dadosDividas = analisarDividas(dividas, hoje);

        return new AnalysisSnapshot(
                idUsuario,
                hoje,
                inicioJanela,
                usuario,
                preferencias,
                overview,
                transacoes,
                dividas,
                completarComCatalogo(idPorCategoria, categorias),
                arredondar(rendaMensal),
                arredondar(rendaDeclarada),
                arredondar(rendaObservada),
                arredondar(despesaMensal),
                arredondar(despesaMesCorrente),
                arredondar(receitaMesCorrente),
                gastoPorCategoria,
                mediaPorCategoria,
                comprasPorVendedor,
                gastoPorVendedor,
                nomesVendedor,
                arredondar(dadosDividas.comprometimentoMensal()),
                arredondar(dadosDividas.totalDividas()),
                arredondar(dadosDividas.totalJurosPendentes()),
                dadosDividas.parcelasAtrasadas(),
                arredondar(dadosDividas.valorEmAtraso()),
                dadosDividas.diasAtrasoMaximo()
        );
    }

    /**
     * Um módulo fora do ar não pode derrubar a análise inteira: sem dívidas
     * ainda dá para analisar consumo, sem transações ainda dá para analisar
     * dívidas. O que falta vira o valor neutro.
     */
    private <T> T buscar(java.util.function.Supplier<T> leitura, T fallback, String descricao) {
        try {
            T valor = leitura.get();
            return valor != null ? valor : fallback;
        } catch (Exception e) {
            log.warn("Análise seguiu sem {}: {}", descricao, e.getMessage());
            return fallback;
        }
    }

    /**
     * O id da categoria vem das próprias transações; o catálogo entra só para
     * cobrir categoria que existe mas não teve gasto no período.
     */
    private Map<String, Long> completarComCatalogo(
            Map<String, Long> idPorCategoria, List<CategoryResponse> categorias) {

        for (CategoryResponse categoria : categorias) {
            if (categoria.id() != null && categoria.nome() != null) {
                idPorCategoria.putIfAbsent(categoria.nome(), chaveCategoria(categoria));
            }
        }
        return idPorCategoria;
    }

    /**
     * Categoria do sistema e categoria do usuário têm ids independentes (tabelas
     * diferentes), então o id 3 pode existir nas duas. As do usuário entram
     * negadas para conviverem no mesmo mapa sem colidir.
     */
    private Long chaveCategoria(CategoryResponse categoria) {
        return switch (categoria.origem()) {
            case SISTEMA -> categoria.id();
            case USUARIO -> -categoria.id();
        };
    }

    private BigDecimal somarNoMes(List<TransactionResponse> transacoes, TipoTransacao tipo, YearMonth mes) {
        return transacoes.stream()
                .filter(t -> tipo.equals(t.tipo()))
                .filter(t -> t.dataTransacao() != null && YearMonth.from(t.dataTransacao()).equals(mes))
                .map(TransactionResponse::valor)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Média por mês considerando só os meses que já fecharam e que têm
     * lançamento. Dividir pelo número de meses da janela puniria quem começou a
     * usar o app na semana passada.
     */
    private BigDecimal mediaMensalMesesFechados(
            List<TransactionResponse> transacoes, TipoTransacao tipo, YearMonth mesCorrente) {

        Map<YearMonth, BigDecimal> porMes = new HashMap<>();
        for (TransactionResponse t : transacoes) {
            if (!tipo.equals(t.tipo()) || t.dataTransacao() == null || t.valor() == null) {
                continue;
            }
            YearMonth mes = YearMonth.from(t.dataTransacao());
            if (mes.equals(mesCorrente)) {
                continue;
            }
            porMes.merge(mes, t.valor(), BigDecimal::add);
        }
        if (porMes.isEmpty()) {
            // Só há o mês corrente: usar o parcial é melhor que dizer zero.
            return somarNoMes(transacoes, tipo, mesCorrente);
        }
        BigDecimal total = porMes.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return total.divide(BigDecimal.valueOf(porMes.size()), 2, RoundingMode.HALF_UP);
    }

    private void acumularPorCategoria(
            List<TransactionResponse> transacoes,
            YearMonth mesCorrente,
            Map<String, BigDecimal> gastoMesCorrente,
            Map<String, BigDecimal> mediaMesesAnteriores,
            Map<String, Long> idPorCategoria) {

        Map<String, Map<YearMonth, BigDecimal>> anteriores = new HashMap<>();

        for (TransactionResponse t : transacoes) {
            if (!TipoTransacao.DESPESA.equals(t.tipo()) || t.dataTransacao() == null || t.valor() == null) {
                continue;
            }
            String categoria = t.nomeCategoria() != null ? t.nomeCategoria() : "Sem categoria";
            registrarIdDaCategoria(t, categoria, idPorCategoria);
            YearMonth mes = YearMonth.from(t.dataTransacao());

            if (mes.equals(mesCorrente)) {
                gastoMesCorrente.merge(categoria, t.valor(), BigDecimal::add);
            } else {
                anteriores.computeIfAbsent(categoria, c -> new HashMap<>())
                        .merge(mes, t.valor(), BigDecimal::add);
            }
        }

        anteriores.forEach((categoria, porMes) -> {
            BigDecimal total = porMes.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            mediaMesesAnteriores.put(categoria,
                    total.divide(BigDecimal.valueOf(porMes.size()), 2, RoundingMode.HALF_UP));
        });
    }

    private void registrarIdDaCategoria(
            TransactionResponse t, String categoria, Map<String, Long> idPorCategoria) {
        if (t.idCategoriaGastoSistema() != null) {
            idPorCategoria.putIfAbsent(categoria, t.idCategoriaGastoSistema());
        } else if (t.idCategoriaGastoUsuario() != null) {
            idPorCategoria.putIfAbsent(categoria, -t.idCategoriaGastoUsuario());
        }
    }

    private void acumularPorVendedor(
            List<TransactionResponse> transacoes,
            YearMonth mesCorrente,
            Map<Long, Integer> compras,
            Map<Long, BigDecimal> gastos,
            Map<Long, String> nomes) {

        for (TransactionResponse t : transacoes) {
            if (!TipoTransacao.DESPESA.equals(t.tipo()) || t.idVendedor() == null
                    || t.dataTransacao() == null || t.valor() == null) {
                continue;
            }
            if (!YearMonth.from(t.dataTransacao()).equals(mesCorrente)) {
                continue;
            }
            compras.merge(t.idVendedor(), 1, Integer::sum);
            gastos.merge(t.idVendedor(), t.valor(), BigDecimal::add);
            if (t.nomeVendedor() != null) {
                nomes.putIfAbsent(t.idVendedor(), t.nomeVendedor());
            }
        }
    }

    private DadosDasDividas analisarDividas(List<DividaResponse> dividas, LocalDate hoje) {
        BigDecimal comprometimento = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal jurosPendentes = BigDecimal.ZERO;
        BigDecimal emAtraso = BigDecimal.ZERO;
        int diasMaximo = 0;
        Map<Long, List<ParcelaResponse>> atrasadas = new HashMap<>();

        YearMonth mesCorrente = YearMonth.from(hoje);

        for (DividaResponse divida : dividas) {
            total = total.add(Calculo.nz(divida.saldo()));
            if (divida.parcelas() == null) {
                continue;
            }
            for (ParcelaResponse parcela : divida.parcelas()) {
                if (StatusParcela.PAGA.equals(parcela.status())
                        || StatusParcela.CANCELADA.equals(parcela.status())) {
                    continue;
                }
                jurosPendentes = jurosPendentes.add(Calculo.nz(parcela.valorJuros()));

                LocalDate vencimento = parcela.dataVencimento();
                if (vencimento == null) {
                    continue;
                }
                if (YearMonth.from(vencimento).equals(mesCorrente)) {
                    comprometimento = comprometimento.add(Calculo.nz(parcela.valorTotal()));
                }
                if (vencimento.isBefore(hoje)) {
                    atrasadas.computeIfAbsent(divida.id(), id -> new ArrayList<>()).add(parcela);
                    emAtraso = emAtraso.add(Calculo.nz(parcela.valorTotal()));
                    diasMaximo = Math.max(diasMaximo, (int) ChronoUnit.DAYS.between(vencimento, hoje));
                }
            }
        }

        // Mês sem parcela a vencer (todas já pagas, ou vencimento no dia 5 e hoje
        // é dia 20) não significa dívida sem custo mensal. A parcela mínima é o
        // compromisso recorrente real de uma dívida ainda ativa.
        if (comprometimento.signum() == 0) {
            for (DividaResponse divida : dividas) {
                if (Calculo.nz(divida.saldo()).signum() > 0) {
                    comprometimento = comprometimento.add(Calculo.nz(divida.parcelaMinima()));
                }
            }
        }

        return new DadosDasDividas(comprometimento, total, jurosPendentes, atrasadas, emAtraso, diasMaximo);
    }

    private BigDecimal arredondar(BigDecimal valor) {
        return Calculo.dinheiro(valor);
    }

    private record DadosDasDividas(
            BigDecimal comprometimentoMensal,
            BigDecimal totalDividas,
            BigDecimal totalJurosPendentes,
            Map<Long, List<ParcelaResponse>> parcelasAtrasadas,
            BigDecimal valorEmAtraso,
            int diasAtrasoMaximo
    ) {
    }
}
