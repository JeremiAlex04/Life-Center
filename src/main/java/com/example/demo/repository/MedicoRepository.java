package com.example.demo.repository;

import com.example.demo.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

public interface  MedicoRepository extends JpaRepository<Medico, Long>{
    Optional<Medico> findByUsuarioUsername(String username);
}
