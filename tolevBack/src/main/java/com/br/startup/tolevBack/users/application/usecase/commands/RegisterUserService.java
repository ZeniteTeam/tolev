package com.br.startup.tolevBack.users.application.usecase.commands;

import com.br.startup.tolevBack.config.security.JwtService;
import com.br.startup.tolevBack.users.application.dto.request.RegisterRequest;
import com.br.startup.tolevBack.users.application.dto.response.AuthResponse;
import com.br.startup.tolevBack.users.exceptions.EmailAlreadyExistsException;
import com.br.startup.tolevBack.users.internal.entity.Usuario;
import com.br.startup.tolevBack.users.internal.enums.PapelUsuario;
import com.br.startup.tolevBack.users.internal.mapper.UserMapper;
import com.br.startup.tolevBack.users.internal.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegisterUserService {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse execute(RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new EmailAlreadyExistsException("Já existe um usuário com o email: " + request.email());
        }
        if (userRepository.findByNomeUsuario(request.nomeUsuario()).isPresent()) {
            throw new EmailAlreadyExistsException("Já existe um usuário com o nome de usuário: " + request.nomeUsuario());
        }

        Usuario usuario = Usuario.builder()
                .nome(request.nome())
                .genero(request.genero())
                .dataNascimento(request.dataNascimento())
                .nomeUsuario(request.nomeUsuario())
                .email(request.email())
                .senha(passwordEncoder.encode(request.senha()))
                .papel(PapelUsuario.USER)
                .ativo(true)
                .build();

        Usuario salvo = userRepository.save(usuario);

        String token = jwtService.generateToken(
                User.builder()
                        .username(salvo.getEmail())
                        .password(salvo.getSenha())
                        .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + salvo.getPapel().name())))
                        .build());

        return new AuthResponse(token, "Bearer", jwtService.getExpiration(), UserMapper.toResponse(salvo));
    }
}
