package com.br.startup.tolevBack.finance.api.controller;

import com.br.startup.tolevBack.finance.api.facade.TransactionFacade;
import com.br.startup.tolevBack.finance.application.dto.request.TransactionRequest;
import com.br.startup.tolevBack.finance.application.dto.response.TransactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionFacade transactionFacade;

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getTransactions(@RequestParam Long idUsuario) {
        return ResponseEntity.ok(transactionFacade.getAll(idUsuario));
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@RequestBody TransactionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionFacade.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransaction(@PathVariable Long id) {
        return ResponseEntity.ok(transactionFacade.getById(id));
    }
}
