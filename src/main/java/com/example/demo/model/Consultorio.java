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

    @Transient
    private long doctorCount;
}
