package com.br.startup.tolevBack.finance.application.service;

import com.br.startup.tolevBack.common.gemini.GeminiClient;
import com.br.startup.tolevBack.common.gemini.GeminiException;
import com.br.startup.tolevBack.common.gemini.GeminiProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ExtratoAnaliseServiceTest {

    private static final String BASE_URL = "https://gemini.test/v1beta";
    private static final byte[] PDF_FALSO = "%PDF-1.4 conteudo falso".getBytes();

    /** O catalogo real vem do banco; aqui basta uma amostra para o enum do schema. */
    private static final List<String> CATEGORIAS = List.of("Alimentação", "Outros", "Salário");

    private MockRestServiceServer server;
    private ExtratoAnaliseService service;

    @BeforeEach
    void setUp() {
        GeminiProperties props = new GeminiProperties("chave-de-teste", "gemini-2.5-flash", BASE_URL, Duration.ofSeconds(5));
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE_URL);
        server = MockRestServiceServer.bindTo(builder).build();
        GeminiClient client = new GeminiClient(props, builder.build());
        service = new ExtratoAnaliseService(client, new ObjectMapper().registerModule(new JavaTimeModule()));
    }

    @Test
    void mandaOPdfComoArquivoEForcaRespostaJson() {
        server.expect(requestTo(BASE_URL + "/models/gemini-2.5-flash:generateContent"))
                .andExpect(jsonPath("$.contents[0].parts[0].inlineData.mimeType").value("application/pdf"))
                .andExpect(jsonPath("$.contents[0].parts[1].text").exists())
                .andExpect(jsonPath("$.systemInstruction.parts[0].text").exists())
                .andExpect(jsonPath("$.generationConfig.responseMimeType").value("application/json"))
                .andExpect(jsonPath("$.generationConfig.responseSchema.type").value("OBJECT"))
                .andExpect(jsonPath("$.generationConfig.responseSchema.properties.tipoTransacaoMaisFrequente.enum")
                        .value(org.hamcrest.Matchers.containsInAnyOrder("PIX", "CREDITO", "DEBITO")))
                // O catalogo entra como enum do schema: o modelo nao consegue
                // devolver uma categoria que nao existe no banco.
                .andExpect(jsonPath("$.generationConfig.responseSchema.properties.transacoes.items.properties.categoria.enum")
                        .value(org.hamcrest.Matchers.contains("Alimentação", "Outros", "Salário")))
                .andRespond(withSuccess("""
                        {"candidates":[{"content":{"parts":[{"text":"{\\"transacoes\\":[{\\"tipo\\":\\"ENTRADA\\",\\"valor\\":100.00,\\"descricao\\":\\"salario\\",\\"dataTransacao\\":\\"2024-01-15\\",\\"categoria\\":\\"Salário\\"}],\\"totalEntradas\\":100.00,\\"totalSaidas\\":0.00,\\"tipoMaisFrequente\\":\\"ENTRADA\\",\\"tipoTransacaoMaisFrequente\\":\\"PIX\\",\\"transacaoMaiorValor\\":{\\"tipo\\":\\"ENTRADA\\",\\"valor\\":100.00,\\"descricao\\":\\"salario\\",\\"dataTransacao\\":\\"2024-01-15\\",\\"categoria\\":\\"Salário\\"}}"}]},"finishReason":"STOP"}]}
                        """, MediaType.APPLICATION_JSON));

        ExtratoAnaliseService.ExtratoExtraido resultado = service.analisar(PDF_FALSO, CATEGORIAS);

        assertThat(resultado.transacoes()).hasSize(1);
        assertThat(resultado.totalEntradas()).isEqualByComparingTo(BigDecimal.valueOf(100.00));
        assertThat(resultado.tipoMaisFrequente()).isEqualTo(ExtratoAnaliseService.TipoLancamento.ENTRADA);
        assertThat(resultado.tipoTransacaoMaisFrequente()).isEqualTo(ExtratoAnaliseService.TipoTransacaoMaisFrequente.PIX);
        assertThat(resultado.transacoes().get(0).dataTransacao()).isEqualTo(LocalDate.of(2024, 1, 15));
        assertThat(resultado.transacoes().get(0).categoria()).isEqualTo("Salário");
        server.verify();
    }

    @Test
    void respostaForaDoFormatoViraGeminiException() {
        server.expect(requestTo(BASE_URL + "/models/gemini-2.5-flash:generateContent"))
                .andRespond(withSuccess("""
                        {"candidates":[{"content":{"parts":[{"text":"isso não é JSON"}]},"finishReason":"STOP"}]}
                        """, MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> service.analisar(PDF_FALSO, CATEGORIAS))
                .isInstanceOf(GeminiException.class)
                .hasMessageContaining("não veio no formato esperado");
    }

    @Test
    void catalogoVazioERejeitadoAntesDaChamada() {
        // Sem categorias nao da para montar o enum do schema, e mandar o extrato
        // sem restricao deixaria o modelo inventar nome de categoria.
        assertThatThrownBy(() -> service.analisar(PDF_FALSO, List.of()))
                .isInstanceOf(GeminiException.class)
                .hasMessageContaining("Nenhuma categoria ativa");
    }

    @Test
    void pdfVazioERejeitadoAntesDaChamada() {
        assertThatThrownBy(() -> service.analisar(new byte[0], CATEGORIAS))
                .isInstanceOf(GeminiException.class)
                .hasMessageContaining("PDF vazio");
    }
}
