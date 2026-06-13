package com.br.startup.tolevBack.users.api.controller;

import com.br.startup.tolevBack.users.api.facade.UserFacade;
import com.br.startup.tolevBack.users.application.dto.request.UsuarioRequest;
import com.br.startup.tolevBack.users.application.dto.response.UsuarioAssinaturaResponse;
import com.br.startup.tolevBack.users.application.dto.response.UsuarioResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserFacade userFacade;

    @PostMapping
    public ResponseEntity<UsuarioResponse> createUser(@RequestBody UsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userFacade.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userFacade.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> updateUser(@PathVariable Long id, @RequestBody UsuarioRequest request) {
        return ResponseEntity.ok(userFacade.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userFacade.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/assinaturas")
    public ResponseEntity<List<UsuarioAssinaturaResponse>> getUserAssinaturas(@PathVariable Long id) {
        return ResponseEntity.ok(userFacade.getAssinaturas(id));
    }
}
