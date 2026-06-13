package com.br.startup.tolevBack.finance.api.controller;

import com.br.startup.tolevBack.finance.api.facade.AccountFacade;
import com.br.startup.tolevBack.finance.application.dto.request.ConnectAccountRequest;
import com.br.startup.tolevBack.finance.application.dto.response.AccountBalanceResponse;
import com.br.startup.tolevBack.finance.application.dto.response.AccountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountFacade accountFacade;

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAccounts(@RequestParam Long idUsuario) {
        return ResponseEntity.ok(accountFacade.getAll(idUsuario));
    }

    @PostMapping("/connect")
    public ResponseEntity<AccountResponse> connectAccount(@RequestBody ConnectAccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountFacade.connect(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable Long id) {
        return ResponseEntity.ok(accountFacade.getById(id));
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<AccountBalanceResponse> getAccountBalance(@PathVariable Long id) {
        return ResponseEntity.ok(accountFacade.getBalance(id));
    }
}
