package com.br.startup.tolevBack.users.internal.entity;

import com.br.startup.tolevBack.users.internal.enums.MetodoOrcamento;
import com.br.startup.tolevBack.users.internal.enums.MetodoQuitacao;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Preferências financeiras do usuário: a estratégia de quitação de dívidas e o
 * método de orçamento escolhidos, além dos campos de apoio (renda, aporte extra,
 * divisão do orçamento) usados por projeções, análises e recomendações.
 *
 * Relação 1:1 com {@link Usuario} através de {@code id_usuario}.
 */
@Entity
@Table(name = "tb_preferencias_financeiras")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreferenciaFinanceira {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_usuario", nullable = false, unique = true)
    private Long idUsuario;

    // ----- Quitação de dívidas -----

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_quitacao", nullable = false)
    @Builder.Default
    private MetodoQuitacao metodoQuitacao = MetodoQuitacao.AVALANCHE;

    /** Valor extra que o usuário pretende aportar mensalmente na quitação. */
    @Column(name = "aporte_extra_mensal")
    @Builder.Default
    private BigDecimal aporteExtraMensal = BigDecimal.ZERO;

    // ----- Orçamento -----

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_orcamento", nullable = false)
    @Builder.Default
    private MetodoOrcamento metodoOrcamento = MetodoOrcamento.REGRA_50_30_20;

    /** Renda mensal usada como base para a divisão do orçamento. */
    @Column(name = "renda_mensal")
    @Builder.Default
    private BigDecimal rendaMensal = BigDecimal.ZERO;

    /** Percentual do orçamento destinado a gastos fixos/essenciais. */
    @Column(name = "perc_fixos", nullable = false)
    @Builder.Default
    private Integer percFixos = 50;

    /** Percentual do orçamento destinado à quitação de dívidas. */
    @Column(name = "perc_dividas", nullable = false)
    @Builder.Default
    private Integer percDividas = 30;

    /** Percentual do orçamento destinado a lazer/estilo de vida. */
    @Column(name = "perc_lazer", nullable = false)
    @Builder.Default
    private Integer percLazer = 20;

    /** Meta de reserva de emergência (opcional). */
    @Column(name = "reserva_emergencia_meta")
    @Builder.Default
    private BigDecimal reservaEmergenciaMeta = BigDecimal.ZERO;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @PrePersist
    void onCreate() {
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = this.criadoEm;
    }

    @PreUpdate
    void onUpdate() {
        this.atualizadoEm = LocalDateTime.now();
    }
}
