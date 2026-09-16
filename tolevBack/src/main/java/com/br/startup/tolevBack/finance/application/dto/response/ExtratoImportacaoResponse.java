package com.br.startup.tolevBack.finance.application.dto.response;

import com.br.startup.tolevBack.finance.internal.enums.StatusImportacaoExtrato;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * O estado de um upload de extrato, do envio ao resultado.
 *
 * <p>É a mesma forma nas três situações — acabou de ser enviado, ainda está
 * sendo lido, terminou — porque quem consome é uma tela só, que muda o que
 * mostra conforme o {@code status}. Ter um DTO por fase obrigaria o app a
 * adivinhar qual esperar de cada endpoint.
 *
 * <p>Todo o bloco de resultado vem nulo enquanto o status é {@code PROCESSANDO}.
 *
 * <p>Não traz a lista de transações importadas: quando o resultado fica pronto o
 * app recarrega as próprias telas (extrato, análise, gráficos), e devolver as
 * transações aqui seria uma segunda cópia dos mesmos dados, capaz de discordar
 * da primeira.
 *
 * @param confirmada  o usuário já viu este resultado; enquanto for {@code false}
 *                    e o status for {@code CONCLUIDA}, o app mostra o aviso de
 *                    "extrato pronto"
 * @param marcoAnterior até que data este banco já estava importado quando o PDF
 *                      chegou; {@code null} no primeiro extrato do banco
 * @param lancamentosPulados quantos o Gemini leu mas já estavam cobertos pelo marco
 * @param semCategoria quantos ficaram sem categoria porque a que o modelo
 *                     escolheu não combinava com o tipo do lançamento
 */
public record ExtratoImportacaoResponse(
    Long id,
    Long idBanco,
    String nomeBanco,
    StatusImportacaoExtrato status,
    String nomeArquivo,
    String erro,
    boolean confirmada,
    LocalDateTime criadoEm,
    LocalDateTime concluidoEm,
    LocalDate marcoAnterior,
    LocalDate periodoInicio,
    LocalDate periodoFim,
    Integer lancamentosLidos,
    Integer lancamentosImportados,
    Integer lancamentosPulados,
    Integer semCategoria,
    BigDecimal totalEntradas,
    BigDecimal totalSaidas
) {}
