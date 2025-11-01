package com.example.demo.controller;

import com.example.demo.repository.MedicoRepository;
import com.example.demo.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin") // Define la ruta base "/admin"
public class DashboardController {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    /**
     * Este método maneja la URL "/admin/dashboard" a la que eres redirigido.
     */
    @GetMapping("/dashboard")
    public String viewAdminDashboard(Model model) {
        long totalPacientes = pacienteRepository.count();
        long totalMedicos = medicoRepository.count();

        model.addAttribute("totalPacientes", totalPacientes);
        model.addAttribute("totalMedicos", totalMedicos);
        
        // Valores de ejemplo como antes
        model.addAttribute("totalCitas", 0); 
        model.addAttribute("totalHabitaciones", 0);

        // Devuelve la plantilla que SÍ existe
        return "admin/dashboard"; 
    }
}