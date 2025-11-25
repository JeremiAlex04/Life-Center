package com.example.demo.controller;

import com.example.demo.model.Cita;
import com.example.demo.model.Medico;
import com.example.demo.service.CitaService;
import com.example.demo.service.MedicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
        System.out.println("Attempting to find medico with username: " + username); // For debugging
        Medico medico = medicoService.findByUsername(username);

        if (medico != null) {
            System.out.println("Medico found: " + medico.getNombres()); // For debugging
            List<Cita> citas = citaService.findUpcomingCitasByMedicoId(medico.getIdMedico());
            long citasHoyCount = citaService.countTodayCitasByMedicoId(medico.getIdMedico());
            long citasProximasCount = citaService.countUpcomingCitasByMedicoId(medico.getIdMedico());

            model.addAttribute("citas", citas);
            model.addAttribute("citasHoyCount", citasHoyCount);
            model.addAttribute("citasProximasCount", citasProximasCount);
            model.addAttribute("medico", medico);
            return "portal/medico/dashboard";
        } else {
            System.out.println("Medico not found for username: " + username); // For debugging
            return "redirect:/login?error=medico_not_found";
        }
    }

    @GetMapping("/citas/{id}/cambiar-estado")
    public String actualizarEstadoCita(@org.springframework.web.bind.annotation.PathVariable Long id,
            @org.springframework.web.bind.annotation.RequestParam com.example.demo.model.EstadoCita estado) {
        citaService.actualizarEstadoCita(id, estado);
        return "redirect:/medico/dashboard";
    }

    @GetMapping("/citas/{id}/atender")
    public String viewAtenderCita(@org.springframework.web.bind.annotation.PathVariable Long id, Model model) {
        Cita cita = citaService.findById(id);
        if (cita == null) {
            return "redirect:/medico/dashboard?error=CitaNoEncontrada";
        }
        model.addAttribute("cita", cita);
        return "portal/medico/atender_cita";
    }

    @org.springframework.web.bind.annotation.PostMapping("/citas/{id}/finalizar")
    public String finalizarCita(@org.springframework.web.bind.annotation.PathVariable Long id,
            @org.springframework.web.bind.annotation.RequestParam String diagnostico,
            @org.springframework.web.bind.annotation.RequestParam String tratamiento,
            @org.springframework.web.bind.annotation.RequestParam String observaciones) {
        citaService.finalizarCita(id, diagnostico, tratamiento, observaciones);
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
}