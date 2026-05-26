package com.br.startup.tolevBack.users.controllers;

import com.br.startup.tolevBack.users.api.IUserService;
import com.br.startup.tolevBack.users.internal.data.dtos.UsuarioRequest;
import com.br.startup.tolevBack.users.internal.data.dtos.UsuarioResponse;
import com.br.startup.tolevBack.users.internal.data.dtos.UsuarioAssinaturaResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUserService userService;

    @PostMapping
    public ResponseEntity<UsuarioResponse> createUser(@RequestBody UsuarioRequest request) {
        UsuarioResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> getUser(@PathVariable Long id) {
        UsuarioResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> updateUser(@PathVariable Long id, @RequestBody UsuarioRequest request) {
        UsuarioResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/assinaturas")
    public ResponseEntity<List<UsuarioAssinaturaResponse>> getUserAssinaturas(@PathVariable Long id) {
        List<UsuarioAssinaturaResponse> responses = userService.getUserAssinaturas(id);
        return ResponseEntity.ok(responses);
    }
}
