package com.br.startup.tolevBack.progression.internal.entity;

import com.br.startup.tolevBack.progression.internal.enums.EstiloModulo;
import com.br.startup.tolevBack.progression.internal.enums.TipoModulo;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

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

    private String nome;
    private String descricao;

    private BigDecimal requisitos;

    @Column(name = "pos_x")
    private BigDecimal posX;

    @Column(name = "pos_y")
    private BigDecimal posY;

    @Enumerated(EnumType.STRING)
    private TipoModulo tipo;

    @Enumerated(EnumType.STRING)
    private EstiloModulo estilo;

    @OneToMany(mappedBy = "mapaModulo", fetch = FetchType.LAZY)
    private List<MapaModuloDetalhe> detalhes;

    @OneToMany(mappedBy = "mapaModulo", fetch = FetchType.LAZY)
    private List<ModuloProgressaoUsuario> progressosUsuarios;
}
