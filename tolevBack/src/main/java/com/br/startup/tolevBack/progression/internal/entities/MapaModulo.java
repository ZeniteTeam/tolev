package com.br.startup.tolevBack.progression.internal.entities;

import com.br.startup.tolevBack.progression.enums.EstiloModulo;
import com.br.startup.tolevBack.progression.enums.TipoModulo;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tb_mapa_modulos")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MapaModulo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mapa_progressao")
    private MapaProgressao mapaProgressao;

    private BigDecimal requisitos;
    @Column(name = "pos_x")
    private BigDecimal posX;

    @Column(name = "pos_y")
    private BigDecimal posY;
    @Enumerated(EnumType.STRING)
    private TipoModulo tipo;

    @Enumerated(EnumType.STRING)
    private EstiloModulo estilo;
}
