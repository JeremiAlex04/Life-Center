package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
}