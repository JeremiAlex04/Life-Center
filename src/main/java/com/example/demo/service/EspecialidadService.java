package com.example.demo.service;

import com.example.demo.model.Especialidad;
import com.example.demo.repository.EspecialidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EspecialidadService {

    @Autowired
    private EspecialidadRepository especialidadRepository;

    public List<Especialidad> findAll() {
        return especialidadRepository.findAllOrdenadas();
    }

    public List<Especialidad> findActivas() {
        // JOIN FETCH puede crear duplicados de Especialidad cuando hay múltiples
        // médicos
        // Usamos LinkedHashSet para eliminar duplicados manteniendo el orden
        List<Especialidad> especialidades = especialidadRepository.findByEstadoWithMedicos("ACTIVA");
        return new java.util.ArrayList<>(new java.util.LinkedHashSet<>(especialidades));
    }

    public Especialidad findById(Long id) {
        return especialidadRepository.findById(id).orElse(null);
    }

    public Especialidad save(Especialidad especialidad) {
        // Validar nombre único
        if (especialidad.getIdEspecialidad() == null) {
            Optional<Especialidad> existente = especialidadRepository.findByNombre(especialidad.getNombre());
            if (existente.isPresent()) {
                throw new IllegalArgumentException("Ya existe una especialidad con ese nombre.");
            }
        } else {
            Optional<Especialidad> existente = especialidadRepository.findByNombre(especialidad.getNombre());
            if (existente.isPresent()
                    && !existente.get().getIdEspecialidad().equals(especialidad.getIdEspecialidad())) {
                throw new IllegalArgumentException("Ya existe una especialidad con ese nombre.");
            }
        }

        return especialidadRepository.save(especialidad);
    }

    public void deleteById(Long id) {
        Especialidad especialidad = findById(id);
        if (especialidad != null) {
            long cantidadMedicos = especialidadRepository.contarMedicosPorEspecialidad(id);
            if (cantidadMedicos > 0) {
                throw new IllegalArgumentException(
                        "No se puede eliminar una especialidad que tiene médicos asignados.");
            }
            especialidadRepository.deleteById(id);
        }
    }

    public long contarMedicos(Long especialidadId) {
        return especialidadRepository.contarMedicosPorEspecialidad(especialidadId);
    }
}
