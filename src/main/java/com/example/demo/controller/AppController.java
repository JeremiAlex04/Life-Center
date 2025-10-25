package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AppController {

    @GetMapping("/")
    public String viewLoginPage() {
        return "index";
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String usuario, @RequestParam("password") String contraseña, Model model) {
        if ("lifecenter".equals(usuario) && "admin123".equals(contraseña)) {
            return "redirect:/dashboard";
        } else {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
            return "index";
        }
    }

    @GetMapping("/dashboard")
    public String viewDashboard() {
        return "dashboard";
    }
}
