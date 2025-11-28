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

    @Query("SELECT DISTINCT m.especialidad FROM Medico m")
    List<String> findDistinctEspecialidades();

    /**
     * Busca médicos asignados a un consultorio específico en un turno específico,
     * excluyendo un médico en particular (útil para validar al actualizar).
     */
    @Query("SELECT m FROM Medico m WHERE m.consultorio.idConsultorio = :consultorioId AND m.turno = :turno AND m.idMedico != :medicoId")
    List<Medico> findByConsultorioAndTurnoExcludingMedico(
            @Param("consultorioId") Long consultorioId,
            @Param("turno") String turno,
            @Param("medicoId") Long medicoId);

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT m FROM Medico m WHERE m.idMedico = :id")
    Optional<Medico> findByIdWithLock(@Param("id") Long id);
}
