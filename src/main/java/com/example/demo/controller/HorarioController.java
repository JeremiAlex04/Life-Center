package com.example.demo.controller;

import com.example.demo.model.HorarioMedico;
import com.example.demo.repository.HorarioMedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/horarios")
public class HorarioController {

    @Autowired
    private HorarioMedicoRepository horarioRepository;

    @Autowired
    private com.example.demo.service.HorarioService horarioService;

    @Autowired
    private com.example.demo.service.MedicoService medicoService;

    @GetMapping("/disponibles")
    public List<HorarioMedico> obtenerHorariosDisponibles(
            @RequestParam Long medicoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        // Obtener todos los horarios del médico para la fecha
        List<HorarioMedico> todosLosHorarios = horarioRepository.findByMedicoIdMedicoAndFecha(medicoId, fecha);

        // Si no hay horarios y la fecha es hoy o futura, intentar generarlos
        if (todosLosHorarios.isEmpty() && !fecha.isBefore(LocalDate.now())) {
            medicoService.findById(medicoId).ifPresent(medico -> {
                horarioService.generarHorarios(medico, fecha, fecha);
            });
            // Volver a buscar después de generar
            todosLosHorarios = horarioRepository.findByMedicoIdMedicoAndFecha(medicoId, fecha);
        }

        // Filtrar solo los DISPONIBLES
        return todosLosHorarios.stream()
                .filter(h -> "DISPONIBLE".equalsIgnoreCase(h.getEstado()))
                .sorted((h1, h2) -> h1.getHoraInicio().compareTo(h2.getHoraInicio()))
                .collect(Collectors.toList());
    }
}
