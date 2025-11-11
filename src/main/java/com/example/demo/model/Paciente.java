package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
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
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fecha_nacimiento;
    
    private String genero;
    private String telefono;
    private String distrito;
    private String direccion;
    private String numero_seguro;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;
}