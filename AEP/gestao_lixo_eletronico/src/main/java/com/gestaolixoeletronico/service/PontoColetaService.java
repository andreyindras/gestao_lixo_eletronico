package com.gestaolixoeletronico.service;

import com.gestaolixoeletronico.entities.PontoColeta;
import com.gestaolixoeletronico.entities.Usuario;
import com.gestaolixoeletronico.enums.TipoUsuario;
import com.gestaolixoeletronico.repository.PontoColetaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PontoColetaService {

    private final PontoColetaRepository pontoColetaRepository;

    public PontoColetaService(PontoColetaRepository pontoColetaRepository) {
        this.pontoColetaRepository = pontoColetaRepository;
    }

    public List<PontoColeta> listarPontos() {
        return pontoColetaRepository.findAll();
    }

    public Optional<PontoColeta> buscarPorId(Long id) {
        return pontoColetaRepository.findById(id);
    }

    public PontoColeta salvarPonto(PontoColeta pontoColeta, Usuario usuario) {
        if (usuario.getTipoUsuario() == TipoUsuario.ADMIN) {
            pontoColeta.setUsuario(usuario);
            return pontoColetaRepository.save(pontoColeta);
        }
        throw new RuntimeException("Apenas administradores podem cadastrar pontos de coleta!");
    }

    public PontoColeta atualizarPonto(Long id, PontoColeta pontoColetaAtualizado, Usuario usuario) {
        if (usuario.getTipoUsuario() != TipoUsuario.ADMIN) {
            throw new RuntimeException("Apenas administradores podem atualizar pontos de coleta!");
        }

        return pontoColetaRepository.findById(id)
                .map(pontoExistente -> {
                    pontoExistente.setNome(pontoColetaAtualizado.getNome());
                    pontoExistente.setEndereco(pontoColetaAtualizado.getEndereco());
                    pontoExistente.setNumero(pontoColetaAtualizado.getNumero());
                    pontoExistente.setBairro(pontoColetaAtualizado.getBairro());
                    pontoExistente.setCidade(pontoColetaAtualizado.getCidade());
                    pontoExistente.setEstado(pontoColetaAtualizado.getEstado());
                    pontoExistente.setDiasFuncionamento(pontoColetaAtualizado.getDiasFuncionamento());
                    pontoExistente.setHorarioAbertura(pontoColetaAtualizado.getHorarioAbertura());
                    pontoExistente.setHorarioFechamento(pontoColetaAtualizado.getHorarioFechamento());
                    return pontoColetaRepository.save(pontoExistente);
                })
                .orElseThrow(() -> new RuntimeException("Ponto de coleta não encontrado"));
    }

    public void deletarPonto(Long id, Usuario usuario) {
        if (usuario.getTipoUsuario() != TipoUsuario.ADMIN) {
            throw new RuntimeException("Apenas administradores podem deletar pontos de coleta!");
        }

        PontoColeta ponto = pontoColetaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ponto de coleta não encontrado"));
        pontoColetaRepository.delete(ponto);
    }
}