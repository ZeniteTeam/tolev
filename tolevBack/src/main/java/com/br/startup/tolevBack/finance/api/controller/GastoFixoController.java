package com.br.startup.tolevBack.finance.api.controller;

import com.br.startup.tolevBack.finance.api.facade.GastoFixoFacade;
import com.br.startup.tolevBack.finance.application.dto.request.GastoFixoRequest;
import com.br.startup.tolevBack.finance.application.dto.response.GastoFixoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Os gastos fixos do usuário — aluguel, internet, academia.
 *
 * <p>É o insumo que falta para o app dizer quanto sobra de verdade. Sem ele,
 * "renda menos parcelas" é o mais perto de sobra que dá para calcular, e isso
 * não é sobra nenhuma.
 */
@RestController
@RequestMapping("/gastos-fixos")
@RequiredArgsConstructor
public class GastoFixoController {

    private final GastoFixoFacade gastoFixoFacade;

    @GetMapping
    public ResponseEntity<List<GastoFixoResponse>> getGastosFixos(@RequestParam Long idUsuario) {
        return ResponseEntity.ok(gastoFixoFacade.getAll(idUsuario));
    }

    @PostMapping
    public ResponseEntity<GastoFixoResponse> createGastoFixo(@RequestBody GastoFixoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gastoFixoFacade.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GastoFixoResponse> updateGastoFixo(
            @PathVariable Long id,
            @RequestParam Long idUsuario,
            @RequestBody GastoFixoRequest request) {
        return ResponseEntity.ok(gastoFixoFacade.update(id, idUsuario, request));
    }

    /** Desativa. A linha fica, para o histórico de valores sobreviver. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGastoFixo(
            @PathVariable Long id,
            @RequestParam Long idUsuario) {
        gastoFixoFacade.delete(id, idUsuario);
        return ResponseEntity.noContent().build();
    }

    /**
     * "Continua tudo assim" — carimba todos os gastos vivos com a data de hoje.
     * É o que mantém o cadastro confiável sem exigir que a pessoa reabra cada
     * um todo mês.
     */
    @PostMapping("/confirmacao")
    public ResponseEntity<Void> confirmarGastosFixos(@RequestParam Long idUsuario) {
        gastoFixoFacade.confirmar(idUsuario);
        return ResponseEntity.noContent().build();
    }
}
