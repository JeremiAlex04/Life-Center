package com.example.demo.service;

import com.example.demo.model.Cita;
import com.example.demo.model.EstadoCita;
import com.example.demo.model.HistorialClinico;
import com.example.demo.model.HorarioMedico;
import com.example.demo.model.Medico;
import com.example.demo.repository.CitaRepository;
import com.example.demo.repository.HorarioMedicoRepository;
import com.example.demo.repository.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
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
        if (cita.getPaciente() == null || cita.getMedico() == null || cita.getMedico().getIdMedico() == null
                || cita.getFecha() == null || cita.getHora() == null) {
            throw new IllegalArgumentException("Faltan datos obligatorios para registrar la cita.");
        }

        Long medicoId = cita.getMedico().getIdMedico();
        Medico medico = medicoRepository.findByIdWithLock(medicoId)
                .orElseThrow(() -> new IllegalArgumentException("Medico no encontrado."));

        cita.setMedico(medico);

        if (cita.getIdCita() == null) {
            cita.setEstado(EstadoCita.PENDIENTE);
            cita.setDiagnostico(null);
            cita.setTratamiento(null);
            cita.setObservaciones(null);
        }

        int duracion = (medico.getDuracionCita() != null) ? medico.getDuracionCita() : 30;
        cita.setDuracionMinutos(duracion);

        LocalDate hoy = LocalDate.now();
        if (cita.getFecha().isBefore(hoy)) {
            throw new IllegalArgumentException("No se pueden agendar citas en fechas pasadas.");
        }

        if (cita.getFecha().isEqual(hoy) && cita.getHora().isBefore(LocalTime.now())) {
            throw new IllegalArgumentException("No se pueden agendar citas en horas pasadas.");
        }

        LocalTime horaInicio = cita.getHora();
        LocalTime horaFin = horaInicio.plusMinutes(duracion);

        List<Cita> citasExistentes = citaRepository.findCitasParaValidarSolapamiento(
                medicoId,
                cita.getFecha(),
                Arrays.asList(EstadoCita.PENDIENTE, EstadoCita.CONFIRMADA));

        for (Cita citaExistente : citasExistentes) {
            if (cita.getIdCita() != null && cita.getIdCita().equals(citaExistente.getIdCita())) {
                continue;
            }

            LocalTime inicioExistente = citaExistente.getHora();
            int duracionExistente = (citaExistente.getDuracionMinutos() != null) ? citaExistente.getDuracionMinutos()
                    : 30;
            LocalTime finExistente = inicioExistente.plusMinutes(duracionExistente);

            if (horaInicio.isBefore(finExistente) && horaFin.isAfter(inicioExistente)) {
                throw new IllegalArgumentException(
                        String.format("Ya existe una cita en conflicto (%s - %s).", inicioExistente, finExistente));
            }
        }

        validarHorarioTurno(medico.getTurno(), horaInicio, horaFin);

        HorarioMedico slot = horarioMedicoRepository
                .findByMedicoFechaHoraForUpdate(medicoId, cita.getFecha(), cita.getHora())
                .orElseThrow(() -> new IllegalArgumentException("No existe un horario generado para ese medico y hora."));

        if (!"DISPONIBLE".equalsIgnoreCase(slot.getEstado())) {
            throw new IllegalArgumentException("El horario seleccionado ya no esta disponible.");
        }

        slot.setEstado("RESERVADO");
        horarioMedicoRepository.save(slot);

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

    @Transactional
    public void actualizarEstadoCita(Long citaId, EstadoCita nuevoEstado) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));
        validarTransicionEstado(cita.getEstado(), nuevoEstado);
        cita.setEstado(nuevoEstado);
        citaRepository.save(cita);
        sincronizarEstadoHorario(cita, nuevoEstado);
    }

    @Transactional
    public void actualizarEstadoCitaParaMedico(Long citaId, Long medicoId, EstadoCita nuevoEstado) {
        Cita cita = obtenerCitaDelMedico(citaId, medicoId);
        validarTransicionEstado(cita.getEstado(), nuevoEstado);
        cita.setEstado(nuevoEstado);
        citaRepository.save(cita);
        sincronizarEstadoHorario(cita, nuevoEstado);
    }

    @Transactional
    public void finalizarCita(Long citaId, String diagnostico, String tratamiento, String observaciones) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));
        finalizarCitaInterna(cita, diagnostico, tratamiento, observaciones);
    }

    @Transactional
    public void finalizarCitaParaMedico(Long citaId, Long medicoId, String diagnostico, String tratamiento,
            String observaciones) {
        Cita cita = obtenerCitaDelMedico(citaId, medicoId);
        finalizarCitaInterna(cita, diagnostico, tratamiento, observaciones);
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

    private Cita obtenerCitaDelMedico(Long citaId, Long medicoId) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));

        if (cita.getMedico() == null || cita.getMedico().getIdMedico() == null
                || !cita.getMedico().getIdMedico().equals(medicoId)) {
            throw new AccessDeniedException("No tiene permisos para modificar esta cita");
        }

        return cita;
    }

    private void validarHorarioTurno(String turno, LocalTime inicio, LocalTime fin) {
        if (turno == null) {
            return;
        }

        if (turno.equalsIgnoreCase("Manana") || turno.equalsIgnoreCase("Mañana")) {
            if (inicio.isBefore(LocalTime.of(7, 0)) || fin.isAfter(LocalTime.of(13, 0))) {
                throw new IllegalArgumentException("El medico solo atiende en turno manana (07:00 - 13:00).");
            }
            return;
        }

        if (turno.equalsIgnoreCase("Tarde")) {
            if (inicio.isBefore(LocalTime.of(14, 0)) || fin.isAfter(LocalTime.of(20, 0))) {
                throw new IllegalArgumentException("El medico solo atiende en turno tarde (14:00 - 20:00).");
            }
            return;
        }

        if (turno.equalsIgnoreCase("Noche")) {
            if (inicio.isBefore(LocalTime.of(20, 0)) || fin.isAfter(LocalTime.of(23, 59))) {
                throw new IllegalArgumentException("El medico solo atiende en turno noche (20:00 - 23:59).");
            }
        }
    }

    private void validarTransicionEstado(EstadoCita actual, EstadoCita nuevo) {
        if (nuevo == null) {
            throw new IllegalArgumentException("El estado de la cita es obligatorio.");
        }

        if (actual == null || actual == nuevo) {
            return;
        }

        boolean valida = switch (actual) {
            case PENDIENTE -> nuevo == EstadoCita.CONFIRMADA || nuevo == EstadoCita.CANCELADA;
            case CONFIRMADA -> nuevo == EstadoCita.COMPLETADA || nuevo == EstadoCita.CANCELADA;
            case CANCELADA, COMPLETADA -> false;
        };

        if (!valida) {
            throw new IllegalArgumentException(
                    String.format("Transicion de estado no permitida: %s -> %s", actual, nuevo));
        }
    }

    private void sincronizarEstadoHorario(Cita cita, EstadoCita estadoCita) {
        if (cita.getMedico() == null || cita.getMedico().getIdMedico() == null || cita.getFecha() == null
                || cita.getHora() == null) {
            return;
        }

        horarioMedicoRepository
                .findByMedicoFechaHoraForUpdate(cita.getMedico().getIdMedico(), cita.getFecha(), cita.getHora())
                .ifPresent(slot -> {
                    if (estadoCita == EstadoCita.CANCELADA) {
                        slot.setEstado("DISPONIBLE");
                    } else if (estadoCita == EstadoCita.COMPLETADA) {
                        slot.setEstado("OCUPADO");
                    } else {
                        slot.setEstado("RESERVADO");
                    }
                    horarioMedicoRepository.save(slot);
                });
    }

    private void finalizarCitaInterna(Cita cita, String diagnostico, String tratamiento, String observaciones) {
        validarTransicionEstado(cita.getEstado(), EstadoCita.COMPLETADA);

        cita.setDiagnostico(diagnostico);
        cita.setTratamiento(tratamiento);
        cita.setObservaciones(observaciones);
        cita.setEstado(EstadoCita.COMPLETADA);

        citaRepository.save(cita);
        sincronizarEstadoHorario(cita, EstadoCita.COMPLETADA);

        HistorialClinico historial = new HistorialClinico();
        historial.setPaciente(cita.getPaciente());
        historial.setFecha(cita.getFecha());
        historial.setDiagnostico(diagnostico);
        historial.setTratamiento(tratamiento);
        historial.setObservaciones(observaciones);
        historialClinicoService.save(historial);
    }
}
