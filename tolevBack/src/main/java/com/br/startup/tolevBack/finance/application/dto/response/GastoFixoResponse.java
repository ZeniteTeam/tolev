package com.br.startup.tolevBack.finance.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Um gasto fixo como o app o mostra.
 *
 * @param confirmadoEm quando a pessoa confirmou pela última vez que o valor
 *                     continua valendo; nulo = nunca. A tela usa isso para
 *                     saber se o total do mês é um dado conferido ou um
 *                     número que envelheceu sozinho
 */
public record GastoFixoResponse(
    Long id,
    Long idUsuario,
    String nome,
    BigDecimal valor,
    Integer diaVencimento,
    LocalDate confirmadoEm
) {}
