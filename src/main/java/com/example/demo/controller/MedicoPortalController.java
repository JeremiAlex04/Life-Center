package com.example.demo.controller;

import com.example.demo.model.Cita;
import com.example.demo.model.Medico;
import com.example.demo.service.CitaService;
import com.example.demo.service.MedicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/medico") 
public class MedicoPortalController {

    @Autowired
    private MedicoService medicoService;

    @Autowired
    private CitaService citaService;

    /**
     * Maneja la solicitud GET para el dashboard del médico.
     */
    @GetMapping("/dashboard")
    public String viewMedicoDashboard(Model model, Authentication authentication) {
        String username = authentication.getName();
        Medico medico = medicoService.findByUsername(username);

        if (medico != null) {
            List<Cita> citas = citaService.findUpcomingCitasByMedicoId(medico.getIdMedico());
            long citasHoyCount = citaService.countTodayCitasByMedicoId(medico.getIdMedico());
            long citasProximasCount = citaService.countUpcomingCitasByMedicoId(medico.getIdMedico());

            model.addAttribute("citas", citas);
            model.addAttribute("citasHoyCount", citasHoyCount);
            model.addAttribute("citasProximasCount", citasProximasCount);
            model.addAttribute("medico", medico);
            return "portal/medico/dashboard";
        } else {
            return "redirect:/login?error=medico_not_found";
        }
    }

    @PostMapping("/citas/{id}/estado")
    public String actualizarEstadoCita(@PathVariable Long id,
            @RequestParam com.example.demo.model.EstadoCita estado,
            Authentication authentication) {
        Medico medico = obtenerMedicoAutenticado(authentication);
        citaService.actualizarEstadoCitaParaMedico(id, medico.getIdMedico(), estado);
        return "redirect:/medico/dashboard";
    }

    @GetMapping("/citas/{id}/atender")
    public String viewAtenderCita(@PathVariable Long id, Model model, Authentication authentication) {
        Cita cita = citaService.findById(id);
        if (cita == null) {
            return "redirect:/medico/dashboard?error=CitaNoEncontrada";
        }

        Medico medico = obtenerMedicoAutenticado(authentication);
        if (cita.getMedico() == null || !medico.getIdMedico().equals(cita.getMedico().getIdMedico())) {
            throw new AccessDeniedException("No tiene permisos para atender esta cita");
        }

        model.addAttribute("cita", cita);
        return "portal/medico/atender_cita";
    }

    @PostMapping("/citas/{id}/finalizar")
    public String finalizarCita(@PathVariable Long id,
            @RequestParam String diagnostico,
            @RequestParam String tratamiento,
            @RequestParam String observaciones,
            Authentication authentication) {
        Medico medico = obtenerMedicoAutenticado(authentication);
        citaService.finalizarCitaParaMedico(id, medico.getIdMedico(), diagnostico, tratamiento, observaciones);
        return "redirect:/medico/dashboard";
    }

    @GetMapping("/pacientes")
    public String viewMisPacientes(Model model, Authentication authentication) {
        String username = authentication.getName();
        Medico medico = medicoService.findByUsername(username);

        if (medico != null) {
            List<com.example.demo.model.Paciente> pacientes = citaService
                    .findPacientesByMedicoId(medico.getIdMedico());
            model.addAttribute("pacientes", pacientes);
        }
        return "portal/medico/pacientes";
    }

    @GetMapping("/pacientes/{id}/historial")
    public String viewHistorialPaciente(@org.springframework.web.bind.annotation.PathVariable Long id, Model model,
            Authentication authentication) {
        String username = authentication.getName();
        Medico medico = medicoService.findByUsername(username);

        if (medico != null) {
            List<Cita> historial = citaService.findCitasByMedicoAndPaciente(medico.getIdMedico(), id);
            model.addAttribute("historial", historial);
        }
        return "portal/medico/historial-fragment :: historial";
    }

    private Medico obtenerMedicoAutenticado(Authentication authentication) {
        String username = authentication.getName();
        Medico medico = medicoService.findByUsername(username);
        if (medico == null) {
            throw new AccessDeniedException("Médico autenticado no encontrado");
        }
        return medico;
    }
}
