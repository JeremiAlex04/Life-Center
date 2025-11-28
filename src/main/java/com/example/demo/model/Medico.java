package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Medico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMedico;

    private String dni;
    private String nombres;
    private String apellidos;

    // Mantener temporalmente para compatibilidad durante migración
    private String especialidad;

    private String numeroColegiatura;
    private String telefono;
    private String email;
    private String estado;
    private String turno;
    private Integer anioEgreso;
    private String fotoUrl;

    @Column(columnDefinition = "integer default 30")
    private Integer duracionCita = 30;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_consultorio", nullable = false)
    private Consultorio consultorio;

    @ManyToOne
    @JoinColumn(name = "id_especialidad")
    private Especialidad especialidadObj;

    // Helper method para obtener nombre de especialidad
    public String getEspecialidadNombre() {
        if (especialidadObj != null) {
            return especialidadObj.getNombre();
        }
        return especialidad;
    }
}