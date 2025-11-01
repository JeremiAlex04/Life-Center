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

@Controller
@RequestMapping("/medicos")
public class MedicoController {

    @Autowired
    private MedicoService medicoService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String listarMedicos(Model model) {
        model.addAttribute("medicos", medicoService.findAll());
        return "admin/medicos/listado";
    }
    
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        return "admin/medicos/formulario";
    }

    /**
     * CORREGIDO: Ahora maneja la lógica de Usuario al crear o editar.
     */
    @PostMapping("/guardar")
    public String guardarMedico(@ModelAttribute("medico") Medico medico) {
        
        if (medico.getId_medico() == null) {
            // --- Es un médico NUEVO ---
            Usuario usuario = new Usuario();
            usuario.setUsername(medico.getDni()); // DNI como username por defecto
            usuario.setPassword(passwordEncoder.encode(medico.getDni())); // DNI como password por defecto
            usuario.setRol(Rol.ROLE_MEDICO);
            medico.setUsuario(usuario);
        } else {
            // --- Es un médico EXISTENTE (EDICIÓN) ---
            // Recuperamos el médico de la BD para obtener su usuario
            Medico medicoExistente = medicoService.findById(medico.getId_medico())
                    .orElseThrow(() -> new IllegalArgumentException("Id de Médico inválido:" + medico.getId_medico()));
            medico.setUsuario(medicoExistente.getUsuario()); // Re-asociamos el usuario existente
        }
        
        medicoService.save(medico);
        return "redirect:/medicos";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Medico medico = medicoService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Id de Médico inválido:" + id));
        model.addAttribute("medico", medico);
        return "admin/medicos/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarMedico(@PathVariable Long id) {
        medicoService.deleteById(id);
        return "redirect:/medicos";
    }
}