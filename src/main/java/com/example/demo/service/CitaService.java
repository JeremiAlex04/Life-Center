package com.example.demo.service;

import com.example.demo.model.Cita;
import com.example.demo.model.HistorialClinico;
import com.example.demo.repository.CitaRepository;
import com.example.demo.repository.MedicoRepository;
import com.example.demo.repository.HorarioMedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class CitaService {
    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private HistorialClinicoService historialClinicoService;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private HorarioMedicoRepository horarioMedicoRepository;

    public List<Cita> findAll() {
        return citaRepository.findAll();
    }

    public Cita findById(Long id) {
        return citaRepository.findById(id).orElse(null);
    }

    @Transactional
    public Cita save(Cita cita) {
        // Bloqueo pesimista para evitar concurrencia en el mismo médico
        if (cita.getMedico() != null && cita.getMedico().getIdMedico() != null) {
            medicoRepository.findByIdWithLock(cita.getMedico().getIdMedico());
        }

        if (cita.getEstado() == null) {
            cita.setEstado(com.example.demo.model.EstadoCita.PENDIENTE);
        }

        LocalDate hoy = LocalDate.now();
        if (cita.getFecha().isBefore(hoy)) {
            throw new IllegalArgumentException("No se pueden agendar citas en fechas pasadas.");
        }

        if (cita.getFecha().isEqual(hoy) && cita.getHora().isBefore(LocalTime.now())) {
            throw new IllegalArgumentException("No se pueden agendar citas en horas pasadas.");
        }

        // Validar solapamiento de citas considerando la duración
        LocalTime horaInicio = cita.getHora();
        int duracion = (cita.getDuracionMinutos() != null) ? cita.getDuracionMinutos() : 30;
        LocalTime horaFin = horaInicio.plusMinutes(duracion);

        // Obtener todas las citas del médico en esa fecha
        List<Cita> citasExistentes = citaRepository.findCitasParaValidarSolapamiento(
                cita.getMedico().getIdMedico(),
                cita.getFecha());

        // Validar solapamiento en Java
        for (Cita citaExistente : citasExistentes) {
            // Ignorar la misma cita si estamos editando
            if (cita.getIdCita() != null && cita.getIdCita().equals(citaExistente.getIdCita())) {
                continue;
            }

            LocalTime horaExistenteInicio = citaExistente.getHora();
            int duracionExistente = (citaExistente.getDuracionMinutos() != null) ? citaExistente.getDuracionMinutos()
                    : 30;
            LocalTime horaExistenteFin = horaExistenteInicio.plusMinutes(duracionExistente);

            // Verificar solapamiento: las citas se solapan si:
            // horaInicio < horaExistenteFin AND horaFin > horaExistenteInicio
            if (horaInicio.isBefore(horaExistenteFin) && horaFin.isAfter(horaExistenteInicio)) {
                throw new IllegalArgumentException(
                        String.format("Ya existe una cita que se solapa con este horario. " +
                                "Cita existente: %s - %s. Por favor, elija otro horario.",
                                horaExistenteInicio, horaExistenteFin));
            }
        }

        // Validar que la hora de la cita corresponda al turno del médico
        String turno = cita.getMedico().getTurno();
        LocalTime hora = cita.getHora();

        if (turno != null) {
            if (turno.equalsIgnoreCase("Mañana")) {
                if (hora.isBefore(LocalTime.of(7, 0)) || hora.isAfter(LocalTime.of(13, 0))) {
                    throw new IllegalArgumentException("El médico solo atiende en el turno Mañana (07:00 - 13:00).");
                }
            } else if (turno.equalsIgnoreCase("Tarde")) {
                if (hora.isBefore(LocalTime.of(14, 0)) || hora.isAfter(LocalTime.of(20, 0))) {
                    throw new IllegalArgumentException("El médico solo atiende en el turno Tarde (14:00 - 20:00).");
                }
            }
        }

        // Actualizar el estado del slot a RESERVADO
        if (cita.getIdCita() == null) { // Solo para nuevas citas
            List<com.example.demo.model.HorarioMedico> horarios = horarioMedicoRepository
                    .findByMedicoIdMedicoAndFecha(cita.getMedico().getIdMedico(), cita.getFecha());

            for (com.example.demo.model.HorarioMedico h : horarios) {
                if (h.getHoraInicio().equals(cita.getHora())) {
                    h.setEstado("RESERVADO");
                    horarioMedicoRepository.save(h);
                    break;
                }
            }
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
        historial.setFecha(cita.getFecha());
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