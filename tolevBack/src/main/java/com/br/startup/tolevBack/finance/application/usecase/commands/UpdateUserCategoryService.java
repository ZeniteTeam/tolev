package com.br.startup.tolevBack.finance.application.usecase.commands;

import com.br.startup.tolevBack.finance.application.dto.request.CategoryRequest;
import com.br.startup.tolevBack.finance.application.dto.response.CategoryResponse;
import com.br.startup.tolevBack.finance.application.service.CategoryValidator;
import com.br.startup.tolevBack.finance.internal.entity.CategoriaGastoUsuario;
import com.br.startup.tolevBack.finance.internal.mapper.CategoryMapper;
import com.br.startup.tolevBack.finance.internal.repository.ICategoriaGastoSistemaRepository;
import com.br.startup.tolevBack.finance.internal.repository.ICategoriaGastoUsuarioRepository;
import com.br.startup.tolevBack.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Renomeia ou recolore uma categoria do usuário.
 *
 * <p>O tipo não muda de propósito: as transações já classificadas nela viraram
 * despesa ou receita por causa dele, e trocá-lo faria a análise inteira mudar
 * de lado sem que ninguém tivesse mexido em transação nenhuma.
 */
@Service
@RequiredArgsConstructor
public class UpdateUserCategoryService {

    private final ICategoriaGastoUsuarioRepository categoriaUsuarioRepository;
    private final ICategoriaGastoSistemaRepository categoriaSistemaRepository;

    @Transactional
    public CategoryResponse execute(Long id, CategoryRequest request) {
        CategoriaGastoUsuario categoria = categoriaUsuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoria não encontrada com id: " + id));

        if (!Boolean.TRUE.equals(categoria.getAtivo())) {
            throw new NotFoundException("Categoria não encontrada com id: " + id);
        }
        if (request.idUsuario() != null && !request.idUsuario().equals(categoria.getIdUsuario())) {
            throw new IllegalArgumentException("Essa categoria não pertence ao usuário informado.");
        }

        if (request.nome() != null) {
            String nome = CategoryValidator.nomeValido(request.nome());
            CategoryValidator.nomeLivre(
                    nome, categoria.getTipo(), categoria.getIdUsuario(), id,
                    categoriaSistemaRepository, categoriaUsuarioRepository);
            categoria.setNome(nome);
        }
        if (request.cor() != null) {
            categoria.setCor(CategoryValidator.corValida(request.cor()));
        }

        return CategoryMapper.toResponse(categoriaUsuarioRepository.save(categoria));
    }
}
