package com.br.startup.tolevBack.finance.application.usecase.queries;

import com.br.startup.tolevBack.finance.application.dto.response.ExtratoImportacaoResponse;
import com.br.startup.tolevBack.finance.internal.entity.ImportacaoExtrato;
import com.br.startup.tolevBack.finance.internal.mapper.ImportacaoExtratoMapper;
import com.br.startup.tolevBack.finance.internal.repository.IImportacaoExtratoRepository;
import com.br.startup.tolevBack.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * As consultas da tela de importação de extrato.
 *
 * <p>O app pergunta o estado em vez de ser avisado: não há push nem websocket
 * hoje, e um extrato demora dezenas de segundos, não horas — perguntar de tempos
 * em tempos enquanto houver um em andamento resolve, e continua funcionando
 * depois de o app ter sido fechado e reaberto, que é o cenário que motivou tudo.
 */
@Service
@RequiredArgsConstructor
public class GetExtratoImportsService {

    private final IImportacaoExtratoRepository importacaoRepository;

    /** Histórico do usuário, mais recentes primeiro. */
    @Transactional(readOnly = true)
    public List<ExtratoImportacaoResponse> execute(Long idUsuario) {
        if (idUsuario == null) {
            throw new IllegalArgumentException("Informe o usuário das importações.");
        }
        return importacaoRepository.findByIdUsuarioOrderByCriadoEmDesc(idUsuario).stream()
                .map(ImportacaoExtratoMapper::toResponse)
                .toList();
    }

    /** Uma importação específica — o que a tela de acompanhamento fica relendo. */
    @Transactional(readOnly = true)
    public ExtratoImportacaoResponse byId(Long idUsuario, Long idImportacao) {
        if (idUsuario == null) {
            throw new IllegalArgumentException("Informe o usuário da importação.");
        }
        ImportacaoExtrato importacao = importacaoRepository.findById(idImportacao)
                .orElseThrow(() -> new NotFoundException(
                        "Importação não encontrada com id: " + idImportacao));

        if (!idUsuario.equals(importacao.getIdUsuario())) {
            throw new IllegalArgumentException("Essa importação não pertence ao usuário informado.");
        }
        return ImportacaoExtratoMapper.toResponse(importacao);
    }
}
