package com.br.startup.tolevBack.finance.internal.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_categoria_compra")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vendedor")
    private Vendedor vendedor;

    private String nomeCategoria;
}
