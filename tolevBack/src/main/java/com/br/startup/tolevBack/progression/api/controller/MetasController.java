package com.br.startup.tolevBack.progression.api.controller;

import com.br.startup.tolevBack.progression.api.facade.MetaFacade;
import com.br.startup.tolevBack.progression.application.dto.request.MetaRequest;
import com.br.startup.tolevBack.progression.application.dto.response.MetaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/metas")
@RequiredArgsConstructor
public class MetasController {

    private final MetaFacade metaFacade;

    @GetMapping
    public ResponseEntity<List<MetaResponse>> getMetas(@RequestParam Long idUsuario) {
        return ResponseEntity.ok(metaFacade.getAll(idUsuario));
    }

    @PostMapping
    public ResponseEntity<MetaResponse> createMeta(@RequestBody MetaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(metaFacade.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetaResponse> getMeta(@PathVariable Long id) {
        return ResponseEntity.ok(metaFacade.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MetaResponse> updateMeta(@PathVariable Long id, @RequestBody MetaRequest request) {
        return ResponseEntity.ok(metaFacade.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeta(@PathVariable Long id) {
        metaFacade.delete(id);
        return ResponseEntity.noContent().build();
    }
}
