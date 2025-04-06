package com.gestaolixoeletronico.controller;

import com.gestaolixoeletronico.entities.PontoColeta;
import com.gestaolixoeletronico.entities.Usuario;
import com.gestaolixoeletronico.service.PontoColetaService;
import com.gestaolixoeletronico.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/pontos-coleta")
public class PontoColetaController {

    private final PontoColetaService pontoColetaService;
    private final UsuarioService usuarioService;

    public PontoColetaController(PontoColetaService pontoColetaService, UsuarioService usuarioService) {
        this.pontoColetaService = pontoColetaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<PontoColeta> listarTodosPontos() {
        return pontoColetaService.listarPontos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PontoColeta> buscarPontoPorId(@PathVariable Long id) {
        Optional<PontoColeta> ponto = pontoColetaService.buscarPorId(id);
        return ponto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PontoColeta> criarPontoColeta(
            @RequestBody Map<String, Object> requestBody,
            UriComponentsBuilder uriBuilder) {

        Long usuarioId = Long.valueOf(requestBody.get("usuarioId").toString());
        PontoColeta pontoColeta = new PontoColeta();

        pontoColeta.setNome((String) requestBody.get("nome"));
        pontoColeta.setEndereco((String) requestBody.get("endereco"));
        pontoColeta.setNumero((String) requestBody.get("numero"));
        pontoColeta.setBairro((String) requestBody.get("bairro"));
        pontoColeta.setCidade((String) requestBody.get("cidade"));
        pontoColeta.setEstado((String) requestBody.get("estado"));
        pontoColeta.setDiasFuncionamento((String) requestBody.get("diasFuncionamento"));
        pontoColeta.setHorarioAbertura((String) requestBody.get("horarioAbertura"));
        pontoColeta.setHorarioFechamento((String) requestBody.get("horarioFechamento"));

        Usuario usuario = usuarioService.buscarPorId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        PontoColeta novoPonto = pontoColetaService.salvarPonto(pontoColeta, usuario);

        URI uri = uriBuilder.path("/pontos-coleta/{id}").buildAndExpand(novoPonto.getId()).toUri();
        return ResponseEntity.created(uri).body(novoPonto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PontoColeta> atualizarPontoColeta(
            @PathVariable Long id,
            @RequestBody PontoColeta pontoColeta,
            @RequestParam Long usuarioId) {

        Usuario usuario = usuarioService.buscarPorId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        PontoColeta pontoAtualizado = pontoColetaService.atualizarPonto(id, pontoColeta, usuario);
        return ResponseEntity.ok(pontoAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerPontoColeta(
            @PathVariable Long id,
            @RequestParam Long usuarioId) {

        Usuario usuario = usuarioService.buscarPorId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        pontoColetaService.deletarPonto(id, usuario);
        return ResponseEntity.noContent().build();
    }
}