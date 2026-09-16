package com.br.startup.tolevBack.finance.application.dto.response;

import java.math.BigDecimal;

/**
 * Um banco do catálogo — a lista de onde escolher na hora de subir o extrato.
 *
 * @param codigoBanco código COMPE (001, 341, 260...). É por ele que o app casa o
 *                    registro com a cor e a sigla do banco na tela; casar por
 *                    nome quebraria na primeira vez que "Itaú Unibanco" virasse
 *                    "Itaú"
 */
public record BankResponse(
    Long id,
    String titulo,
    String codigoBanco,
    BigDecimal agencia
) {}
