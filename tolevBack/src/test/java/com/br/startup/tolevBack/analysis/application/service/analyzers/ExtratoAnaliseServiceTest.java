package com.br.startup.tolevBack.analysis.application.service.analyzers;

import com.br.startup.tolevBack.common.gemini.GeminiClient;
import com.br.startup.tolevBack.common.gemini.GeminiException;
import com.br.startup.tolevBack.common.gemini.GeminiProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ExtratoAnaliseServiceTest {

    private static final String BASE_URL = "https://gemini.test/v1beta";
    private static final byte[] PDF_FALSO = "%PDF-1.4 conteudo falso".getBytes();

    private MockRestServiceServer server;
    private ExtratoAnaliseService service;

    @BeforeEach
    void setUp() {
        GeminiProperties props = new GeminiProperties("chave-de-teste", "gemini-2.5-flash", BASE_URL, Duration.ofSeconds(5));
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE_URL);
        server = MockRestServiceServer.bindTo(builder).build();
        GeminiClient client = new GeminiClient(props, builder.build());
        service = new ExtratoAnaliseService(client, new ObjectMapper());
    }

    @Test
    void mandaOPdfComoArquivoEForcaRespostaJson() {
        server.expect(requestTo(BASE_URL + "/models/gemini-2.5-flash:generateContent"))
                .andExpect(jsonPath("$.contents[0].parts[0].inlineData.mimeType").value("application/pdf"))
                .andExpect(jsonPath("$.contents[0].parts[1].text").exists())
                .andExpect(jsonPath("$.systemInstruction.parts[0].text").exists())
                .andExpect(jsonPath("$.generationConfig.responseMimeType").value("application/json"))
                .andRespond(withSuccess("""
                        {"candidates":[{"content":{"parts":[{"text":"{\\"transacoes\\":[{\\"tipo\\":\\"ENTRADA\\",\\"valor\\":100.00,\\"descricao\\":\\"salario\\"}],\\"totalEntradas\\":100.00,\\"totalSaidas\\":0.00,\\"tipoMaisFrequente\\":\\"ENTRADA\\",\\"transacaoMaiorValor\\":{\\"tipo\\":\\"ENTRADA\\",\\"valor\\":100.00,\\"descricao\\":\\"salario\\"}}"}]},"finishReason":"STOP"}]}
                        """, MediaType.APPLICATION_JSON));

        ExtratoAnaliseService.ExtratoExtraido resultado = service.analisar(PDF_FALSO);

        assertThat(resultado.transacoes()).hasSize(1);
        assertThat(resultado.totalEntradas()).isEqualByComparingTo(BigDecimal.valueOf(100.00));
        assertThat(resultado.tipoMaisFrequente()).isEqualTo(ExtratoAnaliseService.TipoLancamento.ENTRADA);
        server.verify();
    }

    @Test
    void respostaForaDoFormatoViraGeminiException() {
        server.expect(requestTo(BASE_URL + "/models/gemini-2.5-flash:generateContent"))
                .andRespond(withSuccess("""
                        {"candidates":[{"content":{"parts":[{"text":"isso não é JSON"}]},"finishReason":"STOP"}]}
                        """, MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> service.analisar(PDF_FALSO))
                .isInstanceOf(GeminiException.class)
                .hasMessageContaining("não veio no formato esperado");
    }

    @Test
    void pdfVazioERejeitadoAntesDaChamada() {
        assertThatThrownBy(() -> service.analisar(new byte[0]))
                .isInstanceOf(GeminiException.class)
                .hasMessageContaining("PDF vazio");
    }
}
