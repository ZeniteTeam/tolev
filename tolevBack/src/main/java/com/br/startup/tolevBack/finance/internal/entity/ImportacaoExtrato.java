package com.br.startup.tolevBack.finance.internal.entity;

import com.br.startup.tolevBack.finance.internal.enums.StatusImportacaoExtrato;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Um upload de extrato, do envio do PDF até o usuário ver o resultado.
 *
 * <p>É o registro que sobrevive à requisição: o POST devolve na hora, a leitura
 * pelo Gemini roda em outra thread e escreve o desfecho aqui, e o app pergunta o
 * estado quando quiser — inclusive depois de ter sido fechado no meio.
 *
 * <p>Guarda também as duas coisas que precisam durar além do upload: até que
 * data já foi importado naquele banco (o marco que impede reimportar o mesmo
 * período) e quais transações saíram deste upload (para poder desfazê-lo
 * inteiro).
 *
 * <p>O marco é por <em>banco</em>: o extrato do Itaú não diz nada sobre o que já
 * veio do Nubank, e um marco por usuário recusaria o segundo banco sem motivo.
 * Não há vínculo com {@link ContaBancaria} de propósito — a importação nunca
 * mexe em saldo, e hoje nenhum usuário tem conta conectada.
 *
 * <p>Todo o bloco de resultado é nulo enquanto o status é {@code PROCESSANDO}:
 * ainda não existe período, contagem nem total para preencher.
 */
@Entity
@Table(name = "tb_importacoes_extrato")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportacaoExtrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idUsuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_banco")
    private Banco banco;

    @Enumerated(EnumType.STRING)
    private StatusImportacaoExtrato status;

    /** Nome do PDF enviado — é como o usuário distingue dois uploads na lista. */
    private String nomeArquivo;

    /** Motivo da falha, em português, pronto para a tela. Nulo quando deu certo. */
    private String erro;

    /**
     * Período do que foi de fato gravado — não o período do PDF. Se metade do
     * extrato caiu no filtro do marco anterior, o início é o primeiro lançamento
     * que sobrou, senão o marco andaria para trás no upload seguinte.
     */
    private LocalDate dataInicio;
    private LocalDate dataFim;

    /** Até onde este banco já estava importado quando o PDF chegou. */
    private LocalDate marcoAnterior;

    /** Quantos lançamentos o Gemini leu no PDF, antes do filtro do marco. */
    private Integer quantidadeLida;

    private Integer quantidadeImportada;

    /** Lidos pelo Gemini mas já cobertos por uma importação anterior. */
    private Integer quantidadePulada;

    /** Importados sem categoria porque a escolhida não combinava com o tipo. */
    private Integer quantidadeSemCategoria;

    private BigDecimal totalEntradas;
    private BigDecimal totalSaidas;

    private LocalDateTime criadoEm;

    private LocalDateTime concluidoEm;

    /**
     * Quando o usuário clicou em "Atualizar" e viu o resultado. Enquanto for
     * nulo o app segue avisando que há um extrato pronto — é o que garante que
     * fechar o app no meio do processamento não faça o aviso se perder.
     */
    private LocalDateTime confirmadoEm;
}
