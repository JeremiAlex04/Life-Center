package com.example.demo.repository;

import com.example.demo.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional; 

public interface PacienteRepository extends JpaRepository<Paciente, Long>{
    Optional<Paciente> findByDni(String dni);
    Optional<Paciente> findByUsuarioUsername(String username);
}