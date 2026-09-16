package com.br.startup.tolevBack.finance.application.usecase.commands;

import com.br.startup.tolevBack.finance.application.dto.request.CategoryRequest;
import com.br.startup.tolevBack.finance.application.dto.response.CategoryResponse;
import com.br.startup.tolevBack.finance.application.service.CategoryValidator;
import com.br.startup.tolevBack.finance.internal.entity.CategoriaGastoUsuario;
import com.br.startup.tolevBack.finance.internal.enums.TipoCategoriaGasto;
import com.br.startup.tolevBack.finance.internal.mapper.CategoryMapper;
import com.br.startup.tolevBack.finance.internal.repository.ICategoriaGastoSistemaRepository;
import com.br.startup.tolevBack.finance.internal.repository.ICategoriaGastoUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Cria uma categoria própria do usuário, ao lado do catálogo do sistema. */
@Service
@RequiredArgsConstructor
public class CreateUserCategoryService {

    private final ICategoriaGastoUsuarioRepository categoriaUsuarioRepository;
    private final ICategoriaGastoSistemaRepository categoriaSistemaRepository;

    @Transactional
    public CategoryResponse execute(CategoryRequest request) {
        Long idUsuario = request.idUsuario();
        if (idUsuario == null) {
            throw new IllegalArgumentException("Informe o usuário da categoria.");
        }

        String nome = CategoryValidator.nomeValido(request.nome());
        // Sem tipo a categoria nasceria invisível: o formulário de transação só
        // mostra as do lado que está sendo lançado.
        TipoCategoriaGasto tipo = request.tipo() != null ? request.tipo() : TipoCategoriaGasto.DESPESA;

        CategoryValidator.nomeLivre(
                nome, tipo, idUsuario, null, categoriaSistemaRepository, categoriaUsuarioRepository);

        CategoriaGastoUsuario categoria = categoriaUsuarioRepository.save(
                CategoriaGastoUsuario.builder()
                        .idUsuario(idUsuario)
                        .nome(nome)
                        .cor(CategoryValidator.corValida(request.cor()))
                        .tipo(tipo)
                        .ativo(true)
                        .build());

        return CategoryMapper.toResponse(categoria);
    }
}
