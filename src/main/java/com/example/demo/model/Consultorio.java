package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Consultorio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idConsultorio;

    private String numero;
    private String piso;
    private String estado; // DISPONIBLE, MANTENIMIENTO, LIMPIEZA, FUERA_SERVICIO
    private String tipo; // GENERAL, DENTAL, GINECOLOGIA, PEDIATRIA

    @Transient
    private long doctorCount;

    @Transient
    private java.util.List<String> doctorNames;
}
