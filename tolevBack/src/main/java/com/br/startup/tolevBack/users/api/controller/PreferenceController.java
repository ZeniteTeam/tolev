package com.br.startup.tolevBack.users.api.controller;

import com.br.startup.tolevBack.users.api.facade.PreferenceFacade;
import com.br.startup.tolevBack.users.application.dto.request.PreferenciaFinanceiraRequest;
import com.br.startup.tolevBack.users.application.dto.response.PreferenciaFinanceiraResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/{id}/preferencias")
@RequiredArgsConstructor
public class PreferenceController {

    private final PreferenceFacade preferenceFacade;

    @GetMapping
    public ResponseEntity<PreferenciaFinanceiraResponse> getPreferencias(@PathVariable Long id) {
        return ResponseEntity.ok(preferenceFacade.get(id));
    }

    @PutMapping
    public ResponseEntity<PreferenciaFinanceiraResponse> updatePreferencias(
            @PathVariable Long id,
            @RequestBody PreferenciaFinanceiraRequest request) {
        return ResponseEntity.ok(preferenceFacade.update(id, request));
    }
}
