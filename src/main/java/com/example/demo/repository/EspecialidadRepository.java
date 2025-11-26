package com.example.demo.repository;

import com.example.demo.model.Especialidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EspecialidadRepository extends JpaRepository<Especialidad, Long> {

    Optional<Especialidad> findByNombre(String nombre);

    List<Especialidad> findByEstado(String estado);

    @Query("SELECT e FROM Especialidad e ORDER BY e.nombre ASC")
    List<Especialidad> findAllOrdenadas();

    @Query("SELECT COUNT(m) FROM Medico m WHERE m.especialidadObj.idEspecialidad = :especialidadId")
    long contarMedicosPorEspecialidad(@Param("especialidadId") Long especialidadId);
}
