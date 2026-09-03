package com.br.startup.tolevBack.shared.events;

/**
 * O que mudou na vida financeira do usuário. Descreve o fato, não a tela nem o
 * endpoint — quem publica não sabe (nem precisa saber) que existe uma análise
 * escutando do outro lado.
 */
public enum OrigemAlteracao {

    /** Lançamento manual de receita ou despesa. */
    TRANSACAO_CRIADA(false),

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
    PREFERENCIAS_ATUALIZADAS(true);

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
