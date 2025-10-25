package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
public class Paciente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_paciente;

    private String dni;
    private String nombres;
    private String apellidos;
    private LocalDate fecha_nacimiento;
    private String genero;
    private String telefono;
    private String distrito; 
    private String direccion; 
    private String numero_seguro;

   
}