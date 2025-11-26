package com.example.demo.controller;

import com.example.demo.model.Consultorio;
import com.example.demo.model.Medico;
import com.example.demo.repository.MedicoRepository;
import com.example.demo.service.ConsultorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller; // Mantener Controller para servir la página
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller // Mantener @Controller para el método que sirve la página HTML
@RequestMapping("/admin/consultorios")
@PreAuthorize("hasRole('ADMIN')")
public class ConsultorioAdminController {

    @Autowired
    private ConsultorioService consultorioService;

    @Autowired
    private MedicoRepository medicoRepository;

    // Método para servir la página HTML de gestión de consultorios
    @GetMapping
    public String gestionarConsultorios(Model model) {
        return "admin/consultorios"; // Esta será nuestra única página: admin/consultorios.html
    }

    @GetMapping("/api")
    @ResponseBody
    public List<Consultorio> getAllConsultoriosApi() {
        List<Consultorio> consultorios = consultorioService.findAll();
        consultorios.forEach(c -> {
            long count = consultorioService.countAssignedDoctors(c.getIdConsultorio());
            c.setDoctorCount(count);

            List<Medico> medicos = medicoRepository.findByConsultorio(c);
            List<String> names = medicos.stream()
                    .map(m -> m.getNombres() + " " + m.getApellidos() + " (" + m.getTurno() + ")")
                    .collect(Collectors.toList());
            c.setDoctorNames(names);
        });
        return consultorios;
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Consultorio> getConsultorioByIdApi(@PathVariable Long id) {
        return consultorioService.findById(id)
                .map(consultorio -> {
                    consultorio.setDoctorCount(consultorioService.countAssignedDoctors(consultorio.getIdConsultorio()));

                    List<Medico> medicos = medicoRepository.findByConsultorio(consultorio);
                    List<String> names = medicos.stream()
                            .map(m -> m.getNombres() + " " + m.getApellidos() + " (" + m.getTurno() + ")")
                            .collect(Collectors.toList());
                    consultorio.setDoctorNames(names);

                    return new ResponseEntity<>(consultorio, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<Consultorio> createConsultorioApi(@RequestBody Consultorio consultorio) {
        Consultorio savedConsultorio = consultorioService.save(consultorio);
        return new ResponseEntity<>(savedConsultorio, HttpStatus.CREATED);
    }

    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Consultorio> updateConsultorioApi(@PathVariable Long id,
            @RequestBody Consultorio consultorioDetails) {
        return consultorioService.findById(id)
                .map(consultorio -> {
                    consultorio.setNumero(consultorioDetails.getNumero());
                    consultorio.setPiso(consultorioDetails.getPiso());
                    consultorio.setEstado(consultorioDetails.getEstado());
                    consultorio.setTipo(consultorioDetails.getTipo());
                    Consultorio updatedConsultorio = consultorioService.save(consultorio);
                    return new ResponseEntity<>(updatedConsultorio, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteConsultorioApi(@PathVariable Long id) {
        if (!consultorioService.canDelete(id)) {
            return new ResponseEntity<>("No se puede eliminar el consultorio porque tiene médicos asignados",
                    HttpStatus.CONFLICT);
        }
        consultorioService.deleteById(id);
        return new ResponseEntity<>("Consultorio eliminado exitosamente", HttpStatus.NO_CONTENT);
    }

    @GetMapping("/api/{id}/medicos")
    @ResponseBody
    public List<Medico> getMedicosByConsultorioApi(@PathVariable Long id) {
        Consultorio consultorio = consultorioService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Id de Consultorio inválido:" + id));
        return medicoRepository.findByConsultorio(consultorio);
    }
}
