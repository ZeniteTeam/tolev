package com.br.startup.tolevBack.progression.application.usecase.commands.Debts;

import com.br.startup.tolevBack.progression.application.dto.request.RegisterPaymentRequest;
import com.br.startup.tolevBack.progression.application.dto.request.RegisterPaymentRequest.ParcelaPaga;
import com.br.startup.tolevBack.progression.application.dto.response.DividaResponse;
import com.br.startup.tolevBack.progression.internal.entity.Divida;
import com.br.startup.tolevBack.progression.internal.entity.PagamentoParcela;
import com.br.startup.tolevBack.progression.internal.entity.ParcelaDivida;
import com.br.startup.tolevBack.progression.internal.entity.ProgressoDivida;
import com.br.startup.tolevBack.progression.internal.enums.StatusDivida;
import com.br.startup.tolevBack.progression.internal.enums.StatusParcela;
import com.br.startup.tolevBack.progression.internal.mapper.DividaMapper;
import com.br.startup.tolevBack.progression.internal.repository.IDividaRepository;
import com.br.startup.tolevBack.progression.internal.repository.IPagamentoParcelaRepository;
import com.br.startup.tolevBack.progression.internal.repository.IParcelaDividaRepository;
import com.br.startup.tolevBack.progression.internal.repository.IProgressoDividaRepository;
import com.br.startup.tolevBack.shared.events.DadosFinanceirosAlteradosEvent;
import com.br.startup.tolevBack.shared.events.OrigemAlteracao;
import com.br.startup.tolevBack.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RegisterDividaPaymentService {

    private final IDividaRepository dividaRepository;
    private final IParcelaDividaRepository parcelaDividaRepository;
    private final IPagamentoParcelaRepository pagamentoParcelaRepository;
    private final IProgressoDividaRepository progressoDividaRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public DividaResponse execute(RegisterPaymentRequest request) {

        Divida divida = dividaRepository.findById(request.idDivida())
                .orElseThrow(() -> new NotFoundException("Dívida não encontrada com id: " + request.idDivida()));

        if (request.parcelas() == null || request.parcelas().isEmpty()) {
            throw new IllegalArgumentException("Nenhuma parcela informada para pagamento.");
        }

        Map<Integer, BigDecimal> valorPorNumero = new HashMap<>();
        for (ParcelaPaga paga : request.parcelas()) {
            if (paga != null && paga.numero() != null) {
                valorPorNumero.put(paga.numero(), paga.valorPago());
            }
        }
        if (valorPorNumero.isEmpty()) {
            throw new IllegalArgumentException("Nenhuma parcela informada para pagamento.");
        }

        LocalDate hoje = LocalDate.now();
        List<ParcelaDivida> selecionadas =
                parcelaDividaRepository.findByDividaAndNumeroParcelaIn(divida, List.copyOf(valorPorNumero.keySet()));

        BigDecimal totalPago = BigDecimal.ZERO;
        // O saldo devedor é principal: só a amortização o reduz, nunca os juros
        // embutidos na parcela. Somar o valor cheio faria a dívida "quitar" antes.
        BigDecimal totalAmortizado = BigDecimal.ZERO;

        for (ParcelaDivida parcela : selecionadas) {
            if (StatusParcela.PAGA.equals(parcela.getStatus())) {
                continue; // já paga — pagamento é idempotente por parcela
            }

            BigDecimal valorPago = valorInformado(valorPorNumero.get(parcela.getNumeroParcela()), parcela);

            parcela.setStatus(StatusParcela.PAGA);
            parcela.setDataPagamento(hoje);

            pagamentoParcelaRepository.save(PagamentoParcela.builder()
                    .parcelaDivida(parcela)
                    .valorPago(valorPago)
                    .dataPagamento(hoje)
                    .build());

            totalPago = totalPago.add(valorPago);
            totalAmortizado = totalAmortizado.add(amortizacaoDe(parcela, valorPago));
        }
        parcelaDividaRepository.saveAll(selecionadas);

        // Progressão da dívida acumula o valor pago
        ProgressoDivida progresso = progressoDividaRepository.findByDivida(divida)
                .orElseGet(() -> {
                    ProgressoDivida novo = new ProgressoDivida();
                    novo.setDivida(divida);
                    novo.setProgresso(BigDecimal.ZERO);
                    return novo;
                });
        BigDecimal progressoAtual = progresso.getProgresso() != null ? progresso.getProgresso() : BigDecimal.ZERO;
        progresso.setProgresso(progressoAtual.add(totalPago));
        progressoDividaRepository.save(progresso);

        // Saldo devedor diminui pela amortização (nunca abaixo de zero)
        BigDecimal saldoAtual = divida.getValorDivida() != null ? divida.getValorDivida() : BigDecimal.ZERO;
        divida.setValorDivida(saldoAtual.subtract(totalAmortizado).max(BigDecimal.ZERO));

        // Todas as parcelas quitadas → dívida paga
        List<ParcelaDivida> todas = parcelaDividaRepository.findByDividaOrderByNumeroParcela(divida);
        boolean tudoPago = !todas.isEmpty()
                && todas.stream().allMatch(p -> StatusParcela.PAGA.equals(p.getStatus()));
        if (tudoPago) {
            divida.setStatus(StatusDivida.PAGA);
            divida.setValorDivida(BigDecimal.ZERO);
        }
        dividaRepository.save(divida);

        // Pagamento muda o retrato inteiro (saldo, comprometimento, risco de
        // atraso), então é evento de alto impacto: recalcula sem esperar a
        // janela de debounce.
        eventPublisher.publishEvent(DadosFinanceirosAlteradosEvent.de(
                divida.getIdUsuario(), OrigemAlteracao.PAGAMENTO_DIVIDA, "DIVIDA", divida.getId()));

        // Não reatribuir divida.setParcelas(...): trocar a coleção gerenciada
        // (orphanRemoval = true) por uma nova lista dispara
        // "A collection with orphan deletion was no longer referenced...".
        // O mapper lê a coleção gerenciada — já atualizada nesta transação.
        return DividaMapper.toResponse(divida);
    }

    /** Sem valor informado, assume-se que a parcela foi paga pelo valor dela. */
    private BigDecimal valorInformado(BigDecimal informado, ParcelaDivida parcela) {
        if (informado != null && informado.signum() > 0) {
            return informado;
        }
        return parcela.getValorTotal() != null ? parcela.getValorTotal() : BigDecimal.ZERO;
    }

    /**
     * Quanto do pagamento abate o principal: a amortização prevista para a
     * parcela, mais o que tiver sido pago acima do valor dela (pagamento extra
     * abate principal direto).
     */
    private BigDecimal amortizacaoDe(ParcelaDivida parcela, BigDecimal valorPago) {
        BigDecimal valorTotal = parcela.getValorTotal() != null ? parcela.getValorTotal() : BigDecimal.ZERO;
        // Parcelas antigas (anteriores ao cálculo de amortização) não têm
        // valorPrincipal: nelas o valor cheio era principal.
        BigDecimal principal = parcela.getValorPrincipal() != null ? parcela.getValorPrincipal() : valorTotal;
        BigDecimal excedente = valorPago.subtract(valorTotal).max(BigDecimal.ZERO);
        return principal.add(excedente);
    }
}
