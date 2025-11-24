package com.example.demo.controller;

import com.example.demo.model.Cita;
import com.example.demo.model.Paciente;
import com.example.demo.repository.MedicoRepository;
import com.example.demo.repository.PacienteRepository;
import com.example.demo.service.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/citas")
public class CitaController {

    @Autowired
    private CitaService citaService;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @GetMapping("/nueva")
    public String mostrarFormularioNuevaCita(Model model) {
        model.addAttribute("cita", new Cita());
        model.addAttribute("medicos", medicoRepository.findAll());
        return "portal/paciente/citas";
    }

    @PostMapping("/guardar")
    public String guardarCita(@ModelAttribute("cita") Cita cita, Authentication authentication, Model model) {
        // Obtenemos el username (DNI) del paciente logueado
        String username = authentication.getName();

        // Buscamos al paciente por su DNI
        Paciente paciente = pacienteRepository.findByDni(username)
                .orElseThrow(() -> new UsernameNotFoundException("Paciente no encontrado con DNI: " + username));

        // Asignamos el paciente a la cita
        cita.setPaciente(paciente);

        try {
            citaService.save(cita);
        } catch (IllegalArgumentException e) {
            // Si hay error de validación, volvemos al formulario con el mensaje de error
            model.addAttribute("error", e.getMessage());
            model.addAttribute("medicos", medicoRepository.findAll());
            return "portal/paciente/citas";
        }

        return "redirect:/paciente/dashboard";
    }
}