package com.example.demo.controller;

import com.example.demo.model.Medico;
import com.example.demo.model.Especialidad;
import com.example.demo.service.MedicoService;
import com.example.demo.service.EspecialidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AppController {

        @Autowired
        private MedicoService medicoService;

        @Autowired
        private EspecialidadService especialidadService;

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
                List<Especialidad> especialidades = especialidadService.findActivas();
                System.out.println("DEBUG: Especialidades encontradas en Controller: " + especialidades.size());
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
                model.addAttribute("medicos", medicos);
                return "medicos";
        }

        @GetMapping("/nosotros")
        public String verPaginaNosotros(Model model) {
                return "nosotros";
        }

        @GetMapping("/contacto")
        public String verPaginaContacto(Model model) {
                return "contacto";
        }

}
