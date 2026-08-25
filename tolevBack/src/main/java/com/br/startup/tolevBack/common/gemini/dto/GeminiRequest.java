package com.br.startup.tolevBack.common.gemini.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Base64;
import java.util.List;
import java.util.Map;

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
            return new Content("user", List.of(Part.texto(texto)));
        }

        /** Instrução de sistema não leva role. */
        public static Content sistema(String texto) {
            return new Content(null, List.of(Part.texto(texto)));
        }

        /**
         * Turno do usuário com um arquivo anexado (ex.: PDF) e um texto de apoio.
         * O arquivo vem primeiro nas parts — é a ordem recomendada pela API para
         * leitura de documentos.
         */
        public static Content userComArquivo(String mimeType, byte[] arquivo, String texto) {
            return new Content("user", List.of(Part.arquivo(mimeType, arquivo), Part.texto(texto)));
        }
    }

    /** Uma parte do turno: ou texto, ou um arquivo inline em base64 — nunca os dois. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Part(String text, InlineData inlineData) {

        public static Part texto(String texto) {
            return new Part(texto, null);
        }

        public static Part arquivo(String mimeType, byte[] dados) {
            return new Part(null, new InlineData(mimeType, Base64.getEncoder().encodeToString(dados)));
        }
    }

    /** Arquivo pequeno embutido direto no corpo da requisição, em base64. */
    public record InlineData(String mimeType, String data) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record GenerationConfig(
            Double temperature,
            Integer maxOutputTokens,
            String responseMimeType,
            Schema responseSchema
    ) {}

    /**
     * Subconjunto do schema OpenAPI que a API aceita em {@code responseSchema}.
     * Ao contrário de só pedir um formato no prompt, isso restringe a própria
     * geração token a token — inclusive valores de enum, que uma instrução em
     * texto pode falhar em cumprir.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Schema(
            String type,
            Map<String, Schema> properties,
            Schema items,
            List<String> required,
            @JsonProperty("enum") List<String> enumValues
    ) {
        public static Schema string() {
            return new Schema("STRING", null, null, null, null);
        }

        public static Schema stringEnum(String... valores) {
            return new Schema("STRING", null, null, null, List.of(valores));
        }

        public static Schema number() {
            return new Schema("NUMBER", null, null, null, null);
        }

        public static Schema array(Schema itens) {
            return new Schema("ARRAY", null, itens, null, null);
        }

        public static Schema object(Map<String, Schema> properties, String... obrigatorios) {
            return new Schema("OBJECT", properties, null, List.of(obrigatorios), null);
        }
    }

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
