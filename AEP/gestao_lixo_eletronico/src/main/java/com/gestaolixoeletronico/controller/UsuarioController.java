package com.gestaolixoeletronico.controller;

import com.gestaolixoeletronico.dto.LoginDTO;
import com.gestaolixoeletronico.entities.Usuario;
import com.gestaolixoeletronico.enums.TipoUsuario;
import com.gestaolixoeletronico.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<Usuario> listarTodosUsuarios() {
        return usuarioService.listarUsuarios();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarUsuarioPorId(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.buscarPorId(id);
        return usuario.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@RequestBody LoginDTO loginDTO) {
        Optional<Usuario> usuario = usuarioService.buscarPorEmail(loginDTO.getEmail());

        if (usuario.isPresent() && usuario.get().getSenha().equals(loginDTO.getSenha())) {
            return ResponseEntity.ok(usuario.get());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping
    public ResponseEntity<Usuario> criarUsuarioComum(
            @RequestBody Usuario usuario,
            UriComponentsBuilder uriBuilder) {

        Usuario novoUsuario = usuarioService.salvarUsuario(usuario);

        URI uri = uriBuilder.path("/usuarios/{id}").buildAndExpand(novoUsuario.getId()).toUri();
        return ResponseEntity.created(uri).body(novoUsuario);
    }

    @PostMapping("/admin")
    public ResponseEntity<Usuario> criarUsuarioAdmin(
            @RequestBody Usuario usuario,
            UriComponentsBuilder uriBuilder) {

        Usuario novoAdmin = usuarioService.criarAdmin(usuario);

        URI uri = uriBuilder.path("/usuarios/{id}").buildAndExpand(novoAdmin.getId()).toUri();
        return ResponseEntity.created(uri).body(novoAdmin);
    }
}