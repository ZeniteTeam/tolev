package com.br.startup.tolevBack.graphs.internal.enums;

/**
 * Como está o comprometimento da renda com dívida.
 *
 * <p>Duas réguas, nesta ordem. A primeira é externa e fixa: metade da renda
 * indo para dívida é crítico, independentemente do que o plano da pessoa diga —
 * um plano que reserva 60% para dívida não torna 55% saudável. A segunda é o
 * próprio plano dela: passar do que planejou é atenção, mesmo que o número
 * ainda esteja longe da linha dura.
 */
public enum NivelComprometimento {

    /** Dentro do que o próprio plano reserva para dívidas. */
    SAUDAVEL,

    /** Passou do plano, mas ainda abaixo de metade da renda. */
    ATENCAO,

    /** Mais de 50% da renda comprometida com dívida. */
    CRITICO,

    /** Sem renda informada não há proporção para avaliar. */
    SEM_RENDA
}
