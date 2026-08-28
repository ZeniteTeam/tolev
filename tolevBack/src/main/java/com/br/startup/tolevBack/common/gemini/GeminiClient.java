package com.br.startup.tolevBack.common.gemini;

import com.br.startup.tolevBack.common.gemini.dto.GeminiRequest;
import com.br.startup.tolevBack.common.gemini.dto.GeminiResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Cliente HTTP da API Gemini (Google AI Studio).
 *
 * <p>Uso típico:
 * <pre>{@code
 * String resposta = geminiClient.gerarTexto("Resuma esta dívida em uma frase");
 * }</pre>
 *
 * <p>Para controle fino (temperatura, histórico de conversa, outro modelo), monte
 * um {@link GeminiRequest} e chame {@link #gerar(GeminiRequest, String)}.
 *
 * <p>É um requester puro: não sabe nada de dívidas nem de usuários. Quem precisar
 * de prompt de domínio monta o texto no próprio módulo e chama daqui.
 */
@Component
public class GeminiClient {

    private final GeminiProperties properties;
    private final RestClient restClient;

    public GeminiClient(GeminiProperties properties, @Qualifier("geminiRestClient") RestClient restClient) {
        this.properties = properties;
        this.restClient = restClient;
    }

    /** Pergunta simples: manda o prompt e devolve o texto da resposta. */
    public String gerarTexto(String prompt) {
        return gerarTexto(null, prompt);
    }

    /**
     * @param systemInstruction como o modelo deve se comportar; pode ser nulo
     * @param prompt            a pergunta em si
     * @return o texto gerado
     * @throws GeminiException se a chamada falhar ou o modelo não devolver texto
     */
    public String gerarTexto(String systemInstruction, String prompt) {
        if (prompt == null || prompt.isBlank()) {
            throw new GeminiException("Prompt vazio.");
        }
        GeminiResponse resposta = gerar(GeminiRequest.de(systemInstruction, prompt), null);
        return resposta.texto().orElseThrow(() -> new GeminiException(
                "Gemini não retornou texto (finishReason: " + resposta.finishReason() + ")."));
    }

    /**
     * Chamada crua ao {@code :generateContent}.
     *
     * @param request corpo completo da requisição
     * @param model   modelo a usar; nulo cai no configurado em {@code gemini.model}
     * @throws GeminiException se a API responder erro ou o corpo vier vazio
     */
    public GeminiResponse gerar(GeminiRequest request, String model) {
        if (!properties.configurado()) {
            throw new GeminiException(
                    "GEMINI_API_KEY não configurada — defina a variável de ambiente para usar o Gemini.");
        }

        String modelo = (model == null || model.isBlank()) ? properties.model() : model;

        try {
            GeminiResponse resposta = restClient.post()
                    .uri("/models/{model}:generateContent", modelo)
                    // Chave no header, não na query: evita vazar em log de acesso.
                    .header("x-goog-api-key", properties.apiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(GeminiResponse.class);

            if (resposta == null) {
                throw new GeminiException("Gemini respondeu com corpo vazio.");
            }
            return resposta;
        } catch (RestClientException ex) {
            throw new GeminiException("Falha ao chamar o Gemini (modelo " + modelo + ").", ex);
        }
    }
}
