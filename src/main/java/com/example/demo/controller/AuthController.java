package com.example.demo.controller;

import com.example.demo.model.Paciente;
import com.example.demo.model.Rol;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.PacienteService;
import com.example.demo.service.ServicioRecuperacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ServicioRecuperacion servicioRecuperacion;

    // ------------------- Login & Registro -------------------
    @GetMapping("/login")
    public String viewLoginPage() {
        return "login";
    }

    @GetMapping("/registro")
    public String viewRegistroPage(Model model) {
        if (!model.containsAttribute("paciente")) {
            model.addAttribute("paciente", new Paciente());
        }
        return "registro";
    }

    @PostMapping("/registro")
    public String registrarPaciente(@ModelAttribute("paciente") Paciente paciente,
            BindingResult bindingResult,
            @RequestParam("password") String password,
            RedirectAttributes redirectAttributes) {
        if (usuarioRepository.findByUsername(paciente.getDni()).isPresent()) {
            redirectAttributes.addFlashAttribute("error",
                    "El DNI ingresado ya está registrado como usuario.");
            redirectAttributes.addFlashAttribute("paciente", paciente);
            return "redirect:/registro";
        }
        Usuario usuario = new Usuario();
        usuario.setUsername(paciente.getDni());
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setRol(Rol.ROLE_PACIENTE);
        paciente.setUsuario(usuario);
        pacienteService.save(paciente);
        redirectAttributes.addFlashAttribute("registro",
                "¡Registro exitoso! Ahora puedes iniciar sesión con tu DNI.");
        return "redirect:/login";
    }

    // ------------------- Password Recovery -------------------
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot_password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("dni") String dni,
            RedirectAttributes redirectAttributes) {
        System.out.println("[AuthController] Received DNI for password recovery: " + dni);
        Optional<Usuario> usuarioOptional = usuarioRepository.findByUsername(dni);
        if (!usuarioOptional.isPresent()) {
            System.out.println("[AuthController] No user found for DNI: " + dni);
            redirectAttributes.addFlashAttribute("error",
                    "El DNI ingresado no fue encontrado. Por favor, verifique el DNI e intente de nuevo.");
            return "redirect:/forgot-password";
        }
        Usuario usuario = usuarioOptional.get();
        System.out.println("[AuthController] User found: " + usuario.getUsername());
        String token = servicioRecuperacion.crearTokenRecuperacion(usuario);
        System.out.println("[AuthController] Generated token: " + token);
        return "redirect:/reset-password?token=" + token;
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token,
            Model model,
            RedirectAttributes redirectAttributes) {
        System.out.println("[AuthController] Showing reset form for token: " + token);
        String resultado = servicioRecuperacion.validarToken(token);
        if (resultado != null) {
            System.out.println("[AuthController] Token validation failed: " + resultado);
            redirectAttributes.addFlashAttribute("error",
                    "El enlace de recuperación es " + resultado + ".");
            return "redirect:/login";
        }
        model.addAttribute("token", token);
        return "reset_password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
            @RequestParam("password") String password,
            RedirectAttributes redirectAttributes) {
        System.out.println("[AuthController] Processing password reset for token: " + token);
        String resultado = servicioRecuperacion.validarToken(token);
        if (resultado != null) {
            System.out.println("[AuthController] Token validation failed on reset: " + resultado);
            redirectAttributes.addFlashAttribute("error",
                    "El enlace de recuperación es " + resultado + ".");
            return "redirect:/login";
        }
        Usuario usuario = servicioRecuperacion.obtenerUsuarioPorToken(token);
        if (usuario != null) {
            servicioRecuperacion.cambiarContrasena(usuario, password);
            redirectAttributes.addFlashAttribute("registro",
                    "Contraseña restablecida exitosamente.");
            return "redirect:/login";
        } else {
            redirectAttributes.addFlashAttribute("error",
                    "Error al restablecer la contraseña.");
            return "redirect:/login";
        }
    }
}