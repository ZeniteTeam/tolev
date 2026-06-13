package com.br.startup.tolevBack.progression.internal.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tb_mapa_modulos_detalhes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MapaModuloDetalhe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mapa_modulo")
    private MapaModulo mapaModulo;

    private String titulo;
    private String conteudo;

    private BigDecimal requisitos;

    @Column(name = "pos_x")
    private BigDecimal posX;

    @Column(name = "pos_y")
    private BigDecimal posY;
}
