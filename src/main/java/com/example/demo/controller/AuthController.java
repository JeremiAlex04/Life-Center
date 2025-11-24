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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute; 
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
            redirectAttributes.addFlashAttribute("error", "El DNI ingresado ya está registrado como usuario.");
            redirectAttributes.addFlashAttribute("paciente", paciente); 
            return "redirect:/registro";
        }
        
        Usuario usuario = new Usuario();
        usuario.setUsername(paciente.getDni()); 
        usuario.setPassword(passwordEncoder.encode(password)); 
        usuario.setRol(Rol.ROLE_PACIENTE);

        paciente.setUsuario(usuario);
        
        pacienteService.save(paciente);
        
        redirectAttributes.addFlashAttribute("registro", "¡Registro exitoso! Ahora puedes iniciar sesión con tu DNI.");
        return "redirect:/login";
    }
}