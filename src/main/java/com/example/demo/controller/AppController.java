package com.example.demo.controller;

import com.example.demo.model.Medico;
import com.example.demo.service.MedicoService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class AppController {

    public static class Specialty {
        private String name;
        private String description;
        private String imageUrl;

        public Specialty(String name, String description, String imageUrl) {
            this.name = name;
            this.description = description;
            this.imageUrl = imageUrl;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getImageUrl() {
            return imageUrl;
        }
    }

    @Autowired
    private MedicoService medicoService;

    /**
     * Mapea la raíz del sitio ("/") para mostrar la página pública principal.
     * Carga la lista de médicos y los agrupa por especialidad.
     */
    @GetMapping("/")
    public String viewHomePage(Model model) {
        List<Medico> medicos = medicoService.findAll();
        Map<String, List<Medico>> medicosPorEspecialidad = medicos.stream()
                .collect(Collectors.groupingBy(Medico::getEspecialidad));
        model.addAttribute("medicosPorEspecialidad", medicosPorEspecialidad);
        return "index"; // Muestra templates/index.html
    }

    /**
     * Mapea la página de especialidades.
     */
    @GetMapping("/especialidades")
    public String viewEspecialidadesPage(Model model) {
        List<Specialty> specialties = new ArrayList<>();
        specialties.add(new Specialty("Cardiología", "Cuidado experto del corazón y sistema circulatorio.", "/img/especialidades/cardiologia.jpg"));
        specialties.add(new Specialty("Dermatología", "Tratamiento integral de la piel, cabello y uñas.", "/img/especialidades/dermatologia.jpg"));
        specialties.add(new Specialty("Pediatría", "Atención médica completa para niños y adolescentes.", "/img/especialidades/pediatria.jpg"));
        specialties.add(new Specialty("Ginecología", "Salud integral de la mujer en todas las etapas de su vida.", "/img/especialidades/ginecologia.jpg"));
        specialties.add(new Specialty("Traumatología", "Especialistas en lesiones del aparato locomotor.", "/img/especialidades/traumatologia.jpg"));
        specialties.add(new Specialty("Neurología", "Estudio y tratamiento de los trastornos del sistema nervioso.", "/img/especialidades/neurologia.jpg"));
        specialties.add(new Specialty("Oftalmología", "Salud visual y tratamiento de enfermedades oculares.", "/img/especialidades/oftalmologia.jpg"));
        specialties.add(new Specialty("Oncología", "Diagnóstico y tratamiento del cáncer.", "/img/especialidades/oncologia.jpg"));
        specialties.add(new Specialty("Urología", "Tratamiento del sistema urinario y reproductivo masculino.", "/img/especialidades/urologia.jpg"));
        specialties.add(new Specialty("Medicina Interna", "Diagnóstico y tratamiento de enfermedades en adultos.", "/img/especialidades/medicina_interna.jpg"));
        specialties.add(new Specialty("Cirugía General", "Procedimientos quirúrgicos para diversas afecciones.", "/img/especialidades/cirugia_general.jpg"));
        specialties.add(new Specialty("Otorrinolaringología", "Tratamiento de oído, nariz y garganta.", "/img/especialidades/otorrinolaringologia.jpg"));
        
        model.addAttribute("specialties", specialties);
        return "especialidades";
    }
}