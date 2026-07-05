package com.br.startup.tolevBack.progression.integration.implementation;

import com.br.startup.tolevBack.progression.application.dto.response.*;
import com.br.startup.tolevBack.progression.application.usecase.queries.Debts.GetDebtPayoffEstimateService;
import com.br.startup.tolevBack.progression.application.usecase.queries.Debts.GetDebtProjectionService;
import com.br.startup.tolevBack.progression.application.usecase.queries.Debts.GetDividaByIdService;
import com.br.startup.tolevBack.progression.application.usecase.queries.Debts.GetDividaResponseByIdService;
import com.br.startup.tolevBack.progression.application.usecase.queries.Debts.GetDividasByUserService;
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
    private final GetDividasByUserService getDividasByUser;
    private final GetDividaResponseByIdService getDividaResponseById;
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
    public List<DividaResponse> getDividasByUser(Long idUsuario) {
        return getDividasByUser.execute(idUsuario);
    }

    @Override
    public DividaResponse getDividaById(Long idDivida) {
        return getDividaResponseById.execute(idDivida);
    }

    @Override
    public List<ProgressaoModuloResponse> getProgressaoModulos(Long idUsuario) {
        return getProgressaoModulos.execute(idUsuario);
    }
}
