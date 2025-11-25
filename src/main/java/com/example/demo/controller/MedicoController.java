package com.example.demo.controller;

import com.example.demo.model.Medico;
import com.example.demo.model.Rol;
import com.example.demo.model.Usuario;
import com.example.demo.service.MedicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequestMapping("/admin/medicos")
public class MedicoController {

    @Autowired
    private MedicoService medicoService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private com.example.demo.repository.ConsultorioRepository consultorioRepository;

    @GetMapping
    public String listarMedicos(Model model) {
        model.addAttribute("medicos", medicoService.findAll());
        return "admin/medicos/listado";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("medico", new Medico());
        model.addAttribute("consultorios", consultorioRepository.findAll());
        return "admin/medicos/formulario";
    }

    @PostMapping("/guardar")
    public String guardarMedico(@ModelAttribute("medico") Medico medico,
            @RequestParam(value = "otraEspecialidad", required = false) String otraEspecialidad,
            @RequestParam(value = "consultorio", required = false) Long consultorioId) throws IOException {

        // Lógica para manejar "Otra" especialidad
        if ("Otra".equals(medico.getEspecialidad()) && otraEspecialidad != null && !otraEspecialidad.trim().isEmpty()) {
            medico.setEspecialidad(otraEspecialidad.trim());
        }

        // Asignar consultorio desde el ID
        if (consultorioId != null) {
            consultorioRepository.findById(consultorioId).ifPresent(medico::setConsultorio);
        }

        String fotoFileName = medico.getFotoUrl();

        if (medico.getIdMedico() == null) {
            // --- Es un médico NUEVO ---
            if (fotoFileName != null && !fotoFileName.trim().isEmpty()) {
                medico.setFotoUrl("/img/fotos-perfil/medicos/" + fotoFileName.trim());
            } else {
                medico.setFotoUrl(null);
            }

            Usuario usuario = new Usuario();
            usuario.setUsername(medico.getDni());
            usuario.setPassword(passwordEncoder.encode(medico.getDni()));
            usuario.setRol(Rol.ROLE_MEDICO);
            medico.setUsuario(usuario);
        } else {
            Medico medicoExistente = medicoService.findById(medico.getIdMedico())
                    .orElseThrow(() -> new IllegalArgumentException("Id de Médico inválido:" + medico.getIdMedico()));
            medico.setUsuario(medicoExistente.getUsuario());

            if (fotoFileName != null && !fotoFileName.trim().isEmpty()) {
                medico.setFotoUrl("/img/fotos-perfil/medicos/" + fotoFileName.trim());
            } else {
                medico.setFotoUrl(medicoExistente.getFotoUrl());
            }
        }

        medicoService.save(medico);
        return "redirect:/admin/medicos";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Medico medico = medicoService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Id de Médico inválido:" + id));

        if (medico.getFotoUrl() != null && !medico.getFotoUrl().isEmpty()) {
            String[] parts = medico.getFotoUrl().split("/");
            medico.setFotoUrl(parts[parts.length - 1]);
        }

        model.addAttribute("medico", medico);
        model.addAttribute("consultorios", consultorioRepository.findAll());
        return "admin/medicos/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarMedico(@PathVariable Long id) {
        medicoService.deleteById(id);
        return "redirect:/admin/medicos";
    }
}