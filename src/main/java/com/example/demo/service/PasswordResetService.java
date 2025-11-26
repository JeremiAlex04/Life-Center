package com.example.demo.service;

import com.example.demo.model.PasswordResetToken;
import com.example.demo.model.Usuario;
import com.example.demo.repository.PasswordResetTokenRepository;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String createPasswordResetTokenForUser(Usuario user) {
        String token = UUID.randomUUID().toString();
        PasswordResetToken myToken = new PasswordResetToken();

        // Check if user already has a token and delete it
        tokenRepository.findByUsuario(user).ifPresent(tokenRepository::delete);

        myToken.setUsuario(user);
        myToken.setToken(token);
        myToken.setExpiryDate(LocalDateTime.now().plusHours(24)); // 24 hours expiry
        tokenRepository.save(myToken);
        return token;
    }

    public String validatePasswordResetToken(String token) {
        return tokenRepository.findByToken(token)
                .map(passToken -> {
                    if (passToken.isExpired()) {
                        return "expired";
                    }
                    return null;
                })
                .orElse("invalid");
    }

    public Usuario getUserByPasswordResetToken(String token) {
        return tokenRepository.findByToken(token).map(PasswordResetToken::getUsuario).orElse(null);
    }

    public void changeUserPassword(Usuario user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        usuarioRepository.save(user);
        tokenRepository.deleteByUsuario(user); // Invalidate token after use
    }
}
