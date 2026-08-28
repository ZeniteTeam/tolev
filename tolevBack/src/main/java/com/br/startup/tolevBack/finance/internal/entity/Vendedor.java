package com.br.startup.tolevBack.finance.internal.entity;

import com.br.startup.tolevBack.finance.internal.enums.CategoriaVendedor;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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

    /**
     * Procedência: quem digitou esse estabelecimento primeiro. NULL = semeado
     * pelo sistema. Não é posse — o vendedor é compartilhado por toda a base.
     */
    private Long criadoPorUsuario;

    private String nomeEmpresa;

    /** nomeEmpresa sem acento/caixa. Chave de deduplicação global. */
    private String nomeNormalizado;

    private String cpfCnpj;

    @Enumerated(EnumType.STRING)
    private CategoriaVendedor categoriaVendedor;

    @OneToMany(mappedBy = "vendedor", fetch = FetchType.LAZY)
    private List<CategoriaCompra> categoriasCompra;

    @OneToMany(mappedBy = "vendedor", fetch = FetchType.LAZY)
    private List<Transacao> transacoes;

    @OneToMany(mappedBy = "vendedor", fetch = FetchType.LAZY)
    private List<TransacaoRecorrente> transacoesRecorrentes;
}
