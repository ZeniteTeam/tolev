package com.br.startup.tolevBack.finance.api.controller;

import com.br.startup.tolevBack.finance.api.facade.FinancialOverviewFacade;
import com.br.startup.tolevBack.finance.application.dto.response.FinancialOverviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/financial-overview")
@RequiredArgsConstructor
public class FinancialOverviewController {

    private final FinancialOverviewFacade financialOverviewFacade;

    @GetMapping
    public ResponseEntity<FinancialOverviewResponse> getFinancialOverview(@RequestParam Long idUsuario) {
        return ResponseEntity.ok(financialOverviewFacade.getOverview(idUsuario));
    }
}
