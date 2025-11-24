package com.example.demo.service;

import com.example.demo.model.Consultorio;
import com.example.demo.repository.ConsultorioRepository;
import com.example.demo.repository.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConsultorioService {

    @Autowired
    private ConsultorioRepository consultorioRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    public List<Consultorio> findAll() {
        return consultorioRepository.findAll();
    }

    public Optional<Consultorio> findById(Long id) {
        return consultorioRepository.findById(id);
    }

    public Consultorio save(Consultorio consultorio) {
        return consultorioRepository.save(consultorio);
    }

    public void deleteById(Long id) {
        consultorioRepository.deleteById(id);
    }

    public boolean canDelete(Long id) {
        // Check if any doctors are assigned to this consultorio
        Consultorio consultorio = consultorioRepository.findById(id).orElse(null);
        if (consultorio == null)
            return false;
        long count = medicoRepository.countByConsultorio(consultorio);
        return count == 0;
    }

    public long countAssignedDoctors(Long consultorioId) {
        Consultorio consultorio = consultorioRepository.findById(consultorioId).orElse(null);
        if (consultorio == null)
            return 0;
        return medicoRepository.countByConsultorio(consultorio);
    }
}
