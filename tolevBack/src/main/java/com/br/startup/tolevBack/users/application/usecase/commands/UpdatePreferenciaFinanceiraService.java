package com.br.startup.tolevBack.users.application.usecase.commands;

import com.br.startup.tolevBack.shared.events.DadosFinanceirosAlteradosEvent;
import com.br.startup.tolevBack.shared.events.OrigemAlteracao;
import com.br.startup.tolevBack.shared.exceptions.NotFoundException;
import com.br.startup.tolevBack.users.application.dto.request.PreferenciaFinanceiraRequest;
import com.br.startup.tolevBack.users.application.dto.response.PreferenciaFinanceiraResponse;
import com.br.startup.tolevBack.users.internal.entity.PreferenciaFinanceira;
import com.br.startup.tolevBack.users.internal.mapper.PreferenciaFinanceiraMapper;
import com.br.startup.tolevBack.users.internal.repository.IPreferenciaFinanceiraRepository;
import com.br.startup.tolevBack.users.internal.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Cria (na primeira vez) ou atualiza as preferências financeiras do usuário.
 * A atualização é parcial: campos nulos no request preservam o valor atual.
 */
@Service
@RequiredArgsConstructor
public class UpdatePreferenciaFinanceiraService {

    private final IPreferenciaFinanceiraRepository preferenciaRepository;
    private final IUserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public PreferenciaFinanceiraResponse execute(Long idUsuario, PreferenciaFinanceiraRequest request) {
        if (!userRepository.existsById(idUsuario)) {
            throw new NotFoundException("Usuário não encontrado com id: " + idUsuario);
        }

        PreferenciaFinanceira pref = preferenciaRepository.findByIdUsuario(idUsuario)
                .orElseGet(() -> PreferenciaFinanceiraMapper.defaults(idUsuario));

        if (request.metodoQuitacao() != null) pref.setMetodoQuitacao(request.metodoQuitacao());
        if (request.aporteExtraMensal() != null) pref.setAporteExtraMensal(request.aporteExtraMensal());
        if (request.metodoOrcamento() != null) pref.setMetodoOrcamento(request.metodoOrcamento());
        if (request.rendaMensal() != null) pref.setRendaMensal(request.rendaMensal());
        if (request.percFixos() != null) pref.setPercFixos(request.percFixos());
        if (request.percDividas() != null) pref.setPercDividas(request.percDividas());
        if (request.percLazer() != null) pref.setPercLazer(request.percLazer());
        if (request.reservaEmergenciaMeta() != null) pref.setReservaEmergenciaMeta(request.reservaEmergenciaMeta());

        validarDivisaoOrcamento(pref);

        PreferenciaFinanceiraResponse resposta =
                PreferenciaFinanceiraMapper.toResponse(preferenciaRepository.save(pref));

        // Renda e divisão do orçamento são denominador de quase todo indicador:
        // mudou aqui, toda análise anterior ficou obsoleta.
        eventPublisher.publishEvent(DadosFinanceirosAlteradosEvent.de(
                idUsuario, OrigemAlteracao.PREFERENCIAS_ATUALIZADAS, "USUARIO", idUsuario));

        return resposta;
    }

    /** A soma da divisão do orçamento (fixos + dívidas + lazer) deve totalizar 100%. */
    private void validarDivisaoOrcamento(PreferenciaFinanceira pref) {
        int soma = pref.getPercFixos() + pref.getPercDividas() + pref.getPercLazer();
        if (soma != 100) {
            throw new IllegalArgumentException(
                    "A divisão do orçamento (fixos + dívidas + lazer) deve somar 100%. Soma atual: " + soma + "%");
        }
    }
}
