package com.example.demo.controller;

import com.example.demo.model.Medico;
import com.example.demo.service.MedicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class AppController {

        public static class Especialidad {

                private String nombre;
                private String descripcion;
                private String urlImagen;

                public Especialidad(String nombre, String descripcion, String urlImagen) {
                        this.nombre = nombre;
                        this.descripcion = descripcion;
                        this.urlImagen = urlImagen;
                }

                public String getNombre() {
                        return nombre;
                }

                public String getDescripcion() {
                        return descripcion;
                }

                public String getUrlImagen() {
                        return urlImagen;
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

                // Ordenar médicos por especialidad, luego nombre y apellido
                medicos.sort(java.util.Comparator.comparing(Medico::getEspecialidad)
                                .thenComparing(Medico::getNombres)
                                .thenComparing(Medico::getApellidos));

                model.addAttribute("medicos", medicos);
                return "index";
        }

        /**
         * Mapea la página de especialidades.
         */
        @GetMapping("/especialidades")
        public String verPaginaEspecialidades(Model model) {
                List<Especialidad> especialidades = new ArrayList<>();
                especialidades.add(
                                new Especialidad("Cardiología", "Cuidado experto del corazón y sistema circulatorio.",
                                                "/img/especialidades/cardiologia.jpg"));
                especialidades.add(new Especialidad("Dermatología", "Tratamiento integral de la piel, cabello y uñas.",
                                "/img/especialidades/dermatologia.jpg"));
                especialidades.add(new Especialidad("Pediatría", "Atención médica completa para niños y adolescentes.",
                                "/img/especialidades/pediatria.jpg"));
                especialidades.add(new Especialidad("Ginecología",
                                "Salud integral de la mujer en todas las etapas de su vida.",
                                "/img/especialidades/ginecologia.jpg"));
                especialidades.add(new Especialidad("Traumatología", "Especialistas en lesiones del aparato locomotor.",
                                "/img/especialidades/traumatologia.jpg"));
                especialidades.add(new Especialidad("Neurología",
                                "Estudio y tratamiento de los trastornos del sistema nervioso.",
                                "/img/especialidades/neurologia.jpg"));
                especialidades.add(
                                new Especialidad("Oftalmología", "Salud visual y tratamiento de enfermedades oculares.",
                                                "/img/especialidades/oftalmologia.jpg"));
                especialidades.add(new Especialidad("Oncología", "Diagnóstico y tratamiento del cáncer.",
                                "/img/especialidades/oncologia.jpg"));
                especialidades.add(new Especialidad("Urología",
                                "Tratamiento del sistema urinario y reproductivo masculino.",
                                "/img/especialidades/urologia.jpg"));
                especialidades.add(new Especialidad("Medicina Interna",
                                "Diagnóstico y tratamiento de enfermedades en adultos.",
                                "/img/especialidades/medicina_interna.jpg"));
                especialidades.add(new Especialidad("Cirugía General",
                                "Procedimientos quirúrgicos para diversas afecciones.",
                                "/img/especialidades/cirugia_general.jpg"));
                especialidades.add(new Especialidad("Otorrinolaringología", "Tratamiento de oído, nariz y garganta.",
                                "/img/especialidades/otorrinolaringologia.jpg"));

                model.addAttribute("especialidades", especialidades);
                return "especialidades";
        }

        /**
         * Mapea la página de plantel médico completo.
         */
        @GetMapping("/medicos")
        public String verPaginaMedicos(Model model) {
                List<Medico> medicos = medicoService.findAll();

                // Ordenar médicos por especialidad, luego nombre y apellido
                medicos.sort(java.util.Comparator.comparing(Medico::getEspecialidad)
                                .thenComparing(Medico::getNombres)
                                .thenComparing(Medico::getApellidos));

                model.addAttribute("medicos", medicos);
                return "medicos";
        }

}
