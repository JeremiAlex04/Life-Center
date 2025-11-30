package com.example.demo.controller;

import com.example.demo.exception.ConflictoHorarioException;
import com.example.demo.exception.ConsultorioNoDisponibleException;
import com.example.demo.exception.IncompatibilidadEspecialidadException;
import com.example.demo.model.Medico;
import com.example.demo.model.Rol;
import com.example.demo.model.Usuario;
import com.example.demo.service.MedicoService;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @Autowired
    private com.example.demo.repository.EspecialidadRepository especialidadRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public String listarMedicos(Model model) {
        model.addAttribute("medicos", medicoService.findAll());
        return "admin/medicos/listado";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("medico", new Medico());
        model.addAttribute("consultorios", consultorioRepository.findAll());
        model.addAttribute("especialidades", especialidadRepository.findAll());
        return "admin/medicos/formulario";
    }

    @PostMapping("/guardar")
    public String guardarMedico(@ModelAttribute("medico") Medico medico,
            @RequestParam(value = "otraEspecialidad", required = false) String otraEspecialidad,
            @RequestParam(value = "consultorio", required = false) Long consultorioId,
            RedirectAttributes redirectAttributes) throws IOException {

        // Lógica para manejar "Otra" especialidad
        if ("Otra".equals(medico.getEspecialidad()) && otraEspecialidad != null && !otraEspecialidad.trim().isEmpty()) {
            medico.setEspecialidad(otraEspecialidad.trim());
        }

        // Asignar consultorio desde el ID (sin sincronizar tipo automáticamente)
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

            // Verificar si el usuario ya existe
            if (usuarioRepository.findByUsername(medico.getDni()).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "El DNI ya está registrado como usuario.");
                return "redirect:/admin/medicos/nuevo";
            }

            Usuario usuario = new Usuario();
            usuario.setUsername(medico.getDni());

            // Generar contraseña: Inicial Nombre + Inicial Apellido + Año Egreso
            String rawPassword = medico.getDni(); // Default fallback
            if (medico.getNombres() != null && !medico.getNombres().isEmpty() &&
                    medico.getApellidos() != null && !medico.getApellidos().isEmpty() &&
                    medico.getAnioEgreso() != null) {
                rawPassword = (medico.getNombres().substring(0, 1) + medico.getApellidos().substring(0, 1))
                        .toLowerCase()
                        + medico.getAnioEgreso();
            }

            usuario.setPassword(passwordEncoder.encode(rawPassword));
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

        // Intentar guardar con validaciones
        try {
            medicoService.save(medico);
            redirectAttributes.addFlashAttribute("success", "Médico guardado exitosamente");
            return "redirect:/admin/medicos";
        } catch (ConflictoHorarioException | ConsultorioNoDisponibleException
                | IncompatibilidadEspecialidadException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            if (medico.getIdMedico() == null) {
                return "redirect:/admin/medicos/nuevo";
            } else {
                return "redirect:/admin/medicos/editar/" + medico.getIdMedico();
            }
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Medico medico = medicoService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Id de Médico inválido:" + id));

        if (medico.getFotoUrl() != null && !medico.getFotoUrl().isEmpty()) {
            String[] parts = medico.getFotoUrl().split("/");
            medico.setFotoUrl(parts[parts.length - 1]);
        }

        // Asegurar que el campo String especialidad esté sincronizado con el objeto
        // para el select del formulario
        if (medico.getEspecialidadObj() != null) {
            medico.setEspecialidad(medico.getEspecialidadObj().getNombre());
        }

        model.addAttribute("medico", medico);
        model.addAttribute("consultorios", consultorioRepository.findAll());
        model.addAttribute("especialidades", especialidadRepository.findAll());
        return "admin/medicos/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarMedico(@PathVariable Long id) {
        medicoService.deleteById(id);
        return "redirect:/admin/medicos";
    }
}