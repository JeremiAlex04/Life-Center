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

    /**
     * CORREGIDO: Este método ahora se asegura de que el usuario 'admin'
     * exista y tenga los permisos correctos CADA VEZ que la aplicación inicia.
     * Si el usuario 'admin' ya existe pero con datos incorrectos, los corregirá.
     */
    @Override
    public void run(String... args) throws Exception {
        
        // 1. Busca el usuario "admin". Si no lo encuentra, crea un objeto Usuario nuevo.
        Usuario admin = usuarioRepository.findByUsername("admin")
                                         .orElse(new Usuario());
        
        // 2. Establece (o re-establece) las propiedades correctas.
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123")); // Asegura la contraseña
        admin.setRol(Rol.ROLE_ADMIN); // Asegura el ROL DE ADMINISTRADOR
        
        // 3. Guarda el usuario (sea nuevo o actualizado)
        usuarioRepository.save(admin);
    }
}