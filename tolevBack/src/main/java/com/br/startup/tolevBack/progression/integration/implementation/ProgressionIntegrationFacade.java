package com.br.startup.tolevBack.progression.integration.implementation;

import com.br.startup.tolevBack.progression.application.dto.response.*;
import com.br.startup.tolevBack.progression.application.usecase.queries.Debts.GetDebtPayoffEstimateService;
import com.br.startup.tolevBack.progression.application.usecase.queries.Debts.GetDebtProjectionService;
import com.br.startup.tolevBack.progression.application.usecase.queries.Debts.GetDividaByIdService;
import com.br.startup.tolevBack.progression.application.usecase.queries.Goals.GetMetaByIdService;
import com.br.startup.tolevBack.progression.application.usecase.queries.Goals.GetMetasByUserService;
import com.br.startup.tolevBack.progression.application.usecase.queries.Progression.GetProgressaoModulosService;
import com.br.startup.tolevBack.progression.integration.api.ProgressionIntegrationApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProgressionIntegrationFacade implements ProgressionIntegrationApi {

    private final GetDebtProjectionService getDebtProjection;
    private final GetDividaByIdService getDividaById;
    private final GetDebtPayoffEstimateService getDebtPayoffEstimate;
    private final GetMetasByUserService getMetasByUser;
    private final GetMetaByIdService getMetaById;
    private final GetProgressaoModulosService getProgressaoModulos;

    @Override
    public List<DebtProjectionResponse> getDebtProjection(Long idUsuario) {
        return getDebtProjection.execute(idUsuario);
    }

    @Override
    public DebtProjectionResponse getDebtById(Long idDivida) {
        return getDividaById.execute(idDivida);
    }

    @Override
    public DebtPayoffEstimateResponse getDebtPayoffEstimate(Long idUsuario) {
        return getDebtPayoffEstimate.execute(idUsuario);
    }

    @Override
    public List<MetaResponse> getMetasByUser(Long idUsuario) {
        return getMetasByUser.execute(idUsuario);
    }

    @Override
    public MetaResponse getMetaById(Long idMeta) {
        return getMetaById.execute(idMeta);
    }

    @Override
    public List<ProgressaoModuloResponse> getProgressaoModulos(Long idUsuario) {
        return getProgressaoModulos.execute(idUsuario);
    }
}
