package com.example.demo.service;

import com.example.demo.model.TokenRecuperacion;
import com.example.demo.model.Usuario;
import com.example.demo.repository.TokenRecuperacionRepository;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ServicioRecuperacion {

    @Autowired
    private TokenRecuperacionRepository tokenRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public String crearTokenRecuperacion(Usuario usuario) {
        // Eliminar token existente si hay uno
        tokenRepository.deleteByUsuario(usuario);

        TokenRecuperacion token = new TokenRecuperacion(usuario);
        tokenRepository.save(token);
        return token.getToken();
    }

    public String validarToken(String token) {
        TokenRecuperacion passToken = tokenRepository.findByToken(token);
        if (passToken == null) {
            return "inválido";
        }
        if (passToken.estaExpirado()) {
            return "expirado";
        }
        return null; // Válido
    }

    public Usuario obtenerUsuarioPorToken(String token) {
        TokenRecuperacion passToken = tokenRepository.findByToken(token);
        return (passToken != null) ? passToken.getUsuario() : null;
    }

    @Transactional
    public void cambiarContrasena(Usuario usuario, String nuevaContrasena) {
        usuario.setPassword(passwordEncoder.encode(nuevaContrasena));
        usuarioRepository.save(usuario);
        tokenRepository.deleteByUsuario(usuario);
    }
}
