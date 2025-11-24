package com.example.demo.controller;

import com.example.demo.model.Cita;
import com.example.demo.model.EstadoCita;
import com.example.demo.service.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/citas")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCitaController {

    @Autowired
    private CitaService citaService;

    @GetMapping
    public String listarCitas(Model model) {
        List<Cita> citas = citaService.findAll();
        model.addAttribute("citas", citas);
        model.addAttribute("title", "Gestión de Citas");
        return "admin/citas/listado";
    }

    @GetMapping("/cancelar/{id}")
    public String cancelarCita(@PathVariable Long id) {
        citaService.actualizarEstadoCita(id, EstadoCita.CANCELADA);
        return "redirect:/admin/citas";
    }
}
