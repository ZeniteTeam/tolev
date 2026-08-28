package com.br.startup.tolevBack.progression.application.usecase.commands.Debts;

import com.br.startup.tolevBack.progression.application.dto.request.DividaRequest;
import com.br.startup.tolevBack.progression.application.dto.response.DividaResponse;
import com.br.startup.tolevBack.progression.application.service.AmortizacaoService;
import com.br.startup.tolevBack.progression.application.service.AmortizacaoService.ParcelaCalculada;
import com.br.startup.tolevBack.progression.internal.entity.Divida;
import com.br.startup.tolevBack.progression.internal.entity.ParcelaDivida;
import com.br.startup.tolevBack.progression.internal.entity.ProgressoDivida;
import com.br.startup.tolevBack.progression.internal.enums.RegimeJuros;
import com.br.startup.tolevBack.progression.internal.enums.SistemaAmortizacao;
import com.br.startup.tolevBack.progression.internal.enums.StatusDivida;
import com.br.startup.tolevBack.progression.internal.enums.StatusParcela;
import com.br.startup.tolevBack.progression.internal.mapper.DividaMapper;
import com.br.startup.tolevBack.progression.internal.repository.IDividaRepository;
import com.br.startup.tolevBack.progression.internal.repository.IParcelaDividaRepository;
import com.br.startup.tolevBack.progression.internal.repository.IProgressoDividaRepository;
import com.br.startup.tolevBack.shared.events.DadosFinanceirosAlteradosEvent;
import com.br.startup.tolevBack.shared.events.OrigemAlteracao;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateDividaService {

    private final IDividaRepository dividaRepository;
    private final IProgressoDividaRepository progressoDividaRepository;
    private final IParcelaDividaRepository parcelaDividaRepository;
    private final AmortizacaoService amortizacaoService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public DividaResponse execute(DividaRequest request) {
        int quantidade = request.quantidadeParcelas() != null ? request.quantidadeParcelas() : 0;
        BigDecimal saldo = request.saldo() != null ? request.saldo() : BigDecimal.ZERO;

        SistemaAmortizacao sistema = request.sistemaAmortizacao() != null
                ? request.sistemaAmortizacao()
                : SistemaAmortizacao.PRICE;

        RegimeJuros regime = request.regimeJuros() != null
                ? request.regimeJuros()
                : RegimeJuros.COMPOSTO;

        LocalDate primeiroVencimento = request.dataPrimeiroVencimento() != null
                ? request.dataPrimeiroVencimento()
                : LocalDate.now().plusMonths(1);

        // A tabela de parcelas é a fonte da verdade: dela saem os valores, os
        // vencimentos, a parcela mínima e o vencimento final da dívida.

        List<ParcelaCalculada> tabela = amortizacaoService.calcular(
                saldo, quantidade, request.juros(), sistema, regime,
                request.dataLiberacao(), primeiroVencimento);

        Divida divida = Divida.builder()
                .idUsuario(request.idUsuario())
                .nomeDivida(request.nome())
                .banco(request.banco())
                .tipo(request.tipo())
                .valorDivida(saldo)
                .taxaJuros(request.juros())
                .multaAtraso(request.multaAtraso())
                .jurosMora(request.jurosMora())
                .parcelaMinima(primeiraParcela(tabela))
                .pesoEmocional(request.pesoEmocional())
                .quantidadeParcelas(quantidade > 0 ? quantidade : null)
                .dataLiberacao(request.dataLiberacao())
                .dataPrimeiroVencimento(tabela.isEmpty() ? null : primeiroVencimento)
                .dataVencimentoFinal(ultimoVencimento(tabela))
                .sistemaAmortizacao(sistema)
                .regimeJuros(regime)
                .status(StatusDivida.ATIVA)
                .build();

        divida = dividaRepository.save(divida);

        ProgressoDivida progresso = new ProgressoDivida();
        progresso.setDivida(divida);
        progresso.setProgresso(BigDecimal.ZERO);
        progressoDividaRepository.save(progresso);

        divida.setParcelas(persistirParcelas(divida, tabela));

        eventPublisher.publishEvent(DadosFinanceirosAlteradosEvent.de(
                divida.getIdUsuario(), OrigemAlteracao.DIVIDA_CRIADA, "DIVIDA", divida.getId()));

        return DividaMapper.toResponse(divida);
    }

    private List<ParcelaDivida> persistirParcelas(Divida divida, List<ParcelaCalculada> tabela) {
        if (tabela.isEmpty()) {
            return new ArrayList<>();
        }

        List<ParcelaDivida> parcelas = tabela.stream()
                .map(p -> ParcelaDivida.builder()
                        .divida(divida)
                        .numeroParcela(p.numero())
                        .valorTotal(p.valorTotal())
                        .valorPrincipal(p.amortizacao())
                        .valorJuros(p.juros())
                        .status(StatusParcela.PENDENTE)
                        .dataVencimento(p.vencimento())
                        .build())
                .toList();

        return parcelaDividaRepository.saveAll(parcelas);
    }

    private BigDecimal primeiraParcela(List<ParcelaCalculada> tabela) {
        return tabela.isEmpty() ? BigDecimal.ZERO : tabela.get(0).valorTotal();
    }

    private LocalDate ultimoVencimento(List<ParcelaCalculada> tabela) {
        return tabela.isEmpty() ? null : tabela.get(tabela.size() - 1).vencimento();
    }
}
