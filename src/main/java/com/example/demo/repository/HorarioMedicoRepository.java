package com.example.demo.repository;

import com.example.demo.model.HorarioMedico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface HorarioMedicoRepository extends JpaRepository<HorarioMedico, Long> {
    List<HorarioMedico> findByMedicoIdMedicoAndFecha(Long idMedico, LocalDate fecha);

    @Query("SELECT COUNT(h) > 0 FROM HorarioMedico h WHERE h.medico.idMedico = :idMedico AND h.fecha = :fecha")
    boolean existsByMedicoAndFecha(@Param("idMedico") Long idMedico, @Param("fecha") LocalDate fecha);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT h FROM HorarioMedico h WHERE h.medico.idMedico = :medicoId AND h.fecha = :fecha AND h.horaInicio = :horaInicio")
    Optional<HorarioMedico> findByMedicoFechaHoraForUpdate(@Param("medicoId") Long medicoId,
            @Param("fecha") LocalDate fecha,
            @Param("horaInicio") LocalTime horaInicio);

    void deleteByMedicoIdMedicoAndFechaGreaterThanEqual(Long idMedico, LocalDate fecha);
}
