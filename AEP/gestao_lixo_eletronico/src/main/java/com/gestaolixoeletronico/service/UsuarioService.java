package com.gestaolixoeletronico.service;

import com.gestaolixoeletronico.entities.Usuario;
import com.gestaolixoeletronico.enums.TipoUsuario;
import com.gestaolixoeletronico.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Usuario salvarUsuario(Usuario usuario) {
        // Define como COMUM por padrão se não for especificado
        if (usuario.getTipoUsuario() == null) {
            usuario.setTipoUsuario(TipoUsuario.COMUM);
        }
        return usuarioRepository.save(usuario);
    }

    public Usuario criarAdmin(Usuario usuario) {
        usuario.setTipoUsuario(TipoUsuario.ADMIN);
        return usuarioRepository.save(usuario);
    }
}