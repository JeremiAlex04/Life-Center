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

@Controller
@RequestMapping("/pacientes")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    // --- AÑADIDOS PARA MANEJAR USUARIOS ---
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
        return "admin/pacientes/formulario";
    }

    /**
     * CORREGIDO: Ahora maneja la lógica de Usuario al crear o editar.
     */
    @PostMapping("/guardar")
    public String guardarPaciente(@ModelAttribute("paciente") Paciente paciente) {
        
        if (paciente.getId_paciente() == null) {
            // --- Es un paciente NUEVO creado por el Admin ---
            var usuarioExistente = usuarioRepository.findByUsername(paciente.getDni());
            
            if (usuarioExistente.isEmpty()) {
                // Si no existe, creamos un usuario por defecto.
                Usuario usuario = new Usuario();
                usuario.setUsername(paciente.getDni()); // DNI como username
                usuario.setPassword(passwordEncoder.encode(paciente.getDni())); // DNI como password
                usuario.setRol(Rol.ROLE_PACIENTE);
                paciente.setUsuario(usuario);
            } else {
                // Si ya tenía una cuenta, la asociamos
                paciente.setUsuario(usuarioExistente.get());
            }

        } else {
            // --- Es un paciente EXISTENTE (EDICIÓN) ---
            // Recuperamos el paciente de la BD para obtener su usuario
            Paciente pacienteExistente = pacienteService.findById(paciente.getId_paciente())
                    .orElseThrow(() -> new IllegalArgumentException("Id de Paciente inválido:" + paciente.getId_paciente()));
            paciente.setUsuario(pacienteExistente.getUsuario()); // Re-asociamos el usuario existente
        }
        
        pacienteService.save(paciente);
        return "redirect:/pacientes";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Paciente paciente = pacienteService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Id de Paciente inválido:" + id));
        model.addAttribute("paciente", paciente);
        return "pacientes/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarPaciente(@PathVariable Long id) {
        pacienteService.deleteById(id);
        return "redirect:/pacientes";
    }
}