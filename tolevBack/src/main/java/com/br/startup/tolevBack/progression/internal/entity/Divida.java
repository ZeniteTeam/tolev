package com.br.startup.tolevBack.progression.internal.entity;

import com.br.startup.tolevBack.progression.internal.enums.NivelComprometimento;
import com.br.startup.tolevBack.progression.internal.enums.RegimeJuros;
import com.br.startup.tolevBack.progression.internal.enums.SistemaAmortizacao;
import com.br.startup.tolevBack.progression.internal.enums.StatusDivida;
import com.br.startup.tolevBack.progression.internal.enums.TipoDivida;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "tb_dividas")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Divida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idUsuario;
    private String nomeDivida;
    private String credor;
    private String banco;
    private BigDecimal valorDivida;
    /** Juros mensal contratado, em % a.m. */
    private BigDecimal taxaJuros;
    /** Multa aplicada uma única vez sobre a parcela em atraso, em %. */
    private BigDecimal multaAtraso;
    /** Juros de mora, em % a.m., cobrados proporcionalmente aos dias de atraso. */
    private BigDecimal jurosMora;
    private BigDecimal parcelaMinima;
    private Integer pesoEmocional;
    private Integer quantidadeParcelas;
    /** Data em que o valor foi liberado / a compra foi feita. */
    private LocalDate dataLiberacao;
    /** Vencimento da primeira parcela; as demais caem no mesmo dia dos meses seguintes. */
    private LocalDate dataPrimeiroVencimento;
    private LocalDate dataVencimentoFinal;

    @Enumerated(EnumType.STRING)
    private TipoDivida tipo;

    @Enumerated(EnumType.STRING)
    private SistemaAmortizacao sistemaAmortizacao;

    @Enumerated(EnumType.STRING)
    private RegimeJuros regimeJuros;

    @Enumerated(EnumType.STRING)
    private StatusDivida status;

    @Enumerated(EnumType.STRING)
    private NivelComprometimento nivelComprometimento;

    @OneToMany(
            mappedBy = "divida",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ParcelaDivida> parcelas;

    @OneToOne(mappedBy = "divida", fetch = FetchType.LAZY)
    private ProgressoDivida progressoDivida;
}
