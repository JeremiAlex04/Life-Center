package com.example.demo.service;
import com.example.demo.model.Cita;
import com.example.demo.repository.CitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class CitaService {
    @Autowired
    private CitaRepository citaRepository;

    public List<Cita> findAll() {
        return citaRepository.findAll();
    }

    public Cita save(Cita cita) {
        return citaRepository.save(cita);
    }

    public List<Cita> findTodayCitasByMedicoId(Long medicoId) {
        return citaRepository.findByMedicoIdAndFecha(medicoId, LocalDate.now());
    }
}