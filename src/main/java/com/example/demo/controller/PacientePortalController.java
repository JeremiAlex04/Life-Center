package com.example.demo.controller;

import com.example.demo.model.Paciente;

import com.example.demo.service.CitaService;

import com.example.demo.service.HistorialClinicoService; // Importar HistorialClinicoService

import com.example.demo.service.PacienteService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.Authentication;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;

@Controller

@RequestMapping("/paciente")

public class PacientePortalController {

    @Autowired

    private PacienteService pacienteService;

    @Autowired

    private CitaService citaService;

    @Autowired // Inyectar HistorialClinicoService

    private HistorialClinicoService historialClinicoService;

    /**
     * 
     * Maneja la solicitud GET para el dashboard del paciente.
     * 
     * Esta es la página a la que son redirigidos después de iniciar sesión.
     * 
     */

    @GetMapping("/dashboard")

    public String viewPacienteDashboard(Model model, Authentication authentication) {

        String username = authentication.getName();

        Paciente paciente = pacienteService.findByUsername(username);

        if (paciente != null) {

            model.addAttribute("nombrePaciente", paciente.getNombres());

            model.addAttribute("citas", citaService.findByPacienteId(paciente.getIdPaciente()));

        }

        return "portal/paciente/dashboard";

    }

    /**
     * 
     * Maneja la solicitud GET para el historial clínico del paciente.
     * 
     */

    @GetMapping("/historia_clinica")

    public String viewHistoriaClinica(Model model, Authentication authentication) {

        String username = authentication.getName();

        Paciente paciente = pacienteService.findByUsername(username);

        if (paciente != null) {

            model.addAttribute("nombrePaciente", paciente.getNombres());

            model.addAttribute("historiales", historialClinicoService.findByPaciente(paciente)); // Añadir historiales
                                                                                                 // al modelo

        }

        return "portal/paciente/historia_clinica";

    }

}
