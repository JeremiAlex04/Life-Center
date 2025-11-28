package com.example.demo.config;

import com.example.demo.model.Especialidad;
import com.example.demo.model.Medico;
import com.example.demo.repository.EspecialidadRepository;
import com.example.demo.repository.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Sincroniza los campos especialidad (String) y especialidadObj (Especialidad)
 * para corregir inconsistencias en los datos existentes.
 */
@Component
@Order(2) // Se ejecuta después de DataInitializer
public class DataSyncFix implements CommandLineRunner {

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private EspecialidadRepository especialidadRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== Iniciando sincronización de especialidades ===");

        List<Medico> medicos = medicoRepository.findAll();
        int sincronizados = 0;

        for (Medico medico : medicos) {
            boolean cambio = false;

            // Si tiene especialidadObj, sincronizar el campo String
            if (medico.getEspecialidadObj() != null) {
                String nombreEspecialidad = medico.getEspecialidadObj().getNombre();
                if (!nombreEspecialidad.equals(medico.getEspecialidad())) {
                    System.out.println("Sincronizando médico " + medico.getNombres() + " " + medico.getApellidos() +
                            ": '" + medico.getEspecialidad() + "' -> '" + nombreEspecialidad + "'");
                    medico.setEspecialidad(nombreEspecialidad);
                    cambio = true;
                }
            }
            // Si solo tiene el campo String, buscar y asignar especialidadObj
            else if (medico.getEspecialidad() != null && !medico.getEspecialidad().isEmpty()) {
                Optional<Especialidad> especialidadOpt = buscarEspecialidad(medico.getEspecialidad());
                if (especialidadOpt.isPresent()) {
                    medico.setEspecialidadObj(especialidadOpt.get());
                    medico.setEspecialidad(especialidadOpt.get().getNombre());
                    System.out.println(
                            "Asignando especialidadObj a médico " + medico.getNombres() + " " + medico.getApellidos() +
                                    ": " + especialidadOpt.get().getNombre());
                    cambio = true;
                } else {
                    System.out.println("ADVERTENCIA: No se encontró especialidad para '" + medico.getEspecialidad() +
                            "' del médico " + medico.getNombres() + " " + medico.getApellidos());
                }
            }

            if (cambio) {
                medicoRepository.save(medico);
                sincronizados++;
            }
        }

        System.out.println("=== Sincronización completada: " + sincronizados + " médicos actualizados ===");
    }

    private Optional<Especialidad> buscarEspecialidad(String nombre) {
        // Mapeo explícito para correcciones conocidas
        String nombreBusqueda = nombre;
        if (nombre.equalsIgnoreCase("Endocrinología")) {
            nombreBusqueda = "Medicina General / Medicina Familiar";
        } else if (nombre.equalsIgnoreCase("Neurología")) {
            nombreBusqueda = "Medicina Interna";
        } else if (nombre.equalsIgnoreCase("Ginecología")) {
            nombreBusqueda = "Ginecología y Obstetricia";
        } else if (nombre.equalsIgnoreCase("Traumatología")) {
            nombreBusqueda = "Traumatología y Ortopedia";
        }

        // Buscar exacto con el nombre corregido
        Optional<Especialidad> especialidad = especialidadRepository.findByNombre(nombreBusqueda);
        if (especialidad.isPresent()) {
            return especialidad;
        }

        // Buscar por coincidencia parcial
        List<Especialidad> todas = especialidadRepository.findAll();
        for (Especialidad esp : todas) {
            if (esp.getNombre().contains(nombreBusqueda) || nombreBusqueda.contains(esp.getNombre())) {
                return Optional.of(esp);
            }
        }

        return Optional.empty();
    }
}
