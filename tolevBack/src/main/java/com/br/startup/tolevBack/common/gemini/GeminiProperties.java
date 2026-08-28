package com.br.startup.tolevBack.common.gemini;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Configuração do cliente Gemini, lida do prefixo {@code gemini.*}.
 *
 * <p>A chave nunca vai versionada: use a variável de ambiente {@code GEMINI_API_KEY}.
 *
 * @param apiKey  chave da API Google AI Studio; vazia desabilita o cliente
 * @param model   modelo usado quando a chamada não especifica outro
 * @param baseUrl raiz da API REST (permite apontar para um mock em teste)
 * @param timeout tempo máximo de conexão e de leitura por requisição
 */
@ConfigurationProperties(prefix = "gemini")
public record GeminiProperties(
        String apiKey,
        String model,
        String baseUrl,
        Duration timeout
) {
    public GeminiProperties {
        model = (model == null || model.isBlank()) ? "gemini-3.5-flash" : model;
        baseUrl = (baseUrl == null || baseUrl.isBlank())
                ? "https://generativelanguage.googleapis.com/v1beta"
                : baseUrl;
        timeout = timeout != null ? timeout : Duration.ofSeconds(30);
    }

    /** Sem chave configurada não adianta chamar a API. */
    public boolean configurado() {
        return apiKey != null && !apiKey.isBlank();
    }
}
