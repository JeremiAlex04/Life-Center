package com.example.demo.controller;

import com.example.demo.model.Paciente;
import com.example.demo.service.CitaService;
import com.example.demo.service.HistorialClinicoService;
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

    @Autowired
    private HistorialClinicoService historialClinicoService;

    /**
     * Maneja la solicitud GET para el dashboard del paciente.
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
     * Maneja la solicitud GET para el historial clínico del paciente.
     */
    @GetMapping("/historia_clinica")
    public String viewHistoriaClinica(Model model, Authentication authentication) {
        try {
            String username = authentication.getName();
            Paciente paciente = pacienteService.findByUsername(username);

            if (paciente != null) {
                model.addAttribute("nombrePaciente", paciente.getNombres());
                // Obtener citas completadas que actúan como historial
                java.util.List<com.example.demo.model.Cita> historial = citaService
                        .findByPacienteId(paciente.getIdPaciente())
                        .stream()
                        .filter(c -> c.getEstado() == com.example.demo.model.EstadoCita.COMPLETADA)
                        .collect(java.util.stream.Collectors.toList());

                model.addAttribute("historiales", historial);
            }

            return "portal/paciente/historia_clinica";
        } catch (Exception e) {
            try {
                java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter("error_log.txt", true));
                e.printStackTrace(pw);
                pw.close();
            } catch (java.io.IOException ioe) {
                ioe.printStackTrace();
            }
            throw e;
        }
    }

    @GetMapping("/citas/{id}/detalle")
    public String viewDetalleCita(@org.springframework.web.bind.annotation.PathVariable Long id, Model model,
            Authentication authentication) {
        String username = authentication.getName();
        Paciente paciente = pacienteService.findByUsername(username);
        com.example.demo.model.Cita cita = citaService.findById(id);

        if (cita == null || !cita.getPaciente().getIdPaciente().equals(paciente.getIdPaciente())) {
            return "redirect:/paciente/dashboard?error=AccesoDenegado";
        }

        model.addAttribute("cita", cita);
        model.addAttribute("paciente", paciente);
        model.addAttribute("medico", cita.getMedico());

        return "portal/paciente/ver_detalle_cita";
    }

    @GetMapping("/citas/{id}/detalle-fragment")
    public String viewDetalleCitaFragment(@org.springframework.web.bind.annotation.PathVariable Long id, Model model,
            Authentication authentication) {
        String username = authentication.getName();
        Paciente paciente = pacienteService.findByUsername(username);
        com.example.demo.model.Cita cita = citaService.findById(id);

        if (cita == null || !cita.getPaciente().getIdPaciente().equals(paciente.getIdPaciente())) {
            return "error/403";
        }

        model.addAttribute("cita", cita);
        model.addAttribute("paciente", paciente);
        model.addAttribute("medico", cita.getMedico());

        return "portal/paciente/ver_detalle_cita :: detalle";
    }
}
