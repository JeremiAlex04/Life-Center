package com.example.demo.model;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.Data;
import java.time.LocalDate;

@NoArgsConstructor
@Data
@Entity
public class Cita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCita;

    private LocalDate fecha;
    private String motivo;

    @ManyToOne
    @JoinColumn(name = "id_paciente")
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "id_medico")
    private Medico medico;

    private java.time.LocalTime hora;

    private Integer duracionMinutos = 30; // Duración estimada de la cita en minutos

    @Enumerated(EnumType.STRING)
    private EstadoCita estado = EstadoCita.PENDIENTE;

    @Column(columnDefinition = "TEXT")
    private String diagnostico;

    @Column(columnDefinition = "TEXT")
    private String tratamiento;

    @Column(columnDefinition = "TEXT")
    private String observaciones;
}