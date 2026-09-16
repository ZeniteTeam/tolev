package com.br.startup.tolevBack.finance.application.service;

import com.br.startup.tolevBack.finance.internal.entity.CategoriaGastoUsuario;
import com.br.startup.tolevBack.finance.internal.enums.TipoCategoriaGasto;
import com.br.startup.tolevBack.finance.internal.repository.ICategoriaGastoSistemaRepository;
import com.br.startup.tolevBack.finance.internal.repository.ICategoriaGastoUsuarioRepository;

import java.util.Objects;

/** Regras comuns à criação e à edição de categorias do usuário. */
public final class CategoryValidator {

    private static final int NOME_MAX = 40;
    /** Hex de 3 ou 6 dígitos — o mesmo formato que o catálogo do sistema usa. */
    private static final String HEX = "^#([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$";

    private CategoryValidator() {}

    public static String nomeValido(String nome) {
        String limpo = nome != null ? nome.trim() : "";
        if (limpo.isEmpty()) {
            throw new IllegalArgumentException("A categoria precisa de um nome.");
        }
        if (limpo.length() > NOME_MAX) {
            throw new IllegalArgumentException(
                    "O nome da categoria pode ter no máximo " + NOME_MAX + " caracteres.");
        }
        return limpo;
    }

    /** Cor inválida não derruba o cadastro: a grade tem cor padrão para isso. */
    public static String corValida(String cor) {
        if (cor == null || cor.isBlank()) {
            return null;
        }
        String limpa = cor.trim();
        return limpa.matches(HEX) ? limpa : null;
    }

    /**
     * Duas categorias com o mesmo nome deixariam a grade de escolha ambígua e
     * os gráficos por categoria com duas fatias iguais lado a lado.
     *
     * @param idIgnorado a própria categoria, numa edição que não mexeu no nome
     */
    public static void nomeLivre(
            String nome,
            TipoCategoriaGasto tipo,
            Long idUsuario,
            Long idIgnorado,
            ICategoriaGastoSistemaRepository sistemaRepository,
            ICategoriaGastoUsuarioRepository usuarioRepository) {

        boolean noSistema = sistemaRepository.findByAtivoTrueOrderByNomeAsc().stream()
                .filter(c -> c.getTipo() == tipo)
                .anyMatch(c -> nome.equalsIgnoreCase(c.getNome()));
        if (noSistema) {
            throw new IllegalArgumentException("Já existe uma categoria \"" + nome + "\" no app.");
        }

        boolean jaCriada = usuarioRepository.findByIdUsuarioAndAtivoTrueOrderByNomeAsc(idUsuario).stream()
                .filter(c -> c.getTipo() == tipo)
                .filter(c -> !Objects.equals(c.getId(), idIgnorado))
                .map(CategoriaGastoUsuario::getNome)
                .anyMatch(nome::equalsIgnoreCase);
        if (jaCriada) {
            throw new IllegalArgumentException("Você já tem uma categoria chamada \"" + nome + "\".");
        }
    }
}
