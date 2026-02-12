package com.example.demo.controller;

import com.example.demo.model.Paciente;
import com.example.demo.model.Rol;
import com.example.demo.model.Usuario;
import com.example.demo.service.PacienteService;
import com.example.demo.repository.UsuarioRepository; // Importar
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // Importar
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.SecureRandom;

@Controller
@RequestMapping("/admin/pacientes")
public class PacienteController {
    private static final String PASSWORD_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789@$!%*?&";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    // ------------------------------------

    @GetMapping
    public String listarPacientes(Model model) {
        model.addAttribute("pacientes", pacienteService.findAll());
        return "admin/pacientes/listado";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("paciente", new Paciente());
        return "admin/pacientes/formulario";
    }

    @PostMapping("/guardar")
    public String guardarPaciente(@ModelAttribute("paciente") Paciente paciente, RedirectAttributes redirectAttributes) {
        
        if (paciente.getIdPaciente() == null) {
            var usuarioExistente = usuarioRepository.findByUsername(paciente.getDni());
            
            if (usuarioExistente.isEmpty()) {
                // Si no existe, creamos un usuario por defecto.
                Usuario usuario = new Usuario();
                usuario.setUsername(paciente.getDni()); // DNI como username
                String tempPassword = generarPasswordSeguro(12);
                usuario.setPassword(passwordEncoder.encode(tempPassword));
                usuario.setRol(Rol.ROLE_PACIENTE);
                paciente.setUsuario(usuario);
                redirectAttributes.addFlashAttribute("success",
                        "Paciente creado. Contrasena temporal: " + tempPassword);
            } else {
                
                paciente.setUsuario(usuarioExistente.get());
            }

        } else {
            // Recuperamos el paciente de la BD para obtener su usuario
            Paciente pacienteExistente = pacienteService.findById(paciente.getIdPaciente())
                    .orElseThrow(() -> new IllegalArgumentException("Id de Paciente inválido:" + paciente.getIdPaciente()));
            paciente.setUsuario(pacienteExistente.getUsuario()); // Re-asociamos el usuario existente
        }
        
        pacienteService.save(paciente);
        return "redirect:/admin/pacientes";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Paciente paciente = pacienteService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Id de Paciente inválido:" + id));
        model.addAttribute("paciente", paciente);
        return "admin/pacientes/formulario";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarPaciente(@PathVariable Long id) {
        pacienteService.deleteById(id);
        return "redirect:/admin/pacientes";
    }

    private String generarPasswordSeguro(int length) {
        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int idx = SECURE_RANDOM.nextInt(PASSWORD_ALPHABET.length());
            password.append(PASSWORD_ALPHABET.charAt(idx));
        }
        return password.toString();
    }
}
