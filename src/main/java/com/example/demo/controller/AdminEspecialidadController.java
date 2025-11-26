package com.example.demo.controller;

import com.example.demo.model.Especialidad;
import com.example.demo.model.Medico;
import com.example.demo.service.EspecialidadService;
import com.example.demo.service.MedicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/especialidades")
public class AdminEspecialidadController {

    @Autowired
    private EspecialidadService especialidadService;

    @Autowired
    private MedicoService medicoService;

    @GetMapping
    public String listarEspecialidades(Model model) {
        List<Especialidad> especialidades = especialidadService.findAll();
        model.addAttribute("especialidades", especialidades);
        return "admin/especialidades/listado";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("especialidad", new Especialidad());
        model.addAttribute("esNuevo", true);
        return "admin/especialidades/formulario";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Especialidad especialidad = especialidadService.findById(id);
        if (especialidad == null) {
            redirectAttributes.addFlashAttribute("error", "Especialidad no encontrada.");
            return "redirect:/admin/especialidades";
        }
        model.addAttribute("especialidad", especialidad);
        model.addAttribute("esNuevo", false);
        return "admin/especialidades/formulario";
    }

    @PostMapping("/guardar")
    public String guardarEspecialidad(@ModelAttribute Especialidad especialidad,
            RedirectAttributes redirectAttributes) {
        try {
            especialidadService.save(especialidad);
            redirectAttributes.addFlashAttribute("success",
                    especialidad.getIdEspecialidad() == null ? "Especialidad creada exitosamente."
                            : "Especialidad actualizada exitosamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/especialidades/nuevo";
        }
        return "redirect:/admin/especialidades";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarEspecialidad(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            especialidadService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Especialidad eliminada exitosamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/especialidades";
    }

    @GetMapping("/{id}/medicos")
    public String verMedicosPorEspecialidad(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Especialidad especialidad = especialidadService.findById(id);
        if (especialidad == null) {
            redirectAttributes.addFlashAttribute("error", "Especialidad no encontrada.");
            return "redirect:/admin/especialidades";
        }

        List<Medico> medicos = especialidad.getMedicos();
        model.addAttribute("especialidad", especialidad);
        model.addAttribute("medicos", medicos);
        return "admin/especialidades/medicos";
    }
}
