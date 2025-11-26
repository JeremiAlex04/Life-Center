package com.example.demo.repository;

import com.example.demo.model.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {
        @Query("SELECT c FROM Cita c WHERE c.medico.idMedico = :medicoId AND c.fecha = :fecha")
        List<Cita> findByMedicoIdAndFecha(Long medicoId, LocalDate fecha);

        @Query("SELECT c FROM Cita c WHERE c.medico.idMedico = :medicoId AND c.fecha >= :fecha ORDER BY c.fecha ASC, c.hora ASC")
        List<Cita> findUpcomingCitasByMedicoId(@Param("medicoId") Long medicoId, @Param("fecha") LocalDate fecha);

        @Query("SELECT COUNT(c) FROM Cita c WHERE c.medico.idMedico = :medicoId AND c.fecha = :fecha")
        long contarCitasPorMedicoYFecha(@Param("medicoId") Long medicoId, @Param("fecha") LocalDate fecha);

        @Query("SELECT COUNT(c) FROM Cita c WHERE c.medico.idMedico = :medicoId AND c.fecha >= :fecha")
        long contarCitasFuturasPorMedico(@Param("medicoId") Long medicoId, @Param("fecha") LocalDate fecha);

        @Query("SELECT DISTINCT c.paciente FROM Cita c WHERE c.medico.idMedico = :medicoId")
        List<com.example.demo.model.Paciente> findPacientesByMedicoId(@Param("medicoId") Long medicoId);

        @Query("SELECT c FROM Cita c WHERE c.medico.idMedico = :medicoId AND c.paciente.idPaciente = :pacienteId ORDER BY c.fecha DESC, c.hora DESC")
        List<Cita> findByMedicoIdAndPacienteId(@Param("medicoId") Long medicoId, @Param("pacienteId") Long pacienteId);

        // Buscar citas del mismo médico en la misma fecha para validar solapamiento en
        // Java
        @Query("SELECT c FROM Cita c WHERE c.medico.idMedico = :medicoId AND c.fecha = :fecha")
        List<Cita> findCitasParaValidarSolapamiento(@Param("medicoId") Long medicoId, @Param("fecha") LocalDate fecha);

        @Query("SELECT c FROM Cita c WHERE c.paciente.idPaciente = :pacienteId")
        List<Cita> buscarPorPacienteId(@Param("pacienteId") Long pacienteId);
}