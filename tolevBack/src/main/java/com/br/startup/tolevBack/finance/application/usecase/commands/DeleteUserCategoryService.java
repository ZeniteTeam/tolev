package com.br.startup.tolevBack.finance.application.usecase.commands;

import com.br.startup.tolevBack.finance.internal.entity.CategoriaGastoUsuario;
import com.br.startup.tolevBack.finance.internal.repository.ICategoriaGastoUsuarioRepository;
import com.br.startup.tolevBack.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Remove uma categoria do usuário do catálogo.
 *
 * <p>É baixa lógica, não DELETE: as transações classificadas nela continuam
 * apontando para a linha. Apagar de verdade ou esvaziaria a categoria de meses
 * de histórico já analisado, ou esbarraria na FK — a categoria some da grade de
 * escolha e o passado fica de pé.
 */
@Service
@RequiredArgsConstructor
public class DeleteUserCategoryService {

    private final ICategoriaGastoUsuarioRepository categoriaUsuarioRepository;

    @Transactional
    public void execute(Long id, Long idUsuario) {
        CategoriaGastoUsuario categoria = categoriaUsuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoria não encontrada com id: " + id));

        if (idUsuario != null && !idUsuario.equals(categoria.getIdUsuario())) {
            throw new IllegalArgumentException("Essa categoria não pertence ao usuário informado.");
        }

        categoria.setAtivo(false);
        categoriaUsuarioRepository.save(categoria);
    }
}
