package com.br.startup.tolevBack.common.gemini;

/** Falha ao chamar a API do Gemini ou ao interpretar a resposta dela. */
public class GeminiException extends RuntimeException {

    public GeminiException(String message) {
        super(message);
    }

    public GeminiException(String message, Throwable cause) {
        super(message, cause);
    }
}
