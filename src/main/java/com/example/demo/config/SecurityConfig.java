package com.example.demo.config;

import com.example.demo.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/css/**", "/js/**", "/img/**", "/bootstrap-icons/**", "/login", "/registro",
                        "/especialidades", "/medicos", "/nosotros", "/contacto", "/forgot-password", "/reset-password")
                .permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/medico/**").hasRole("MEDICO")
                .requestMatchers("/paciente/**").hasRole("PACIENTE")
                .requestMatchers("/citas/**", "/api/horarios/**").hasRole("PACIENTE")
                .anyRequest().authenticated());

        http.formLogin(form -> form
                .loginPage("/login")
                .successHandler((request, response, authentication) -> {
                    String redirectUrl = "/";
                    var authorities = authentication.getAuthorities();
                    for (var grantedAuthority : authorities) {
                        if (grantedAuthority.getAuthority().equals("ROLE_ADMIN")) {
                            redirectUrl = "/admin/dashboard";
                            break;
                        } else if (grantedAuthority.getAuthority().equals("ROLE_MEDICO")) {
                            redirectUrl = "/medico/dashboard";
                            break;
                        } else if (grantedAuthority.getAuthority().equals("ROLE_PACIENTE")) {
                            redirectUrl = "/paciente/dashboard";
                            break;
                        }
                    }
                    response.sendRedirect(redirectUrl);
                })
                .permitAll());

        http.logout(logout -> logout.permitAll());

        return http.build();
    }
}
