package com.example.demo.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        String redirectUrl = "/login?error=true"; // URL por defecto

        for (GrantedAuthority auth : authentication.getAuthorities()) {
            if ("ROLE_ADMIN".equals(auth.getAuthority())) {
                redirectUrl = "/admin/dashboard"; // Redirección estricta para ADMIN
                break;
            } else if ("ROLE_MEDICO".equals(auth.getAuthority())) {
                redirectUrl = "/medico/dashboard";
                break;
            } else if ("ROLE_PACIENTE".equals(auth.getAuthority())) {
                redirectUrl = "/paciente/dashboard";
                break;
            }
        }
        response.sendRedirect(redirectUrl);
    }
}