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
    private MedicoRepository medicoRepository; // Para listar médicos

    @Autowired
    private PacienteRepository pacienteRepository; // Para encontrar al paciente logueado

    @GetMapping("/nueva")
    public String mostrarFormularioNuevaCita(Model model) {
        model.addAttribute("cita", new Cita());
        model.addAttribute("medicos", medicoRepository.findAll()); // Pasamos la lista de médicos al formulario
        return "portal/paciente/citas";
    }

    @PostMapping("/guardar")
    public String guardarCita(@ModelAttribute("cita") Cita cita, Authentication authentication) {
        // Obtenemos el username (DNI) del paciente logueado
        String username = authentication.getName();
        
        // Buscamos al paciente por su DNI
        Paciente paciente = pacienteRepository.findByDni(username)
                .orElseThrow(() -> new UsernameNotFoundException("Paciente no encontrado con DNI: " + username));
        
        // Asignamos el paciente a la cita
        cita.setPaciente(paciente);
        
        citaService.save(cita);
        return "redirect:/paciente/dashboard"; // Redirigir al dashboard del paciente
    }
}