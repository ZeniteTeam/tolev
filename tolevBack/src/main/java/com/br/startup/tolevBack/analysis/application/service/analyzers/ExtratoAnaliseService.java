package com.br.startup.tolevBack.analysis.application.service.analyzers;

import com.br.startup.tolevBack.common.gemini.GeminiClient;
import com.br.startup.tolevBack.common.gemini.GeminiException;
import com.br.startup.tolevBack.common.gemini.dto.GeminiRequest;
import com.br.startup.tolevBack.common.gemini.dto.GeminiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Lê um extrato bancário em PDF via Gemini e devolve os lançamentos extraídos.
 *
 * <p>Ao contrário do {@link com.br.startup.tolevBack.analysis.application.service.RecommendationTextService},
 * aqui não existe um "template" para cair de volta: os dados extraídos são o
 * próprio resultado, não um polimento de texto. Por isso, falha do Gemini ou
 * resposta fora do formato viram {@link GeminiException} e sobem para quem
 * chamou decidir o que fazer (normalmente, pedir o PDF de novo).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExtratoAnaliseService {

    private static final String MIME_TYPE_PDF = "application/pdf";

    private static final String INSTRUCAO = """
            Você é um analista financeiro. Leia o extrato bancário em PDF anexado \
            e extraia os lançamentos.

            Regras obrigatórias:
            - Use somente informações presentes no documento. Não invente, não \
            estime e não arredonde valores.
            - Alguns dados foram omitidos do PDF por segurança; quando um campo \
            não estiver visível, não tente adivinhar — reflita na lista só o que \
            realmente está no documento.
            - "tipo" de cada lançamento é exatamente "ENTRADA" (dinheiro recebido) \
            ou "SAIDA" (dinheiro gasto). Nenhum outro valor é aceito.
            - Cada lançamento no PDF tem uma data associada (geralmente no formato \
            DD/MM/AAAA ou DD/MM, ao lado ou próxima da descrição). Identifique essa \
            data no documento e converta para "dataTransacao" no formato \
            "AAAA-MM-DD". Se o extrato não indicar o ano, use o ano do período do \
            extrato.
            - "totalEntradas" e "totalSaidas" são a soma dos valores de cada tipo.
            - "tipoMaisFrequente" é o tipo (ENTRADA ou SAIDA) que aparece mais \
            vezes na lista de lançamentos.
            - "tipoTransacaoMaisFrequente" é o método de pagamento que aparece \
            mais vezes no extrato, e é exatamente "PIX", "CREDITO" ou "DEBITO" \
            (sem acento). Nenhum outro valor é aceito.
            - "transacaoMaiorValor" é o lançamento de maior valor absoluto do extrato.
            - Responda SOMENTE com um JSON válido, sem cercas de código, sem \
            comentários e sem texto fora do JSON, exatamente no formato abaixo:

            {
              "transacoes": [
                {"tipo": "ENTRADA ou SAIDA", "valor": 123.45, "descricao": "texto curto do lançamento", "dataTransacao": "AAAA-MM-DD"}
              ],
              "totalEntradas": 0.00,
              "totalSaidas": 0.00,
              "tipoMaisFrequente": "ENTRADA ou SAIDA",
              "tipoTransacaoMaisFrequente": "PIX ou CREDITO ou DEBITO",
              "transacaoMaiorValor": {"tipo": "ENTRADA ou SAIDA", "valor": 0.00, "descricao": "texto curto", "dataTransacao": "AAAA-MM-DD"}
            }
            """;

    private static final GeminiRequest.Schema SCHEMA_LANCAMENTO = GeminiRequest.Schema.object(
            Map.of(
                    "tipo", GeminiRequest.Schema.stringEnum("ENTRADA", "SAIDA"),
                    "valor", GeminiRequest.Schema.number(),
                    "descricao", GeminiRequest.Schema.string(),
                    "dataTransacao", GeminiRequest.Schema.string()),
            "tipo", "valor", "descricao", "dataTransacao");

    private static final GeminiRequest.Schema SCHEMA_EXTRATO = GeminiRequest.Schema.object(
            Map.of(
                    "transacoes", GeminiRequest.Schema.array(SCHEMA_LANCAMENTO),
                    "totalEntradas", GeminiRequest.Schema.number(),
                    "totalSaidas", GeminiRequest.Schema.number(),
                    "tipoMaisFrequente", GeminiRequest.Schema.stringEnum("ENTRADA", "SAIDA"),
                    "tipoTransacaoMaisFrequente", GeminiRequest.Schema.stringEnum("PIX", "CREDITO", "DEBITO"),
                    "transacaoMaiorValor", SCHEMA_LANCAMENTO),
            "transacoes", "totalEntradas", "totalSaidas", "tipoMaisFrequente",
            "tipoTransacaoMaisFrequente", "transacaoMaiorValor");

    private final GeminiClient geminiClient;
    private final ObjectMapper objectMapper;

    /**
     * @param pdf bytes crus do arquivo PDF recebido do frontend
     * @throws GeminiException se o Gemini não conseguir ler o extrato ou devolver
     *                         uma resposta fora do formato esperado
     */
    public ExtratoExtraido analisar(byte[] pdf) {
        if (pdf == null || pdf.length == 0) {
            throw new GeminiException("PDF vazio.");
        }

        GeminiRequest request = new GeminiRequest(
                List.of(GeminiRequest.Content.userComArquivo(
                        MIME_TYPE_PDF, pdf, "Extraia os dados deste extrato bancário conforme as instruções.")),
                GeminiRequest.Content.sistema(INSTRUCAO),
                new GeminiRequest.GenerationConfig(0.0, null, "application/json", SCHEMA_EXTRATO));

        GeminiResponse resposta = geminiClient.gerar(request, null);
        String json = resposta.texto().orElseThrow(() -> new GeminiException(
                "Gemini não extraiu nada do extrato (finishReason: " + resposta.finishReason() + ")."));

        return interpretar(json, resposta.finishReason());
    }

    private ExtratoExtraido interpretar(String json, String finishReason) {
        try {
            return objectMapper.readValue(json, ExtratoExtraido.class);
        } catch (Exception e) {
            log.warn("Resposta do Gemini fora do formato esperado (finishReason: {}): {}", finishReason, json, e);
            throw new GeminiException("Resposta do Gemini não veio no formato esperado.", e);
        }
    }

    public enum TipoLancamento {
        ENTRADA, SAIDA
    }

    public enum TipoTransacaoMaisFrequente {
        PIX, CREDITO, DEBITO
    }

    public record Lancamento(TipoLancamento tipo, BigDecimal valor, String descricao, LocalDate dataTransacao) {
    }

    public record ExtratoExtraido(
            List<Lancamento> transacoes,
            BigDecimal totalEntradas,
            BigDecimal totalSaidas,
            TipoLancamento tipoMaisFrequente,
            TipoTransacaoMaisFrequente tipoTransacaoMaisFrequente,
            Lancamento transacaoMaiorValor
    ) {
    }
}
