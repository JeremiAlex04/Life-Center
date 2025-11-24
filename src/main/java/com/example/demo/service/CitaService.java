package com.example.demo.service;

import com.example.demo.model.Cita;
import com.example.demo.model.HistorialClinico;
import com.example.demo.repository.CitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class CitaService {
    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private HistorialClinicoService historialClinicoService; // Inyectar HistorialClinicoService

    public List<Cita> findAll() {
        return citaRepository.findAll();
    }

    public Cita findById(Long id) {
        return citaRepository.findById(id).orElse(null);
    }

    public Cita save(Cita cita) {
        if (cita.getEstado() == null) {
            cita.setEstado(com.example.demo.model.EstadoCita.PENDIENTE);
        }

        if (cita.getFecha().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("No se pueden agendar citas en fechas pasadas.");
        }

        if (citaRepository.existeCitaMedico(cita.getMedico().getIdMedico(), cita.getFecha(),
                cita.getHora())) {
            throw new IllegalArgumentException("El médico ya tiene una cita agendada en ese horario.");
        }

        return citaRepository.save(cita);
    }

    public List<Cita> findTodayCitasByMedicoId(Long medicoId) {
        return citaRepository.findByMedicoIdAndFecha(medicoId, LocalDate.now());
    }

    public List<Cita> findUpcomingCitasByMedicoId(Long medicoId) {
        return citaRepository.findUpcomingCitasByMedicoId(medicoId, LocalDate.now());
    }

    public long countTodayCitasByMedicoId(Long medicoId) {
        return citaRepository.contarCitasPorMedicoYFecha(medicoId, LocalDate.now());
    }

    public long countUpcomingCitasByMedicoId(Long medicoId) {
        return citaRepository.contarCitasFuturasPorMedico(medicoId, LocalDate.now());
    }

    public List<Cita> findByPacienteId(Long pacienteId) {
        return citaRepository.buscarPorPacienteId(pacienteId);
    }

    public void actualizarEstadoCita(Long citaId, com.example.demo.model.EstadoCita nuevoEstado) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));
        cita.setEstado(nuevoEstado);
        citaRepository.save(cita);
    }

    public void finalizarCita(Long citaId, String diagnostico, String tratamiento, String observaciones) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));

        cita.setDiagnostico(diagnostico);
        cita.setTratamiento(tratamiento);
        cita.setObservaciones(observaciones);
        cita.setEstado(com.example.demo.model.EstadoCita.COMPLETADA);

        citaRepository.save(cita);

        // Crear y guardar el registro en el historial clínico
        HistorialClinico historial = new HistorialClinico();
        historial.setPaciente(cita.getPaciente());
        historial.setFecha(cita.getFecha()); // O LocalDate.now() si prefieres la fecha de finalización
        historial.setDiagnostico(diagnostico);
        historial.setTratamiento(tratamiento);
        historial.setObservaciones(observaciones);
        historialClinicoService.save(historial);
    }

    public List<com.example.demo.model.Paciente> findPacientesByMedicoId(Long medicoId) {
        return citaRepository.findPacientesByMedicoId(medicoId);
    }

    public List<Cita> findCitasByMedicoAndPaciente(Long medicoId, Long pacienteId) {
        return citaRepository.findByMedicoIdAndPacienteId(medicoId, pacienteId);
    }

    public long countAllCitas() {
        return citaRepository.count();
    }
}