package com.br.startup.tolevBack.analysis.application.service.analyzers;

import com.br.startup.tolevBack.common.gemini.GeminiClient;
import com.br.startup.tolevBack.common.gemini.GeminiProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Chamada manual e real ao Gemini para conferir a extração de um extrato PDF
 * de verdade — gasta cota da API e precisa de internet, então não é um teste
 * automatizado: o nome foge do padrão {@code *Test} que o Surefire roda no
 * {@code mvn test}/CI. Rode direto pela IDE quando quiser conferir à mão.
 *
 * <p>Pula sozinho (não falha) se faltar a chave ou o arquivo, pra nunca
 * quebrar uma execução em lote por engano.
 */
class ExtratoAnaliseManualCheck {

    private static final Path PDF = Path.of("src/test/testeextrato - Copia.pdf");

    @Test
    void extraiOExtratoDeVerdade() throws Exception {
        String apiKey = System.getenv("GEMINI_API_KEY");
        Assumptions.assumeTrue(apiKey != null && !apiKey.isBlank(),
                "GEMINI_API_KEY não está definida neste processo — configure na Run Configuration.");
        Assumptions.assumeTrue(Files.exists(PDF),
                "PDF não encontrado em " + PDF.toAbsolutePath() + " — confira o working directory da execução.");

        GeminiProperties properties = new GeminiProperties(
                apiKey, System.getenv("GEMINI_MODEL"), System.getenv("GEMINI_BASE_URL"), null);
        GeminiClient client = new GeminiClient(
                properties, RestClient.builder().baseUrl(properties.baseUrl()).build());
        ObjectMapper mapper = new ObjectMapper();
        ExtratoAnaliseService service = new ExtratoAnaliseService(client, mapper);

        byte[] pdf = Files.readAllBytes(PDF);

        System.out.println("Chamando o Gemini (modelo " + properties.model() + ")...");
        ExtratoAnaliseService.ExtratoExtraido resultado = service.analisar(pdf);

        System.out.println("=== Resultado da extração ===");
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultado));
    }
}
