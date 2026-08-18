package com.br.startup.tolevBack.common.gemini.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Optional;

/**
 * Resposta do {@code :generateContent}. Só mapeia o que interessa — a API manda
 * bem mais campos (safety ratings, citações) que são ignorados.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GeminiResponse(
        List<Candidate> candidates,
        UsageMetadata usageMetadata
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Candidate(Content content, String finishReason) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Content(String role, List<Part> parts) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Part(String text) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record UsageMetadata(
            Integer promptTokenCount,
            Integer candidatesTokenCount,
            Integer totalTokenCount
    ) {}

    /**
     * Texto do primeiro candidato, com as partes concatenadas. Vazio quando o
     * modelo não devolveu nada (bloqueio de segurança, corte por token limit).
     */
    public Optional<String> texto() {
        if (candidates == null || candidates.isEmpty()) {
            return Optional.empty();
        }
        Content content = candidates.get(0).content();
        if (content == null || content.parts() == null) {
            return Optional.empty();
        }
        String texto = content.parts().stream()
                .map(Part::text)
                .filter(t -> t != null && !t.isBlank())
                .reduce("", String::concat);
        return texto.isBlank() ? Optional.empty() : Optional.of(texto);
    }

    /** Motivo de parada do primeiro candidato, útil para diagnosticar respostas vazias. */
    public String finishReason() {
        return candidates == null || candidates.isEmpty() ? null : candidates.get(0).finishReason();
    }
}
