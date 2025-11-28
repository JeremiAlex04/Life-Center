package com.example.demo.service;

import com.example.demo.model.HorarioMedico;
import com.example.demo.model.Medico;
import com.example.demo.repository.HorarioMedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class HorarioService {

    @Autowired
    private HorarioMedicoRepository horarioRepository;

    @Transactional
    public void generarHorarios(Medico medico, LocalDate fechaInicio, LocalDate fechaFin) {
        if (medico.getTurno() == null || medico.getDuracionCita() == null) {
            return;
        }

        LocalTime inicioTurno;
        LocalTime finTurno;

        switch (medico.getTurno().toLowerCase()) {
            case "mañana":
                inicioTurno = LocalTime.of(7, 0);
                finTurno = LocalTime.of(13, 0);
                break;
            case "tarde":
                inicioTurno = LocalTime.of(14, 0);
                finTurno = LocalTime.of(20, 0);
                break;
            case "noche":
                inicioTurno = LocalTime.of(20, 0);
                finTurno = LocalTime.of(23, 59); // Ajustar según requerimiento exacto
                break;
            default:
                return;
        }

        // Eliminar horarios futuros existentes para regenerar (estrategia simple)
        // Nota: En un sistema real, habría que tener cuidado con citas ya reservadas.
        // Aquí asumimos que si se cambia el turno, se regenera todo lo disponible.
        // Para simplificar, solo generamos si no existen, o podríamos borrar los
        // DISPONIBLES.
        // Por seguridad, aquí solo generaremos si no existen para ese día.

        List<HorarioMedico> nuevosHorarios = new ArrayList<>();
        LocalDate fechaActual = fechaInicio;

        while (!fechaActual.isAfter(fechaFin)) {
            if (!horarioRepository.existsByMedicoAndFecha(medico.getIdMedico(), fechaActual)) {
                LocalTime horaActual = inicioTurno;
                while (horaActual.plusMinutes(medico.getDuracionCita()).isBefore(finTurno) ||
                        horaActual.plusMinutes(medico.getDuracionCita()).equals(finTurno)) {

                    HorarioMedico horario = new HorarioMedico();
                    horario.setMedico(medico);
                    horario.setFecha(fechaActual);
                    horario.setHoraInicio(horaActual);
                    horario.setHoraFin(horaActual.plusMinutes(medico.getDuracionCita()));
                    horario.setEstado("DISPONIBLE");

                    nuevosHorarios.add(horario);

                    horaActual = horaActual.plusMinutes(medico.getDuracionCita());
                }
            }
            fechaActual = fechaActual.plusDays(1);
        }

        if (!nuevosHorarios.isEmpty()) {
            horarioRepository.saveAll(nuevosHorarios);
        }
    }
}
