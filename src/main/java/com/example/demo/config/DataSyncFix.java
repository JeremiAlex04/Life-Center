package com.example.demo.config;

import com.example.demo.model.Especialidad;
import com.example.demo.model.Medico;
import com.example.demo.repository.EspecialidadRepository;
import com.example.demo.repository.MedicoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Order(2)
@Profile("!test")
public class DataSyncFix implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSyncFix.class);

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private EspecialidadRepository especialidadRepository;

    @Override
    public void run(String... args) {
        LOGGER.info("Iniciando sincronizacion de especialidades");

        List<Medico> medicos = medicoRepository.findAll();
        int sincronizados = 0;

        for (Medico medico : medicos) {
            boolean cambio = false;

            if (medico.getEspecialidadObj() != null) {
                String nombreEspecialidad = medico.getEspecialidadObj().getNombre();
                if (!nombreEspecialidad.equals(medico.getEspecialidad())) {
                    medico.setEspecialidad(nombreEspecialidad);
                    cambio = true;
                }
            } else if (medico.getEspecialidad() != null && !medico.getEspecialidad().isEmpty()) {
                Optional<Especialidad> especialidadOpt = buscarEspecialidad(medico.getEspecialidad());
                if (especialidadOpt.isPresent()) {
                    medico.setEspecialidadObj(especialidadOpt.get());
                    medico.setEspecialidad(especialidadOpt.get().getNombre());
                    cambio = true;
                } else {
                    LOGGER.warn("No se encontro especialidad para medico {}", medico.getIdMedico());
                }
            }

            if (cambio) {
                medicoRepository.save(medico);
                sincronizados++;
            }
        }

        LOGGER.info("Sincronizacion completada: {} medicos actualizados", sincronizados);
    }

    private Optional<Especialidad> buscarEspecialidad(String nombre) {
        String nombreBusqueda = nombre;
        if (nombre.equalsIgnoreCase("Endocrinologia")) {
            nombreBusqueda = "Medicina General / Medicina Familiar";
        } else if (nombre.equalsIgnoreCase("Neurologia")) {
            nombreBusqueda = "Medicina Interna";
        } else if (nombre.equalsIgnoreCase("Ginecologia")) {
            nombreBusqueda = "Ginecologia y Obstetricia";
        } else if (nombre.equalsIgnoreCase("Traumatologia")) {
            nombreBusqueda = "Traumatologia y Ortopedia";
        }

        Optional<Especialidad> especialidad = especialidadRepository.findByNombre(nombreBusqueda);
        if (especialidad.isPresent()) {
            return especialidad;
        }

        List<Especialidad> todas = especialidadRepository.findAll();
        for (Especialidad esp : todas) {
            if (esp.getNombre().contains(nombreBusqueda) || nombreBusqueda.contains(esp.getNombre())) {
                return Optional.of(esp);
            }
        }

        return Optional.empty();
    }
}
