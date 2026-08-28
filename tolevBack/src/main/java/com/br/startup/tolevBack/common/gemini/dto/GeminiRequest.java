package com.br.startup.tolevBack.common.gemini.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Corpo do {@code :generateContent} da API Gemini.
 *
 * <p>Campos nulos são omitidos do JSON — a API rejeita objetos vazios como
 * {@code "generationConfig": null}.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GeminiRequest(
        List<Content> contents,
        Content systemInstruction,
        GenerationConfig generationConfig
) {

    /** Um turno da conversa. {@code role} é "user" ou "model". */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Content(String role, List<Part> parts) {

        public static Content user(String texto) {
            return new Content("user", List.of(new Part(texto)));
        }

        /** Instrução de sistema não leva role. */
        public static Content sistema(String texto) {
            return new Content(null, List.of(new Part(texto)));
        }
    }

    public record Part(String text) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record GenerationConfig(Double temperature, Integer maxOutputTokens) {}

    /** Pergunta simples, sem instrução de sistema nem ajuste de geração. */
    public static GeminiRequest de(String prompt) {
        return new GeminiRequest(List.of(Content.user(prompt)), null, null);
    }

    /** Pergunta com instrução de sistema. */
    public static GeminiRequest de(String systemInstruction, String prompt) {
        return new GeminiRequest(
                List.of(Content.user(prompt)),
                systemInstruction == null || systemInstruction.isBlank()
                        ? null
                        : Content.sistema(systemInstruction),
                null);
    }
}
