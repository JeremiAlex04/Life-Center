package com.example.demo.controller;

import com.example.demo.model.Paciente;
import com.example.demo.model.Rol;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult; // Importante
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute; // Importante
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/registro")
    public String viewRegistroPage(Model model) {
        if (!model.containsAttribute("paciente")) {
            model.addAttribute("paciente", new Paciente());
        }
        return "registro";
    }

    /**
     * CORREGIDO: Ahora usa @ModelAttribute para vincular el objeto Paciente completo.
     * El nombre de usuario se toma del DNI del paciente.
     */
    @PostMapping("/registro")
    public String registrarPaciente(@ModelAttribute("paciente") Paciente paciente,
                                    BindingResult bindingResult, // Para capturar errores de binding (ej. fecha)
                                    @RequestParam("password") String password,
                                    RedirectAttributes redirectAttributes) {

        // 1. Verificar si el DNI (que es el username) ya existe
        if (usuarioRepository.findByUsername(paciente.getDni()).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "El DNI ingresado ya está registrado como usuario.");
            redirectAttributes.addFlashAttribute("paciente", paciente); // Devuelve los datos para no perderlos
            return "redirect:/registro";
        }
        
        // 2. Crear y configurar el nuevo usuario
        Usuario usuario = new Usuario();
        usuario.setUsername(paciente.getDni()); // DNI como username
        usuario.setPassword(passwordEncoder.encode(password)); // Codificar la contraseña
        usuario.setRol(Rol.ROLE_PACIENTE);

        // 3. Asociar el usuario con el paciente
        paciente.setUsuario(usuario);
        
        // 4. Guardar el paciente (con todos sus datos) y el usuario en cascada
        pacienteService.save(paciente);
        
        redirectAttributes.addFlashAttribute("registro", "¡Registro exitoso! Ahora puedes iniciar sesión con tu DNI.");
        return "redirect:/login";
    }
}