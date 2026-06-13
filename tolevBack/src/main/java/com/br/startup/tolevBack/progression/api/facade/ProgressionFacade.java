package com.br.startup.tolevBack.progression.api.facade;

import com.br.startup.tolevBack.progression.application.dto.response.ProgressionGraphsResponse;
import com.br.startup.tolevBack.progression.application.dto.response.ProgressionOverviewResponse;
import com.br.startup.tolevBack.progression.application.dto.response.ProgressionStatsResponse;
import com.br.startup.tolevBack.progression.application.usecase.queries.GetProgressionGraphsService;
import com.br.startup.tolevBack.progression.application.usecase.queries.GetProgressionOverviewService;
import com.br.startup.tolevBack.progression.application.usecase.queries.GetProgressionStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProgressionFacade {

    private final GetProgressionOverviewService getOverview;
    private final GetProgressionStatsService getStats;
    private final GetProgressionGraphsService getGraphs;

    public ProgressionOverviewResponse getOverview(Long idUsuario) {
        return getOverview.execute(idUsuario);
    }

    public ProgressionStatsResponse getStats(Long idUsuario) {
        return getStats.execute(idUsuario);
    }

    public ProgressionGraphsResponse getGraphs(Long idUsuario) {
        return getGraphs.execute(idUsuario);
    }
}
