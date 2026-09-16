package com.br.startup.tolevBack.finance.application.usecase.commands;

import com.br.startup.tolevBack.finance.application.dto.response.ExtratoImportacaoResponse;
import com.br.startup.tolevBack.finance.internal.entity.ImportacaoExtrato;
import com.br.startup.tolevBack.finance.internal.mapper.ImportacaoExtratoMapper;
import com.br.startup.tolevBack.finance.internal.repository.IImportacaoExtratoRepository;
import com.br.startup.tolevBack.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Marca que o usuário viu o resultado da importação.
 *
 * <p>É o "ok, atualizar" da tela: sem ele, o aviso de extrato pronto teria de
 * viver na memória do app e sumiria no primeiro fechamento — justamente para
 * quem saiu do app durante o processamento, que é o caso que a espera em segundo
 * plano existe para atender.
 */
@Service
@RequiredArgsConstructor
public class ConfirmExtratoImportService {

    private final IImportacaoExtratoRepository importacaoRepository;

    @Transactional
    public ExtratoImportacaoResponse execute(Long idUsuario, Long idImportacao) {
        if (idUsuario == null) {
            throw new IllegalArgumentException("Informe o usuário da importação.");
        }
        ImportacaoExtrato importacao = importacaoRepository.findById(idImportacao)
                .orElseThrow(() -> new NotFoundException(
                        "Importação não encontrada com id: " + idImportacao));

        if (!idUsuario.equals(importacao.getIdUsuario())) {
            throw new IllegalArgumentException("Essa importação não pertence ao usuário informado.");
        }

        // Confirmar duas vezes não é erro — dois toques no botão, ou o app
        // reenviando depois de perder a resposta. Mantém o primeiro horário.
        if (importacao.getConfirmadoEm() == null) {
            importacao.setConfirmadoEm(LocalDateTime.now());
        }
        return ImportacaoExtratoMapper.toResponse(importacao);
    }
}
