package com.example.demo.config;

import com.example.demo.model.Rol;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        
        Usuario admin = usuarioRepository.findByUsername("admin")
                                         .orElse(new Usuario());
        
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123")); 
        admin.setRol(Rol.ROLE_ADMIN);
        
        usuarioRepository.save(admin);
    }
}