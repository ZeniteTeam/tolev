package com.br.startup.tolevBack.progression.api.controller;

import com.br.startup.tolevBack.progression.api.facade.MapFacade;
import com.br.startup.tolevBack.progression.application.dto.response.MapaModuloResponse;
import com.br.startup.tolevBack.progression.application.dto.response.MapaProgressaoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/progression/maps")
@RequiredArgsConstructor
public class MapController {

    private final MapFacade mapFacade;

    @GetMapping
    public ResponseEntity<List<MapaProgressaoResponse>> getMaps() {
        return ResponseEntity.ok(mapFacade.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MapaProgressaoResponse> getMap(@PathVariable Long id) {
        return ResponseEntity.ok(mapFacade.getById(id));
    }

    @GetMapping("/{id}/modules")
    public ResponseEntity<List<MapaModuloResponse>> getMapModules(@PathVariable Long id) {
        return ResponseEntity.ok(mapFacade.getModules(id));
    }
}
