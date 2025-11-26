package com.example.demo.controller;

import com.example.demo.repository.MedicoRepository;
import com.example.demo.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class DashboardController {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private com.example.demo.repository.ConsultorioRepository consultorioRepository;

    @GetMapping("/dashboard")
    public String viewAdminDashboard(Model model) {
        long totalPacientes = pacienteRepository.count();
        long totalMedicos = medicoRepository.count();
        long totalConsultorios = consultorioRepository.count();

        model.addAttribute("totalPacientes", totalPacientes);
        model.addAttribute("totalMedicos", totalMedicos);
        model.addAttribute("totalConsultorios", totalConsultorios);

        model.addAttribute("totalCitas", 0);
        model.addAttribute("totalHabitaciones", 0);

        return "admin/dashboard";
    }
}