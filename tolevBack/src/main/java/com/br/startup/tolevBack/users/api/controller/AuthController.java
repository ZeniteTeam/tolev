package com.br.startup.tolevBack.users.api.controller;

import com.br.startup.tolevBack.users.api.facade.AuthFacade;
import com.br.startup.tolevBack.users.application.dto.request.LoginRequest;
import com.br.startup.tolevBack.users.application.dto.request.RegisterRequest;
import com.br.startup.tolevBack.users.application.dto.response.AuthResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthFacade authFacade;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authFacade.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authFacade.login(request));
    }
}
