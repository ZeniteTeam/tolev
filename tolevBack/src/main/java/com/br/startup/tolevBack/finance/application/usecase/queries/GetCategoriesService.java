package com.br.startup.tolevBack.finance.application.usecase.queries;

import com.br.startup.tolevBack.finance.application.dto.response.CategoryResponse;
import com.br.startup.tolevBack.finance.internal.mapper.CategoryMapper;
import com.br.startup.tolevBack.finance.internal.repository.ICategoriaGastoSistemaRepository;
import com.br.startup.tolevBack.finance.internal.repository.ICategoriaGastoUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Catálogo de categorias que o usuário vê ao classificar uma transação: as do
 * sistema primeiro (as que todo mundo tem), depois as que ele mesmo criou.
 */
@Service
@RequiredArgsConstructor
public class GetCategoriesService {

    private final ICategoriaGastoSistemaRepository categoriaSistemaRepository;
    private final ICategoriaGastoUsuarioRepository categoriaUsuarioRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> execute(Long idUsuario) {
        List<CategoryResponse> categorias = new ArrayList<>(
                categoriaSistemaRepository.findByAtivoTrueOrderByNomeAsc()
                        .stream()
                        .map(CategoryMapper::toResponse)
                        .toList());

        categoriaUsuarioRepository.findByIdUsuarioAndAtivoTrueOrderByNomeAsc(idUsuario)
                .stream()
                .map(CategoryMapper::toResponse)
                .forEach(categorias::add);

        return categorias;
    }
}
