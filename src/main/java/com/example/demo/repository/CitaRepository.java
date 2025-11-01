package com.example.demo.repository;
import com.example.demo.model.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {
    @Query("SELECT c FROM Cita c WHERE c.medico.id_medico = :medicoId AND c.fecha = :fecha")
    List<Cita> findByMedicoIdAndFecha(@Param("medicoId") Long medicoId, @Param("fecha") LocalDate fecha);
}