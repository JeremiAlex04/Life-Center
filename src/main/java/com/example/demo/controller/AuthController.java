package com.example.demo.controller;

import com.example.demo.model.Paciente;
import com.example.demo.model.Rol;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.PacienteService;
import com.example.demo.service.ServicioRecuperacion;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class AuthController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);
    private static final String RESET_TOKEN_SESSION_KEY = "PASSWORD_RESET_TOKEN";

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ServicioRecuperacion servicioRecuperacion;

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
        if (!StringUtils.hasText(password) || password.length() < 8) {
            redirectAttributes.addFlashAttribute("error", "La contrasena debe tener al menos 8 caracteres.");
            redirectAttributes.addFlashAttribute("paciente", paciente);
            return "redirect:/registro";
        }

        if (usuarioRepository.findByUsername(paciente.getDni()).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "El DNI ingresado ya esta registrado como usuario.");
            redirectAttributes.addFlashAttribute("paciente", paciente);
            return "redirect:/registro";
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(paciente.getDni());
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setRol(Rol.ROLE_PACIENTE);
        paciente.setUsuario(usuario);
        pacienteService.save(paciente);

        redirectAttributes.addFlashAttribute("registro", "Registro exitoso. Ahora puedes iniciar sesion con tu DNI.");
        return "redirect:/login";
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot_password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("dni") String dni,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByUsername(dni);

        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            String token = servicioRecuperacion.crearTokenRecuperacion(usuario);
            session.setAttribute(RESET_TOKEN_SESSION_KEY, token);
            LOGGER.info("Se genero un token de recuperacion para una cuenta valida.");
            return "redirect:/reset-password";
        }

        redirectAttributes.addFlashAttribute("message",
                "Si el DNI existe, se ha generado un enlace de recuperacion.");
        return "redirect:/forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam(value = "token", required = false) String token,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        String tokenEfectivo = token;
        if (!StringUtils.hasText(tokenEfectivo)) {
            tokenEfectivo = (String) session.getAttribute(RESET_TOKEN_SESSION_KEY);
        }

        if (!StringUtils.hasText(tokenEfectivo)) {
            redirectAttributes.addFlashAttribute("error", "Solicitud de recuperacion invalida.");
            return "redirect:/forgot-password";
        }

        String resultado = servicioRecuperacion.validarToken(tokenEfectivo);
        if (resultado != null) {
            redirectAttributes.addFlashAttribute("error", "El enlace de recuperacion es " + resultado + ".");
            return "redirect:/login";
        }

        session.setAttribute(RESET_TOKEN_SESSION_KEY, tokenEfectivo);
        model.addAttribute("token", tokenEfectivo);
        return "reset_password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
            @RequestParam("password") String password,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        if (!StringUtils.hasText(password) || password.length() < 8) {
            redirectAttributes.addFlashAttribute("error", "La contrasena debe tener al menos 8 caracteres.");
            return "redirect:/reset-password";
        }

        String resultado = servicioRecuperacion.validarToken(token);
        if (resultado != null) {
            redirectAttributes.addFlashAttribute("error", "El enlace de recuperacion es " + resultado + ".");
            return "redirect:/login";
        }

        Usuario usuario = servicioRecuperacion.obtenerUsuarioPorToken(token);
        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error", "Error al restablecer la contrasena.");
            return "redirect:/login";
        }

        servicioRecuperacion.cambiarContrasena(usuario, password);
        session.removeAttribute(RESET_TOKEN_SESSION_KEY);
        redirectAttributes.addFlashAttribute("registro", "Contrasena restablecida exitosamente.");
        return "redirect:/login";
    }
}
