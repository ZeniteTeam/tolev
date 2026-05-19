package com.br.startup.tolevBack.finance.internal.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_vendedores")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vendedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeEmpresa;
    private String cpfCnpj;
}
