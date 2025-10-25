package com.example.demo.controller;

import com.example.demo.model.Medico;
import com.example.demo.service.MedicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/medicos")
public class MedicoController {

    @Autowired
    private MedicoService medicoService;

    @GetMapping
    public String listarMedicos(Model model) {
        model.addAttribute("medicos", medicoService.findAll());
        return "medicos/listado";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("medico", new Medico());
        return "medicos/formulario";
    }

    @PostMapping("/guardar")
    public String guardarMedico(@ModelAttribute Medico medico,
            @RequestParam(name = "otraEspecialidad", required = false) String otraEspecialidad,
            Model model) {

        if ("Otra".equals(medico.getEspecialidad()) && (otraEspecialidad == null || otraEspecialidad.trim().isEmpty())) {
            model.addAttribute("error", "Debe especificar la especialidad si selecciona 'Otra'.");
            return "medicos/formulario";
        }

        if ("Otra".equals(medico.getEspecialidad())) {
            medico.setEspecialidad(otraEspecialidad);
        }

        medicoService.save(medico);
        return "redirect:/medicos";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Medico medico = medicoService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de Médico inválido:" + id));
        model.addAttribute("medico", medico);
        return "medicos/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarMedico(@PathVariable Long id) {
        medicoService.deleteById(id);
        return "redirect:/medicos";
    }
}
