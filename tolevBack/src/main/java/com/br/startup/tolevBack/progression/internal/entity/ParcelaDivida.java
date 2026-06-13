package com.br.startup.tolevBack.progression.internal.entity;
import com.br.startup.tolevBack.progression.internal.enums.StatusDivida;
import com.br.startup.tolevBack.progression.internal.enums.StatusParcela;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "tb_parcela_dividas")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParcelaDivida {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal valorPrincipal;
    private BigDecimal valorTotal;
    private BigDecimal valorJuros;

    private Integer numeroParcela;

    @Enumerated(EnumType.STRING)
    private StatusParcela status;

    private LocalDate dataPagamento;
    private LocalDate dataVencimento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_divida")
    private Divida divida;

    @OneToMany(
            mappedBy = "parcelaDivida",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<PagamentoParcela> pagamentos;
}
