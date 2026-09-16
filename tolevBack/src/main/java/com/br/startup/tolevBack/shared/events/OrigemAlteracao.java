package com.br.startup.tolevBack.shared.events;

/**
 * O que mudou na vida financeira do usuário. Descreve o fato, não a tela nem o
 * endpoint — quem publica não sabe (nem precisa saber) que existe uma análise
 * escutando do outro lado.
 */
public enum OrigemAlteracao {

    /** Lançamento manual de receita ou despesa. */
    TRANSACAO_CRIADA(false),

    /**
     * Uma transação já gravada ganhou (ou trocou de) categoria.
     *
     * <p>Baixo impacto: não muda quanto o usuário gastou, só em que prateleira
     * o gasto entra. Quem está classificando costuma classificar várias
     * seguidas, e recalcular a cada clique seria trabalho jogado fora.
     */
    TRANSACAO_CATEGORIZADA(false),

    /** Parcela(s) de dívida quitada(s). */
    PAGAMENTO_DIVIDA(true),

    /** Dívida nova entrou no mapa. */
    DIVIDA_CRIADA(true),

    /** Saldo, juros, prazo ou tipo da dívida mudaram. */
    DIVIDA_ATUALIZADA(true),

    /** Dívida saiu do mapa. */
    DIVIDA_REMOVIDA(true),

    /** Valor abatido direto no progresso da dívida. */
    PROGRESSO_DIVIDA(false),

    /** Renda, método de quitação ou divisão do orçamento mudaram. */
    PREFERENCIAS_ATUALIZADAS(true),

    /**
     * Um extrato bancário foi importado (ou teve a importação desfeita).
     *
     * <p>Alto impacto mesmo sendo, no fundo, um monte de TRANSACAO_CRIADA: um
     * extrato traz o mês inteiro de gastos de uma vez, e o usuário acabou de
     * subir o PDF justamente para ver a análise sair. Esperar o debounce de 30
     * minutos aqui seria responder "volte depois" a quem está olhando a tela.
     */
    EXTRATO_IMPORTADO(true);

    private final boolean altoImpacto;

    OrigemAlteracao(boolean altoImpacto) {
        this.altoImpacto = altoImpacto;
    }

    /**
     * Fatos de alto impacto furam o debounce e forçam recálculo imediato.
     *
     * <p>Uma despesa de R$ 30 pode esperar a janela fechar; quitar uma dívida
     * ou mudar a renda muda o retrato inteiro e o usuário está olhando para a
     * tela esperando o número novo.
     */
    public boolean isAltoImpacto() {
        return altoImpacto;
    }
}
