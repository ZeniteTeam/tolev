package com.br.startup.tolevBack.support.internal.entities;

import com.br.startup.tolevBack.support.enums.CategoriaTicket;
import com.br.startup.tolevBack.support.enums.StatusTicket;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "tb_tickets")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idUsuario;

    private String tituloTicket;
    private String descricaoTicket;

    @Enumerated(EnumType.STRING)
    private CategoriaTicket categoria;

    @Enumerated(EnumType.STRING)
    private StatusTicket status;

    private LocalDate dataAbertura;
    private LocalDate dataAtualizacao;
    private LocalDate dataFechamento;
}
