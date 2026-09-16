package com.br.startup.tolevBack.common.gemini;

/**
 * Não há {@code GEMINI_API_KEY} — nenhuma chamada chegou a sair.
 *
 * <p>Separada de {@link GeminiException} porque o desfecho é outro: as demais
 * falhas dizem algo sobre o pedido (o PDF não abriu, a resposta veio torta) e
 * pedir para tentar de novo faz sentido. Esta diz apenas que o servidor está mal
 * configurado, e nada que o usuário faça com o arquivo dele vai mudar isso —
 * mandá-lo conferir o PDF seria mandá-lo procurar defeito onde não há.
 */
public class GeminiNaoConfiguradoException extends GeminiException {

    public GeminiNaoConfiguradoException(String message) {
        super(message);
    }
}
