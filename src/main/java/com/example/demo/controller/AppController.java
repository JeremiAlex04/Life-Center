package com.example.demo.controller;

import com.example.demo.repository.MedicoRepository;
import com.example.demo.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AppController {

    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;

    @Autowired
    public AppController(PacienteRepository pacienteRepository, MedicoRepository medicoRepository) {
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
    }

    @GetMapping("/")
    public String viewLoginPage() {
        return "index";
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String usuario, @RequestParam("password") String contraseña, Model model) {
        if ("lifecenter".equals(usuario) && "admin123".equals(contraseña)) {
            return "redirect:/dashboard";
        } else {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
            return "index";
        }
    }

    @GetMapping("/dashboard")
    public String viewDashboard(Model model) {

        long totalPacientes = pacienteRepository.count(); 
        long totalMedicos = medicoRepository.count();     
        
        long totalCitas = 0;
        long totalHabitaciones = 0;

        model.addAttribute("totalPacientes", totalPacientes);
        model.addAttribute("totalMedicos", totalMedicos);
        model.addAttribute("totalCitas", totalCitas);
        model.addAttribute("totalHabitaciones", totalHabitaciones);

        return "dashboard";
    }
}
