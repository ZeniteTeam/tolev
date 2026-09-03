package com.br.startup.tolevBack.progression.application.usecase.commands.Debts;

import com.br.startup.tolevBack.progression.application.dto.request.DividaRequest;
import com.br.startup.tolevBack.progression.application.dto.response.DividaResponse;
import com.br.startup.tolevBack.progression.application.service.AmortizacaoService;
import com.br.startup.tolevBack.progression.application.service.AmortizacaoService.ParcelaCalculada;
import com.br.startup.tolevBack.progression.internal.entity.Divida;
import com.br.startup.tolevBack.progression.internal.enums.RegimeJuros;
import com.br.startup.tolevBack.progression.internal.enums.SistemaAmortizacao;
import com.br.startup.tolevBack.progression.internal.mapper.DividaMapper;
import com.br.startup.tolevBack.progression.internal.repository.IDividaRepository;
import com.br.startup.tolevBack.shared.events.DadosFinanceirosAlteradosEvent;
import com.br.startup.tolevBack.shared.events.OrigemAlteracao;
import com.br.startup.tolevBack.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UpdateDividaService {

    private final IDividaRepository dividaRepository;
    private final AmortizacaoService amortizacaoService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public DividaResponse execute(Long id, DividaRequest request) {
        Divida divida = dividaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Dívida não encontrada com id: " + id));

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
                : divida.getDataPrimeiroVencimento();

        // As parcelas já emitidas não são regeradas — algumas podem estar pagas.
        // A tabela recalculada serve só para atualizar parcela mínima e prazo.
        List<ParcelaCalculada> tabela = amortizacaoService.calcular(
                saldo, quantidade, request.juros(), sistema, regime,
                request.dataLiberacao(), primeiroVencimento);

        divida.setNomeDivida(request.nome());
        divida.setBanco(request.banco());
        divida.setTipo(request.tipo());
        divida.setValorDivida(saldo);
        divida.setTaxaJuros(request.juros());
        divida.setMultaAtraso(request.multaAtraso());
        divida.setJurosMora(request.jurosMora());
        divida.setParcelaMinima(tabela.isEmpty() ? BigDecimal.ZERO : tabela.get(0).valorTotal());
        divida.setPesoEmocional(request.pesoEmocional());
        divida.setQuantidadeParcelas(request.quantidadeParcelas());
        divida.setDataLiberacao(request.dataLiberacao());
        divida.setDataPrimeiroVencimento(primeiroVencimento);
        divida.setDataVencimentoFinal(tabela.isEmpty() ? null : tabela.get(tabela.size() - 1).vencimento());
        divida.setSistemaAmortizacao(sistema);
        divida.setRegimeJuros(regime);

        Divida saved = dividaRepository.save(divida);

        eventPublisher.publishEvent(DadosFinanceirosAlteradosEvent.de(
                saved.getIdUsuario(), OrigemAlteracao.DIVIDA_ATUALIZADA, "DIVIDA", saved.getId()));

        return DividaMapper.toResponse(saved);
    }
}
