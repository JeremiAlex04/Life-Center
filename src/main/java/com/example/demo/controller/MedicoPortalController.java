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
@RequestMapping("/medico") // Mapeo base para el rol de médico
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
            List<Cita> citasHoy = citaService.findTodayCitasByMedicoId(medico.getId_medico());
            model.addAttribute("citasHoy", citasHoy);
            model.addAttribute("nombreMedico", medico.getNombres());
        }

        return "portal/medico/dashboard"; 
    }
}