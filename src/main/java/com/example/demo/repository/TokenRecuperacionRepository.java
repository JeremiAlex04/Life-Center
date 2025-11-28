package com.example.demo.repository;

import com.example.demo.model.TokenRecuperacion;
import com.example.demo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TokenRecuperacionRepository extends JpaRepository<TokenRecuperacion, Long> {
    TokenRecuperacion findByToken(String token);

    TokenRecuperacion findByUsuario(Usuario usuario);

    @Transactional
    void deleteByUsuario(Usuario usuario);
}
