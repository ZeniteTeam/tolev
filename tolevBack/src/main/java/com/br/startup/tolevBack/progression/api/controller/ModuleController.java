package com.br.startup.tolevBack.progression.api.controller;

import com.br.startup.tolevBack.progression.api.facade.ModuleFacade;
import com.br.startup.tolevBack.progression.application.dto.response.MapaModuloResponse;
import com.br.startup.tolevBack.progression.application.dto.response.ProgressaoModuloResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/progression/modules")
@RequiredArgsConstructor
public class ModuleController {

    private final ModuleFacade moduleFacade;

    @GetMapping("/{id}")
    public ResponseEntity<MapaModuloResponse> getModule(@PathVariable Long id) {
        return ResponseEntity.ok(moduleFacade.getById(id));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<ProgressaoModuloResponse> completeModule(
            @PathVariable Long id,
            @RequestParam Long idUsuario) {
        return ResponseEntity.ok(moduleFacade.complete(id, idUsuario));
    }
}
