package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Medico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_medico;

    private String dni;
    private String nombres;
    private String apellidos;
    private String especialidad;
    private String numero_colegiatura;
    private String telefono;
    private String email;
    private String estado;
    private String turno;
    private Integer anioEgreso;
    private String fotoUrl;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;
}