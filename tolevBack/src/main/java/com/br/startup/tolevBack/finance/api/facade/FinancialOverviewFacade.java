package com.br.startup.tolevBack.finance.api.facade;

import com.br.startup.tolevBack.finance.application.dto.response.FinancialOverviewResponse;
import com.br.startup.tolevBack.finance.application.usecase.queries.GetFinancialOverviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FinancialOverviewFacade {

    private final GetFinancialOverviewService getFinancialOverview;

    public FinancialOverviewResponse getOverview(Long idUsuario) {
        return getFinancialOverview.execute(idUsuario);
    }
}
