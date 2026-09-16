package com.br.startup.tolevBack.finance.application.service;

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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Lê um extrato bancário em PDF via Gemini e devolve os lançamentos extraídos.
 *
 * <p>Ao contrário do {@code RecommendationTextService}, aqui não existe um
 * "template" para cair de volta: os dados extraídos são o próprio resultado, não
 * um polimento de texto. Por isso, falha do Gemini ou resposta fora do formato
 * viram {@link GeminiException} e sobem para quem chamou decidir o que fazer
 * (normalmente, pedir o PDF de novo).
 *
 * <p>Mora em {@code finance} porque o que sai daqui é dado financeiro do
 * usuário — transação, não análise. Quem interpreta o resultado é o
 * {@code ImportExtratoService}, ao lado.
 *
 * <p><strong>O que este serviço deliberadamente não faz:</strong> decidir o que
 * é lançamento novo. A janela já importada é uma comparação de datas em Java,
 * determinística e testável; delegá-la ao modelo trocaria uma regra exata por
 * um palpite.
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
            - "categoria" é a categoria de gasto mais próxima do lançamento, \
            escolhida obrigatoriamente da lista de valores permitidos. Baseie-se na \
            descrição do lançamento. Um lançamento "ENTRADA" só pode receber uma \
            categoria de receita; um lançamento "SAIDA" só pode receber uma \
            categoria de despesa. Quando a descrição não deixar claro, use a \
            categoria genérica ("Outros" para saída, "Outras Receitas" para \
            entrada) em vez de escolher uma categoria específica no chute.
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
                {"tipo": "ENTRADA ou SAIDA", "valor": 123.45, "descricao": "texto curto do lançamento", "dataTransacao": "AAAA-MM-DD", "categoria": "uma das categorias permitidas"}
              ],
              "totalEntradas": 0.00,
              "totalSaidas": 0.00,
              "tipoMaisFrequente": "ENTRADA ou SAIDA",
              "tipoTransacaoMaisFrequente": "PIX ou CREDITO ou DEBITO",
              "transacaoMaiorValor": {"tipo": "ENTRADA ou SAIDA", "valor": 0.00, "descricao": "texto curto", "dataTransacao": "AAAA-MM-DD", "categoria": "uma das categorias permitidas"}
            }
            """;

    private final GeminiClient geminiClient;
    private final ObjectMapper objectMapper;

    /**
     * @param pdf         bytes crus do arquivo PDF recebido do frontend
     * @param categorias  nomes das categorias que o modelo pode usar — vêm do
     *                    catálogo do banco, não de uma lista fixa aqui, para
     *                    catálogo e prompt nunca saírem de sincronia
     * @throws GeminiException se o Gemini não conseguir ler o extrato ou devolver
     *                         uma resposta fora do formato esperado
     */
    public ExtratoExtraido analisar(byte[] pdf, List<String> categorias) {
        if (pdf == null || pdf.length == 0) {
            throw new GeminiException("PDF vazio.");
        }
        if (categorias == null || categorias.isEmpty()) {
            throw new GeminiException("Nenhuma categoria ativa no catálogo para classificar o extrato.");
        }

        GeminiRequest request = new GeminiRequest(
                List.of(GeminiRequest.Content.userComArquivo(
                        MIME_TYPE_PDF, pdf, "Extraia os dados deste extrato bancário conforme as instruções.")),
                GeminiRequest.Content.sistema(INSTRUCAO),
                new GeminiRequest.GenerationConfig(0.0, null, "application/json", schemaExtrato(categorias)));

        GeminiResponse resposta = geminiClient.gerar(request, null);
        String json = resposta.texto().orElseThrow(() -> new GeminiException(
                "Gemini não extraiu nada do extrato (finishReason: " + resposta.finishReason() + ")."));

        return interpretar(json, resposta.finishReason());
    }

    /**
     * O enum de categorias entra no {@code responseSchema}, não só no texto da
     * instrução: assim a API restringe a geração token a token e é impossível
     * voltar um nome de categoria que não existe no catálogo. Sobra para o Java
     * apenas conferir se a categoria escolhida combina com o tipo do lançamento.
     */
    private GeminiRequest.Schema schemaExtrato(List<String> categorias) {
        GeminiRequest.Schema lancamento = schemaLancamento(categorias);

        Map<String, GeminiRequest.Schema> campos = new LinkedHashMap<>();
        campos.put("transacoes", GeminiRequest.Schema.array(lancamento));
        campos.put("totalEntradas", GeminiRequest.Schema.number());
        campos.put("totalSaidas", GeminiRequest.Schema.number());
        campos.put("tipoMaisFrequente", GeminiRequest.Schema.stringEnum("ENTRADA", "SAIDA"));
        campos.put("tipoTransacaoMaisFrequente",
                GeminiRequest.Schema.stringEnum("PIX", "CREDITO", "DEBITO"));
        campos.put("transacaoMaiorValor", lancamento);

        return GeminiRequest.Schema.object(campos,
                "transacoes", "totalEntradas", "totalSaidas", "tipoMaisFrequente",
                "tipoTransacaoMaisFrequente", "transacaoMaiorValor");
    }

    private GeminiRequest.Schema schemaLancamento(List<String> categorias) {
        Map<String, GeminiRequest.Schema> campos = new LinkedHashMap<>();
        campos.put("tipo", GeminiRequest.Schema.stringEnum("ENTRADA", "SAIDA"));
        campos.put("valor", GeminiRequest.Schema.number());
        campos.put("descricao", GeminiRequest.Schema.string());
        campos.put("dataTransacao", GeminiRequest.Schema.string());
        campos.put("categoria", GeminiRequest.Schema.stringEnum(categorias.toArray(String[]::new)));

        return GeminiRequest.Schema.object(campos,
                "tipo", "valor", "descricao", "dataTransacao", "categoria");
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

    /** @param categoria nome de uma categoria do catálogo; validada contra o tipo por quem importa */
    public record Lancamento(
            TipoLancamento tipo,
            BigDecimal valor,
            String descricao,
            LocalDate dataTransacao,
            String categoria
    ) {
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
