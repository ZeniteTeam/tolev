package com.br.startup.tolevBack.progression.api.controller;

import com.br.startup.tolevBack.progression.api.facade.DividaFacade;
import com.br.startup.tolevBack.progression.application.dto.request.AddValueToDividaRequest;
import com.br.startup.tolevBack.progression.application.dto.request.DividaRequest;
import com.br.startup.tolevBack.progression.application.dto.request.RegisterPaymentRequest;
import com.br.startup.tolevBack.progression.application.dto.response.DividaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dividas")
@RequiredArgsConstructor
public class DividaController {

    private final DividaFacade dividaFacade;

    @GetMapping
    public ResponseEntity<List<DividaResponse>> getDividas(@RequestParam Long idUsuario) {
        return ResponseEntity.ok(dividaFacade.getAll(idUsuario));
    }

    @PostMapping
    public ResponseEntity<DividaResponse> createDivida(@RequestBody DividaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(dividaFacade.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DividaResponse> getDivida(@PathVariable Long id) {
        return ResponseEntity.ok(dividaFacade.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DividaResponse> updateDivida(@PathVariable Long id, @RequestBody DividaRequest request) {
        return ResponseEntity.ok(dividaFacade.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDivida(@PathVariable Long id) {
        dividaFacade.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addValueToDivida(@RequestBody AddValueToDividaRequest request) {
        dividaFacade.addNewValue(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/pagamento")
    public ResponseEntity<DividaResponse> registerPayment(@RequestBody RegisterPaymentRequest request) {
        return ResponseEntity.ok(dividaFacade.registerPayment(request));
    }
}
