package com.example.demo.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "especialidad")
public class Especialidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_especialidad")
    private Long idEspecialidad;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @Column(name = "url_imagen", length = 255)
    private String urlImagen;

    @Column(nullable = false, length = 20)
    private String estado = "ACTIVA";

    @OneToMany(mappedBy = "especialidadObj")
    private List<Medico> medicos;

    // Constructors
    public Especialidad() {
    }

    public Especialidad(String nombre, String descripcion, String urlImagen) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.urlImagen = urlImagen;
        this.estado = "ACTIVA";
    }

    // Getters and Setters
    public Long getIdEspecialidad() {
        return idEspecialidad;
    }

    public void setIdEspecialidad(Long idEspecialidad) {
        this.idEspecialidad = idEspecialidad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<Medico> getMedicos() {
        return medicos;
    }

    public void setMedicos(List<Medico> medicos) {
        this.medicos = medicos;
    }

    // Helper method
    public int getCantidadMedicos() {
        return medicos != null ? medicos.size() : 0;
    }
}
