package com.br.startup.tolevBack.users.api.facade;

import com.br.startup.tolevBack.users.application.dto.request.LoginRequest;
import com.br.startup.tolevBack.users.application.dto.request.RegisterRequest;
import com.br.startup.tolevBack.users.application.dto.response.AuthResponse;
import com.br.startup.tolevBack.users.application.usecase.commands.AuthenticateUserService;
import com.br.startup.tolevBack.users.application.usecase.commands.RegisterUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthFacade {

    private final RegisterUserService registerUser;
    private final AuthenticateUserService authenticateUser;

    public AuthResponse register(RegisterRequest request) {
        return registerUser.execute(request);
    }

    public AuthResponse login(LoginRequest request) {
        return authenticateUser.execute(request);
    }
}
