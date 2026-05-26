package com.br.startup.tolevBack.users.internal.services;

import com.br.startup.tolevBack.shared.exceptions.NotFoundException;
import com.br.startup.tolevBack.users.api.IUserService;
import com.br.startup.tolevBack.users.internal.data.dtos.UsuarioRequest;
import com.br.startup.tolevBack.users.internal.data.dtos.UsuarioResponse;
import com.br.startup.tolevBack.users.internal.data.dtos.UsuarioAssinaturaResponse;
import com.br.startup.tolevBack.users.internal.entities.Usuario;
import com.br.startup.tolevBack.users.internal.mapper.UserMapper;
import com.br.startup.tolevBack.users.internal.repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService implements IUserService {

    @Autowired
    private IUserRepository userRepository;

    @Transactional
    public UsuarioResponse createUser(UsuarioRequest request) {
        Usuario usuario = UserMapper.toEntity(request);
        Usuario saved = userRepository.save(usuario);
        return UserMapper.toResponse(saved);
    }

    public UsuarioResponse getUserById(Long id) {
        Usuario usuario = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Usuário não encontrado com id: " + id));
        return UserMapper.toResponse(usuario);
    }

    @Transactional
    public UsuarioResponse updateUser(Long id, UsuarioRequest request) {
        Usuario usuario = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Usuário não encontrado com id: " + id));

        usuario.setNome(request.nome());
        usuario.setGenero(request.genero());
        usuario.setDataNascimento(request.dataNascimento());
        usuario.setNomeUsuario(request.nomeUsuario());
        usuario.setSenha(request.senha());
        usuario.setEmail(request.email());

        Usuario updated = userRepository.save(usuario);
        return UserMapper.toResponse(updated);
    }

    @Transactional
    public void deleteUser(Long id) {
        Usuario usuario = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Usuário não encontrado com id: " + id));
        userRepository.delete(usuario);
    }

    public List<UsuarioAssinaturaResponse> getUserAssinaturas(Long idUsuario) {
        Usuario usuario = userRepository.findById(idUsuario)
            .orElseThrow(() -> new NotFoundException("Usuário não encontrado com id: " + idUsuario));
        return usuario.getUsuarioAssinaturas().stream()
            .map(UserMapper::toAssinaturaResponse)
            .toList();
    }
}
