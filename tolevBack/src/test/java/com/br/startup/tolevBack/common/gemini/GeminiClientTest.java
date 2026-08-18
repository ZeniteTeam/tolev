package com.br.startup.tolevBack.common.gemini;

import com.br.startup.tolevBack.common.gemini.dto.GeminiRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class GeminiClientTest {

    private static final String BASE_URL = "https://gemini.test/v1beta";

    private MockRestServiceServer server;
    private GeminiClient client;

    private GeminiProperties props(String apiKey) {
        return new GeminiProperties(apiKey, "gemini-2.5-flash", BASE_URL, Duration.ofSeconds(5));
    }

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE_URL);
        server = MockRestServiceServer.bindTo(builder).build();
        client = new GeminiClient(props("chave-de-teste"), builder.build());
    }

    @Test
    void mandaOPromptEDevolveOTextoGerado() {
        server.expect(requestTo(BASE_URL + "/models/gemini-2.5-flash:generateContent"))
                .andExpect(header("x-goog-api-key", "chave-de-teste"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.contents[0].parts[0].text").value("Oi"))
                .andExpect(jsonPath("$.contents[0].role").value("user"))
                .andRespond(withSuccess("""
                        {"candidates":[{"content":{"role":"model",
                        "parts":[{"text":"Olá!"}]},"finishReason":"STOP"}]}
                        """, MediaType.APPLICATION_JSON));

        assertThat(client.gerarTexto("Oi")).isEqualTo("Olá!");
        server.verify();
    }

    @Test
    void instrucaoDeSistemaVaiNoCampoProprio() {
        server.expect(requestTo(BASE_URL + "/models/gemini-2.5-flash:generateContent"))
                .andExpect(jsonPath("$.systemInstruction.parts[0].text").value("Seja breve"))
                .andRespond(withSuccess("""
                        {"candidates":[{"content":{"parts":[{"text":"ok"}]}}]}
                        """, MediaType.APPLICATION_JSON));

        assertThat(client.gerarTexto("Seja breve", "Oi")).isEqualTo("ok");
        server.verify();
    }

    @Test
    void camposNulosNaoVaoNoJson() {
        // A API rejeita "generationConfig": null — o @JsonInclude tem que cortar.
        server.expect(requestTo(BASE_URL + "/models/gemini-2.5-flash:generateContent"))
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("generationConfig"))))
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("systemInstruction"))))
                .andRespond(withSuccess("""
                        {"candidates":[{"content":{"parts":[{"text":"ok"}]}}]}
                        """, MediaType.APPLICATION_JSON));

        client.gerarTexto("Oi");
        server.verify();
    }

    @Test
    void partesMultiplasSaoConcatenadas() {
        server.expect(requestTo(BASE_URL + "/models/gemini-2.5-flash:generateContent"))
                .andRespond(withSuccess("""
                        {"candidates":[{"content":{"parts":[{"text":"Parte 1. "},{"text":"Parte 2."}]}}]}
                        """, MediaType.APPLICATION_JSON));

        assertThat(client.gerarTexto("Oi")).isEqualTo("Parte 1. Parte 2.");
    }

    @Test
    void modeloExplicitoSobrepoeOConfigurado() {
        server.expect(requestTo(BASE_URL + "/models/gemini-2.5-pro:generateContent"))
                .andRespond(withSuccess("""
                        {"candidates":[{"content":{"parts":[{"text":"ok"}]}}]}
                        """, MediaType.APPLICATION_JSON));

        client.gerar(GeminiRequest.de("Oi"), "gemini-2.5-pro");
        server.verify();
    }

    @Test
    void respostaSemTextoViraErroComOFinishReason() {
        server.expect(requestTo(BASE_URL + "/models/gemini-2.5-flash:generateContent"))
                .andRespond(withSuccess("""
                        {"candidates":[{"content":{"parts":[]},"finishReason":"SAFETY"}]}
                        """, MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> client.gerarTexto("Oi"))
                .isInstanceOf(GeminiException.class)
                .hasMessageContaining("SAFETY");
    }

    @Test
    void erroHttpViraGeminiException() {
        server.expect(requestTo(BASE_URL + "/models/gemini-2.5-flash:generateContent"))
                .andRespond(withServerError());

        assertThatThrownBy(() -> client.gerarTexto("Oi"))
                .isInstanceOf(GeminiException.class)
                .hasMessageContaining("Falha ao chamar o Gemini");
    }

    @Test
    void semChaveConfiguradaNaoChamaAApi() {
        GeminiClient semChave = new GeminiClient(props(""), RestClient.builder().build());

        assertThatThrownBy(() -> semChave.gerarTexto("Oi"))
                .isInstanceOf(GeminiException.class)
                .hasMessageContaining("GEMINI_API_KEY");
    }

    @Test
    void promptVazioERejeitadoAntesDaChamada() {
        assertThatThrownBy(() -> client.gerarTexto("  "))
                .isInstanceOf(GeminiException.class)
                .hasMessageContaining("Prompt vazio");
    }

    @Test
    void propriedadesTemPadraoQuandoNaoConfiguradas() {
        GeminiProperties padrao = new GeminiProperties("k", null, null, null);

        assertThat(padrao.model()).isEqualTo("gemini-2.5-flash");
        assertThat(padrao.baseUrl()).isEqualTo("https://generativelanguage.googleapis.com/v1beta");
        assertThat(padrao.timeout()).isEqualTo(Duration.ofSeconds(30));
        assertThat(padrao.configurado()).isTrue();
    }
}
