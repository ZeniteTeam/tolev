package com.br.startup.tolevBack.users.internal.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "tb_assinaturas")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Assinatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String modeloAssinatura;

    @OneToMany(mappedBy = "assinatura", fetch = FetchType.LAZY)
    private List<UsuarioAssinatura> usuarioAssinaturas;

}
