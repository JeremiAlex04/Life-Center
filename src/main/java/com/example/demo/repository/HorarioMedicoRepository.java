package com.example.demo.repository;

import com.example.demo.model.HorarioMedico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface HorarioMedicoRepository extends JpaRepository<HorarioMedico, Long> {
    List<HorarioMedico> findByMedicoIdMedicoAndFecha(Long idMedico, LocalDate fecha);

    @Query("SELECT COUNT(h) > 0 FROM HorarioMedico h WHERE h.medico.idMedico = :idMedico AND h.fecha = :fecha")
    boolean existsByMedicoAndFecha(@Param("idMedico") Long idMedico, @Param("fecha") LocalDate fecha);

    void deleteByMedicoIdMedicoAndFechaGreaterThanEqual(Long idMedico, LocalDate fecha);
}
