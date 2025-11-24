package com.example.demo.repository;

import com.example.demo.model.HistorialClinico;
import com.example.demo.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialClinicoRepository extends JpaRepository<HistorialClinico, Long> {
    List<HistorialClinico> findByPaciente(Paciente paciente);
}
