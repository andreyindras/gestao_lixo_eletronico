package com.gestaolixoeletronico.repository;

import com.gestaolixoeletronico.entities.PontoColeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PontoColetaRepository extends JpaRepository<PontoColeta, Long> {
    List<PontoColeta> findAllByUsuarioId(Long usuarioId);
}