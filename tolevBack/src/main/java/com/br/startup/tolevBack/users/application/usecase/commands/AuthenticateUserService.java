package com.br.startup.tolevBack.users.application.usecase.commands;

import com.br.startup.tolevBack.config.security.JwtService;
import com.br.startup.tolevBack.users.application.dto.request.LoginRequest;
import com.br.startup.tolevBack.users.application.dto.response.AuthResponse;
import com.br.startup.tolevBack.users.internal.entity.Usuario;
import com.br.startup.tolevBack.users.internal.mapper.UserMapper;
import com.br.startup.tolevBack.users.internal.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticateUserService {

    private final AuthenticationManager authenticationManager;
    private final IUserRepository userRepository;
    private final JwtService jwtService;

    public AuthResponse execute(LoginRequest request) {
        UserDetails userDetails = (UserDetails) authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.senha())
        ).getPrincipal();

        Usuario usuario = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com email: " + request.email()));

        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(token, "Bearer", jwtService.getExpiration(), UserMapper.toResponse(usuario));
    }
}
