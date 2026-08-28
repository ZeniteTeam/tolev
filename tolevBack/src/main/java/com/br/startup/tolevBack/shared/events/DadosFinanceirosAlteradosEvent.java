package com.br.startup.tolevBack.shared.events;

import java.time.LocalDateTime;

/**
 * Os dados financeiros de um usuário mudaram.
 *
 * <p>Evento único e genérico de propósito: os módulos que escrevem (finance,
 * progression, users) anunciam <em>o que aconteceu</em> e nada mais. Quem
 * consome decide o que recalcular. É por isso que não existe
 * {@code TransacaoCriadaEvent}, {@code PagamentoRegistradoEvent} e companhia —
 * cada novo tipo de análise obrigaria a mexer em todos os publicadores.
 *
 * <p>Publicado dentro da transação de escrita, mas entregue só depois do commit
 * ({@code @TransactionalEventListener(AFTER_COMMIT)}): a análise nunca enxerga
 * um estado que ainda pode sofrer rollback.
 *
 * @param idUsuario    dono dos dados que mudaram
 * @param origem       o fato que ocorreu
 * @param entidadeTipo tipo da entidade envolvida ("TRANSACAO", "DIVIDA", ...); pode ser nulo
 * @param entidadeId   id dessa entidade; pode ser nulo
 * @param ocorridoEm   quando o fato aconteceu
 */
public record DadosFinanceirosAlteradosEvent(
        Long idUsuario,
        OrigemAlteracao origem,
        String entidadeTipo,
        Long entidadeId,
        LocalDateTime ocorridoEm
) {

    public static DadosFinanceirosAlteradosEvent de(
            Long idUsuario, OrigemAlteracao origem, String entidadeTipo, Long entidadeId) {
        return new DadosFinanceirosAlteradosEvent(
                idUsuario, origem, entidadeTipo, entidadeId, LocalDateTime.now());
    }

    public static DadosFinanceirosAlteradosEvent de(Long idUsuario, OrigemAlteracao origem) {
        return de(idUsuario, origem, null, null);
    }

    /** @see OrigemAlteracao#isAltoImpacto() */
    public boolean isAltoImpacto() {
        return origem != null && origem.isAltoImpacto();
    }
}
