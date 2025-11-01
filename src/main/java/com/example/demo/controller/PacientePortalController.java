package com.example.demo.controller;

import com.example.demo.model.Paciente;
import com.example.demo.service.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/paciente") // Mapeo base para el rol de paciente
public class PacientePortalController {

    @Autowired
    private PacienteService pacienteService;

    /**
     * Maneja la solicitud GET para el dashboard del paciente.
     * Esta es la página a la que son redirigidos después de iniciar sesión.
     */
    @GetMapping("/dashboard")
    public String viewPacienteDashboard(Model model, Authentication authentication) {
        String username = authentication.getName();
        Paciente paciente = pacienteService.findByUsername(username);

        if (paciente != null) {
            model.addAttribute("nombrePaciente", paciente.getNombres());
        }
        return "portal/paciente/dashboard"; // Busca el archivo en /templates/portal/paciente/dashboard.html
    }
}