package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppController {

    /**
     * Mapea la raíz del sitio ("/") para mostrar la página pública principal.
     */
    @GetMapping("/")
    public String viewHomePage() {
        return "index"; // Muestra templates/index.html
    }

    /**
     * Muestra el formulario de inicio de sesión.
     */
    @GetMapping("/login")
    public String viewLoginPage() {
        return "login";
    }
}