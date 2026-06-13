package com.br.startup.tolevBack.progression.internal.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "tb_mapa_progressao")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MapaProgressao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String urlModelo;
    private String nomeMapa;
    private String descricao;
    private Boolean ativo;

    @OneToMany(mappedBy = "mapaProgressao", fetch = FetchType.LAZY)
    private List<MapaModulo> modulos;
}
