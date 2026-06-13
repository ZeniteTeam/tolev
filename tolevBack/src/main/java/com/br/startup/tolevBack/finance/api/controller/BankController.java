package com.br.startup.tolevBack.finance.api.controller;

import com.br.startup.tolevBack.finance.api.facade.BankFacade;
import com.br.startup.tolevBack.finance.application.dto.response.BankResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/banks")
@RequiredArgsConstructor
public class BankController {

    private final BankFacade bankFacade;

    @GetMapping
    public ResponseEntity<List<BankResponse>> getBanks() {
        return ResponseEntity.ok(bankFacade.getAll());
    }
}
