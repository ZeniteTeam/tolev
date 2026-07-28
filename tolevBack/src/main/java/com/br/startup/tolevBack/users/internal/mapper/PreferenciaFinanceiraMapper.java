package com.br.startup.tolevBack.users.internal.mapper;

import com.br.startup.tolevBack.users.application.dto.response.PreferenciaFinanceiraResponse;
import com.br.startup.tolevBack.users.internal.entity.PreferenciaFinanceira;
import com.br.startup.tolevBack.users.internal.enums.MetodoOrcamento;
import com.br.startup.tolevBack.users.internal.enums.MetodoQuitacao;

import java.math.BigDecimal;

public class PreferenciaFinanceiraMapper {

    public static PreferenciaFinanceiraResponse toResponse(PreferenciaFinanceira p) {
        return new PreferenciaFinanceiraResponse(
                p.getIdUsuario(),
                p.getMetodoQuitacao(),
                p.getAporteExtraMensal(),
                p.getMetodoOrcamento(),
                p.getRendaMensal(),
                p.getPercFixos(),
                p.getPercDividas(),
                p.getPercLazer(),
                p.getReservaEmergenciaMeta()
        );
    }

    /** Preferências padrão de um usuário que ainda não personalizou nada. */
    public static PreferenciaFinanceira defaults(Long idUsuario) {
        return PreferenciaFinanceira.builder()
                .idUsuario(idUsuario)
                .metodoQuitacao(MetodoQuitacao.AVALANCHE)
                .aporteExtraMensal(BigDecimal.ZERO)
                .metodoOrcamento(MetodoOrcamento.REGRA_50_30_20)
                .rendaMensal(BigDecimal.ZERO)
                .percFixos(50)
                .percDividas(30)
                .percLazer(20)
                .reservaEmergenciaMeta(BigDecimal.ZERO)
                .build();
    }
}
