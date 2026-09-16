package com.br.startup.tolevBack.finance.api.controller;

import com.br.startup.tolevBack.finance.api.facade.TransactionFacade;
import com.br.startup.tolevBack.finance.application.dto.request.TransactionCategoryRequest;
import com.br.startup.tolevBack.finance.application.dto.request.TransactionRequest;
import com.br.startup.tolevBack.finance.application.dto.response.TransactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    /**
     * Despesas do período que pedem categoria — a fila da tela de classificação.
     * Inclui as sem categoria nenhuma e as que caíram em "Outros", que na
     * prática quer dizer "decido depois".
     *
     * <p>A janela vem do mesmo jeito que em {@code /graphs/spending-by-category}:
     * {@code inicio}+{@code fim} dizem o período exato e ganham de {@code meses},
     * que conta para trás a partir de hoje. É o par explícito que descreve o
     * período de um extrato importado, que não termina hoje.
     */
    @GetMapping("/to-classify")
    public ResponseEntity<List<TransactionResponse>> getToClassify(
            @RequestParam Long idUsuario,
            @RequestParam(defaultValue = "1") Integer meses,
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate fim,
            @RequestParam(required = false) Long idBanco) {

        if (inicio != null && fim != null) {
            if (inicio.isAfter(fim)) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(transactionFacade.getToClassify(idUsuario, inicio, fim, idBanco));
        }
        return ResponseEntity.ok(transactionFacade.getToClassify(idUsuario, meses, idBanco));
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@RequestBody TransactionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionFacade.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransaction(@PathVariable Long id) {
        return ResponseEntity.ok(transactionFacade.getById(id));
    }

    /** Classifica ou reclassifica uma transação já gravada. */
    @PatchMapping("/{id}/category")
    public ResponseEntity<TransactionResponse> updateCategory(
            @PathVariable Long id,
            @RequestBody TransactionCategoryRequest request) {
        return ResponseEntity.ok(transactionFacade.updateCategory(id, request));
    }
}
