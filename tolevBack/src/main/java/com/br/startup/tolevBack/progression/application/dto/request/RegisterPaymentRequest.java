package com.br.startup.tolevBack.progression.application.dto.request;

import java.math.BigDecimal;
import java.util.List;

/**
 * Registers a payment against one or more installments of a debt. Installments
 * may be paid in any order and several at once.
 */
public record RegisterPaymentRequest(
    Long idDivida,
    List<Integer> parcelas,
    BigDecimal valorPorParcela
) {}
