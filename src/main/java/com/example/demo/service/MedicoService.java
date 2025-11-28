package com.example.demo.service;

import com.example.demo.exception.ConflictoHorarioException;
import com.example.demo.exception.ConsultorioNoDisponibleException;
import com.example.demo.exception.IncompatibilidadEspecialidadException;
import com.example.demo.model.Consultorio;
import com.example.demo.model.Medico;
import com.example.demo.repository.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MedicoService {

    @Autowired
    private MedicoRepository medicoRepository;

    public List<Medico> findAll() {
        return medicoRepository.findAll();
    }

    public Optional<Medico> findById(Long id) {
        return medicoRepository.findById(id);
    }

    /**
     * Guarda un médico después de validar la asignación del consultorio.
     */
    public Medico save(Medico medico) {
        validarAsignacionConsultorio(medico);
        return medicoRepository.save(medico);
    }

    public void deleteById(Long id) {
        medicoRepository.deleteById(id);
    }

    public Medico findByUsername(String username) {
        return medicoRepository.findByUsuarioUsername(username).orElse(null);
    }

    /**
     * Valida que la asignación de un médico a un consultorio sea válida.
     * 
     * Validaciones:
     * 1. El consultorio debe estar en estado DISPONIBLE
     * 2. No debe haber otro médico en el mismo consultorio y turno
     * 3. La especialidad del médico debe ser compatible con el tipo del consultorio
     */
    private void validarAsignacionConsultorio(Medico medico) {
        if (medico.getConsultorio() == null || medico.getTurno() == null) {
            return;
        }

        Consultorio consultorio = medico.getConsultorio();

        // Validación 1: Disponibilidad
        if (!"DISPONIBLE".equals(consultorio.getEstado())) {
            throw new ConsultorioNoDisponibleException(
                    String.format("El consultorio %s no está disponible. Estado actual: %s",
                            consultorio.getNumero(), consultorio.getEstado()));
        }

        // Validación 2: Conflicto de horarios
        Long medicoId = medico.getIdMedico() != null ? medico.getIdMedico() : 0L;
        List<Medico> medicosEnMismoTurno = medicoRepository
                .findByConsultorioAndTurnoExcludingMedico(
                        consultorio.getIdConsultorio(),
                        medico.getTurno(),
                        medicoId);

        if (!medicosEnMismoTurno.isEmpty()) {
            String nombresMedicos = medicosEnMismoTurno.stream()
                    .map(m -> "Dr(a). " + m.getNombres() + " " + m.getApellidos())
                    .collect(Collectors.joining(", "));

            throw new ConflictoHorarioException(
                    String.format("Conflicto de horario: El consultorio %s ya está asignado a %s en el turno %s",
                            consultorio.getNumero(), nombresMedicos, medico.getTurno()));
        }

        // Validación 3: Compatibilidad de especialidad
        if (consultorio.getTipo() != null && !consultorio.getTipo().trim().isEmpty()) {
            String tipoConsultorio = consultorio.getTipo().trim();
            String especialidadMedico = medico.getEspecialidad().trim();

            // Los consultorios tipo "GENERAL" aceptan cualquier especialidad
            if (!"GENERAL".equalsIgnoreCase(tipoConsultorio)) {
                if (!especialidadMedico.equalsIgnoreCase(tipoConsultorio)) {
                    throw new IncompatibilidadEspecialidadException(
                            String.format(
                                    "Incompatibilidad de especialidad: El consultorio %s es de tipo '%s', " +
                                            "pero el médico es especialista en '%s'. " +
                                            "Asigne al médico a un consultorio compatible o de tipo GENERAL.",
                                    consultorio.getNumero(), tipoConsultorio, especialidadMedico));
                }
            }
        }
    }
}