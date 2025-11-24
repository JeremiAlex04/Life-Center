package com.example.demo.repository;

import com.example.demo.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

public interface MedicoRepository extends JpaRepository<Medico, Long> {
    Optional<Medico> findByUsuarioUsername(String username);

    Optional<Medico> findByNumeroColegiatura(String numeroColegiatura);

    @Query("SELECT m FROM Medico m WHERE m.usuario.id = :usuarioId")
    Optional<Medico> findByUsuarioId(@Param("usuarioId") Long usuarioId);

    List<Medico> findByConsultorio(com.example.demo.model.Consultorio consultorio);

    long countByConsultorio(com.example.demo.model.Consultorio consultorio);
}
