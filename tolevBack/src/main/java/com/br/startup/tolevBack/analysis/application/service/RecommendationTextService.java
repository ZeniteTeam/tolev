package com.br.startup.tolevBack.analysis.application.service;

import com.br.startup.tolevBack.analysis.internal.enums.RegraAnalise;
import com.br.startup.tolevBack.common.gemini.GeminiClient;
import com.br.startup.tolevBack.common.gemini.GeminiProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * O texto que o usuário lê na recomendação.
 *
 * <p>A regra decide <em>o que</em> recomendar e calcula os números; aqui só se
 * decide <em>como falar</em>. Com o Gemini configurado, ele reescreve o template
 * em linguagem mais natural a partir dos mesmos números; sem chave, com erro de
 * rede ou com resposta fora do formato, sai o template — que já é uma frase
 * completa e correta.
 *
 * <p>Essa separação é deliberada: conselho financeiro não pode depender de um
 * modelo generativo acertar a aritmética.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationTextService {

    private static final String INSTRUCAO = """
            Você é um consultor financeiro brasileiro falando com uma pessoa comum \
            no aplicativo Tolev. Reescreva o título e a descrição recebidos de forma \
            mais natural e acolhedora, em português do Brasil.

            Regras obrigatórias:
            - Use exatamente os mesmos valores numéricos que aparecem no texto original. \
            Não invente, não arredonde e não estime nenhum número.
            - Não prometa resultado nem garanta economia.
            - Trate a pessoa por "você". Sem emoji, sem exclamação.
            - Título: no máximo 60 caracteres. Descrição: no máximo 300 caracteres, 1 a 2 frases.
            - Responda somente com um JSON no formato {"titulo": "...", "descricao": "..."}, \
            sem cercas de código e sem comentários.
            """;

    private final GeminiClient geminiClient;
    private final GeminiProperties geminiProperties;
    private final ObjectMapper objectMapper;

    /**
     * @return sempre um texto utilizável — o do modelo quando ele coopera, o do
     *         template quando não.
     */
    public Texto gerar(RegraAnalise regra, Map<String, String> dados) {
        Texto template = new Texto(regra.titulo(dados), regra.descricao(dados));

        if (!geminiProperties.configurado()) {
            return template;
        }
        try {
            String resposta = geminiClient.gerarTexto(INSTRUCAO, montarPrompt(template));
            return interpretar(resposta, template);
        } catch (Exception e) {
            log.warn("Recomendação {} ficou com o texto padrão: {}", regra, e.getMessage());
            return template;
        }
    }

    private String montarPrompt(Texto template) {
        return """
                Título: %s
                Descrição: %s
                """.formatted(template.titulo(), template.descricao());
    }

    /**
     * O modelo às vezes devolve o JSON embrulhado em ```json. Em vez de tentar
     * adivinhar todos os formatos possíveis, recorta do primeiro '{' ao último
     * '}' e desiste no primeiro sinal de problema.
     */
    private Texto interpretar(String resposta, Texto template) {
        if (resposta == null || resposta.isBlank()) {
            return template;
        }
        int inicio = resposta.indexOf('{');
        int fim = resposta.lastIndexOf('}');
        if (inicio < 0 || fim <= inicio) {
            return template;
        }
        try {
            JsonNode json = objectMapper.readTree(resposta.substring(inicio, fim + 1));
            String titulo = texto(json, "titulo", template.titulo());
            String descricao = texto(json, "descricao", template.descricao());
            return new Texto(titulo, descricao);
        } catch (Exception e) {
            log.warn("Resposta do Gemini não era JSON válido; usando texto padrão.");
            return template;
        }
    }

    private String texto(JsonNode json, String campo, String padrao) {
        JsonNode valor = json.get(campo);
        if (valor == null || !valor.isTextual() || valor.asText().isBlank()) {
            return padrao;
        }
        return valor.asText().trim();
    }

    public record Texto(String titulo, String descricao) {
    }
}
