package com.br.startup.tolevBack.progression.application.dto.request;

import java.math.BigDecimal;
import java.util.List;

/**
 * Registers a payment against one or more installments of a debt. Installments
 * may be paid in any order and several at once.
 *
 * <p>Cada parcela carrega o próprio valor pago: num SAC as parcelas têm valores
 * diferentes entre si, e mesmo no PRICE a última absorve o arredondamento — um
 * único "valor por parcela" não consegue representar o que foi realmente pago.
 */
public record RegisterPaymentRequest(
    Long idDivida,
    List<ParcelaPaga> parcelas
) {
    /**
     * @param numero    número da parcela dentro da dívida (1-based)
     * @param valorPago quanto foi pago nela; nulo ou zero assume o valor da própria parcela
     */
    public record ParcelaPaga(Integer numero, BigDecimal valorPago) {}
}
