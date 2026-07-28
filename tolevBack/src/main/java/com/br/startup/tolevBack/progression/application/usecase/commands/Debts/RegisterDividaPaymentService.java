package com.br.startup.tolevBack.progression.application.usecase.commands.Debts;

import com.br.startup.tolevBack.progression.application.dto.request.RegisterPaymentRequest;
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
import com.br.startup.tolevBack.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RegisterDividaPaymentService {

    private final IDividaRepository dividaRepository;
    private final IParcelaDividaRepository parcelaDividaRepository;
    private final IPagamentoParcelaRepository pagamentoParcelaRepository;
    private final IProgressoDividaRepository progressoDividaRepository;

    @Transactional
    public DividaResponse execute(RegisterPaymentRequest request) {
        Divida divida = dividaRepository.findById(request.idDivida())
                .orElseThrow(() -> new NotFoundException("Dívida não encontrada com id: " + request.idDivida()));

        if (request.parcelas() == null || request.parcelas().isEmpty()) {
            throw new IllegalArgumentException("Nenhuma parcela informada para pagamento.");
        }

        BigDecimal valorPorParcela = request.valorPorParcela() != null ? request.valorPorParcela() : BigDecimal.ZERO;
        LocalDate hoje = LocalDate.now();

        List<ParcelaDivida> selecionadas =
                parcelaDividaRepository.findByDividaAndNumeroParcelaIn(divida, request.parcelas());

        BigDecimal totalPago = BigDecimal.ZERO;
        for (ParcelaDivida parcela : selecionadas) {
            if (StatusParcela.PAGA.equals(parcela.getStatus())) {
                continue; // já paga — pagamento é idempotente por parcela
            }
            parcela.setStatus(StatusParcela.PAGA);
            parcela.setDataPagamento(hoje);

            pagamentoParcelaRepository.save(PagamentoParcela.builder()
                    .parcelaDivida(parcela)
                    .valorPago(valorPorParcela)
                    .dataPagamento(hoje)
                    .build());

            totalPago = totalPago.add(valorPorParcela);
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

        // Saldo devedor diminui (nunca abaixo de zero)
        BigDecimal saldoAtual = divida.getValorDivida() != null ? divida.getValorDivida() : BigDecimal.ZERO;
        BigDecimal novoSaldo = saldoAtual.subtract(totalPago);
        if (novoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            novoSaldo = BigDecimal.ZERO;
        }
        divida.setValorDivida(novoSaldo);

        // Todas as parcelas quitadas → dívida paga
        List<ParcelaDivida> todas = parcelaDividaRepository.findByDividaOrderByNumeroParcela(divida);
        boolean tudoPago = !todas.isEmpty()
                && todas.stream().allMatch(p -> StatusParcela.PAGA.equals(p.getStatus()));
        if (tudoPago) {
            divida.setStatus(StatusDivida.PAGA);
        }
        dividaRepository.save(divida);

        // Não reatribuir divida.setParcelas(...): trocar a coleção gerenciada
        // (orphanRemoval = true) por uma nova lista dispara
        // "A collection with orphan deletion was no longer referenced...".
        // O mapper lê a coleção gerenciada — já atualizada nesta transação.
        return DividaMapper.toResponse(divida);
    }
}
