package com.example.demo.service;

import com.example.demo.model.HistorialClinico;
import com.example.demo.model.Paciente;
import com.example.demo.repository.HistorialClinicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistorialClinicoService {

    @Autowired
    private HistorialClinicoRepository historialClinicoRepository;

    public HistorialClinico save(HistorialClinico historialClinico) {
        return historialClinicoRepository.save(historialClinico);
    }

    public List<HistorialClinico> findByPaciente(Paciente paciente) {
        return historialClinicoRepository.findByPaciente(paciente);
    }
}
