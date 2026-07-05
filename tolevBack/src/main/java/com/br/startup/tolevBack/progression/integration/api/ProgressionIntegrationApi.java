package com.br.startup.tolevBack.progression.integration.api;

import com.br.startup.tolevBack.progression.application.dto.response.*;

import java.util.List;

public interface ProgressionIntegrationApi {
    List<DebtProjectionResponse> getDebtProjection(Long idUsuario);
    DebtProjectionResponse getDebtById(Long idDivida);
    DebtPayoffEstimateResponse getDebtPayoffEstimate(Long idUsuario);
    List<DividaResponse> getDividasByUser(Long idUsuario);
    DividaResponse getDividaById(Long idDivida);
    List<ProgressaoModuloResponse> getProgressaoModulos(Long idUsuario);
}
