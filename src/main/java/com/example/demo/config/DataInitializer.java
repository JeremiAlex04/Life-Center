package com.example.demo.config;

import com.example.demo.model.Rol;
import com.example.demo.model.Usuario;
import com.example.demo.model.Consultorio;
import com.example.demo.repository.ConsultorioRepository;
import com.example.demo.model.Medico;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.repository.MedicoRepository;
import com.example.demo.model.Paciente;
import com.example.demo.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.List;
import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

        @Autowired
        private UsuarioRepository usuarioRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Autowired
        private MedicoRepository medicoRepository;

        @Autowired
        private ConsultorioRepository consultorioRepository;

        @Autowired
        private PacienteRepository pacienteRepository;

        @Override
        public void run(String... args) throws Exception {

                Usuario admin = usuarioRepository.findByUsername("admin")
                                .orElse(new Usuario());

                if (admin.getId() == null) {
                        admin.setUsername("admin");
                        admin.setPassword(passwordEncoder.encode("admin123"));
                        admin.setRol(Rol.ROLE_ADMIN);
                        usuarioRepository.save(admin);
                }

                // Inicializar Consultorios (5 por piso en pisos 3 y 4)
                if (consultorioRepository.count() == 0) {
                        // Piso 3: 301-305
                        for (int i = 1; i <= 5; i++) {
                                Consultorio consultorio = new Consultorio();
                                consultorio.setNumero(String.valueOf(300 + i));
                                consultorio.setPiso("3");
                                consultorio.setEstado("DISPONIBLE");
                                consultorio.setTipo("GENERAL");
                                consultorioRepository.save(consultorio);
                        }
                        // Piso 4: 401-405
                        for (int i = 1; i <= 5; i++) {
                                Consultorio consultorio = new Consultorio();
                                consultorio.setNumero(String.valueOf(400 + i));
                                consultorio.setPiso("4");
                                consultorio.setEstado("DISPONIBLE");
                                consultorio.setTipo("GENERAL");
                                consultorioRepository.save(consultorio);
                        }
                } else {
                        // Corregir datos existentes si no cumplen la regla y asignar valores por
                        // defecto a nuevos campos
                        List<Consultorio> consultorios = consultorioRepository.findAll();
                        for (Consultorio c : consultorios) {
                                boolean changed = false;
                                try {
                                        int numero = Integer.parseInt(c.getNumero());
                                        String piso = c.getPiso();

                                        // Caso específico detectado: 306-310 en Piso 4.
                                        if (piso.equals("4") && numero >= 306 && numero <= 310) {
                                                int nuevoNumero = 400 + (numero - 305); // 306->401, 310->405
                                                c.setNumero(String.valueOf(nuevoNumero));
                                                changed = true;
                                                System.out.println("Corregido consultorio: " + numero + " -> "
                                                                + nuevoNumero);
                                        }
                                } catch (NumberFormatException e) {
                                        // Ignorar
                                }

                                // Asignar valores por defecto si son nulos (migración)
                                if (c.getEstado() == null) {
                                        c.setEstado("DISPONIBLE");
                                        changed = true;
                                }
                                if (c.getTipo() == null) {
                                        c.setTipo("GENERAL");
                                        changed = true;
                                }

                                if (changed) {
                                        consultorioRepository.save(c);
                                }
                        }
                }

                // Obtener todos los consultorios para asignar a médicos
                List<Consultorio> consultorios = consultorioRepository.findAll();

                // 10 medico inicializados
                String[][] medicosData = {
                                { "medico1", "Andrés", "Salazar", "Cardiología", "48219357", "988111222",
                                                "andres.salazar@example.com",
                                                "Mañana" },
                                { "medico2", "Valeria", "Cornejo", "Pediatría", "70358149", "988222333",
                                                "valeria.cornejo@example.com",
                                                "Tarde" },
                                { "medico3", "Ricardo", "Vargas", "Dermatología", "12974853", "988333444",
                                                "ricardo.vargas@example.com",
                                                "Mañana" },
                                { "medico4", "Gabriela", "Rivas", "Neurología", "86432091", "988444555",
                                                "gabriela.rivas@example.com",
                                                "Tarde" },
                                { "medico5", "Hernán", "Montoya", "Ginecología", "51794268", "988555666",
                                                "hernan.montoya@example.com",
                                                "Mañana" },
                                { "medico6", "Daniela", "Paredes", "Traumatología", "23058647", "988666777",
                                                "daniela.paredes@example.com", "Tarde" },
                                { "medico7", "Sebastián", "Aguilar", "Oftalmología", "94517230", "988777888",
                                                "sebastian.aguilar@example.com", "Mañana" },
                                { "medico8", "Camila", "Landa", "Psiquiatría", "68124059", "988888999",
                                                "camila.landa@example.com",
                                                "Tarde" },
                                { "medico9", "Eduardo", "Reyes", "Urología", "37290618", "988999000",
                                                "eduardo.reyes@example.com", "Mañana" },
                                { "medico10", "Mariana", "Quispe", "Endocrinología", "80452193", "988000111",
                                                "mariana.quispe@example.com", "Tarde" }
                };

                // Obtener todos los usuarios
                int consultorioIndex = 0;
                for (String[] data : medicosData) {
                        String nombre = data[1];
                        String apellido = data[2];
                        String dni = data[4];
                        String username = dni;
                        String password = (nombre.substring(0, 1) + apellido.substring(0, 1)).toLowerCase() + "2010";

                        if (medicoRepository.findByNumeroColegiatura(data[4]).isEmpty()) {
                                Usuario usuario = new Usuario();
                                usuario.setUsername(username);
                                usuario.setPassword(passwordEncoder.encode(password));
                                usuario.setRol(Rol.ROLE_MEDICO);

                                Medico medico = new Medico();
                                medico.setUsuario(usuario);
                                medico.setNombres(nombre);
                                medico.setApellidos(apellido);
                                medico.setEspecialidad(data[3]);
                                medico.setNumeroColegiatura(data[4]);
                                medico.setTelefono(data[5]);
                                medico.setEmail(data[6]);
                                medico.setTurno(data[7]);
                                medico.setDni(dni);
                                medico.setEstado("ACTIVO");
                                medico.setAnioEgreso(2010);
                                medico.setFotoUrl("/img/fotos-perfil/medicos/" + data[0] + ".jpg");

                                // Asignar consultorio
                                if (consultorioIndex < consultorios.size()) {
                                        Consultorio consultorioAsignado = consultorios.get(consultorioIndex);
                                        medico.setConsultorio(consultorioAsignado);

                                        // Sincronizar el Tipo del Consultorio con la Especialidad del Médico
                                        consultorioAsignado.setTipo(data[3]); // data[3] es la Especialidad
                                        consultorioRepository.save(consultorioAsignado);

                                        consultorioIndex++;
                                }

                                medicoRepository.save(medico);
                        }
                }

                // 15 pacientes inicializados
                String[][] pacientesData = {
                                { "71234567", "Carlos", "Perez", "1990-05-15", "Masculino", "987654321", "Lima",
                                                "Av. Arequipa 123",
                                                "12345" },
                                { "72345678", "Ana", "Gomez", "1985-08-20", "Femenino", "987123456", "Miraflores",
                                                "Calle Larco 456",
                                                "23456" },
                                { "73456789", "Luis", "Rodriguez", "1992-11-30", "Masculino", "998765432", "San Isidro",
                                                "Av. Javier Prado 789", "34567" },
                                { "74567890", "Maria", "Fernandez", "1988-02-10", "Femenino", "976543210", "Surco",
                                                "Calle Montebello 101", "45678" },
                                { "75678901", "Jorge", "Lopez", "1995-07-25", "Masculino", "965432109", "La Molina",
                                                "Av. La Molina 202", "56789" },
                                { "76789012", "Laura", "Martinez", "1998-09-05", "Femenino", "954321098", "Barranco",
                                                "Av. Grau 303",
                                                "67890" },
                                { "77890123", "Pedro", "Sanchez", "1980-01-12", "Masculino", "943210987", "Lince",
                                                "Av. Arequipa 404",
                                                "78901" },
                                { "78901234", "Sofia", "Ramirez", "2000-03-22", "Femenino", "932109876", "Pueblo Libre",
                                                "Av. Bolivar 505", "89012" },
                                { "79012345", "Diego", "Torres", "1993-06-18", "Masculino", "921098765", "Jesus Maria",
                                                "Av. Salaverry 606", "90123" },
                                { "80123456", "Carmen", "Diaz", "1983-12-01", "Femenino", "910987654", "Magdalena",
                                                "Av. Brasil 707",
                                                "01234" },
                                { "81234567", "Javier", "Vargas", "1991-04-14", "Masculino", "909876543", "San Miguel",
                                                "Av. La Marina 808", "11234" },
                                { "82345678", "Rosa", "Castro", "1987-10-28", "Femenino", "989876543", "San Borja",
                                                "Av. Aviacion 909",
                                                "22345" },
                                { "83456789", "Miguel", "Silva", "1996-08-08", "Masculino", "978765432", "Chorrillos",
                                                "Av. Huaylas 1010", "33456" },
                                { "84567890", "Elena", "Rojas", "1989-05-19", "Femenino", "967654321",
                                                "Villa El Salvador",
                                                "Av. El Sol 1111", "44567" },
                                { "85678901", "Francisco", "Soto", "1994-02-23", "Masculino", "956543210",
                                                "San Juan de Lurigancho",
                                                "Av. Proceres 1212", "55678" }
                };

                for (String[] data : pacientesData) {
                        String dni = data[0];
                        if (pacienteRepository.findByDni(dni).isEmpty()) {
                                String nombre = data[1];
                                String apellido = data[2];

                                Usuario usuario = new Usuario();
                                usuario.setUsername(dni);
                                String password = (nombre.substring(0, 1) + apellido.substring(0, 1)).toLowerCase()
                                                + LocalDate.parse(data[3]).getYear();
                                usuario.setPassword(passwordEncoder.encode(password));
                                usuario.setRol(Rol.ROLE_PACIENTE);

                                Paciente paciente = new Paciente();
                                paciente.setUsuario(usuario);
                                paciente.setDni(dni);
                                paciente.setNombres(nombre);
                                paciente.setApellidos(apellido);
                                paciente.setFechaNacimiento(LocalDate.parse(data[3]));
                                paciente.setGenero(data[4]);
                                paciente.setTelefono(data[5]);
                                paciente.setDistrito(data[6]);
                                paciente.setDireccion(data[7]);
                                paciente.setNumeroSeguro(data[8]);

                                pacienteRepository.save(paciente);
                        }
                }

                // Sincronización final: Asegurar que los consultorios tengan el tipo de su
                // médico asignado (Corrección para datos existentes)
                List<Consultorio> allConsultorios = consultorioRepository.findAll();
                for (Consultorio c : allConsultorios) {
                        List<Medico> medicos = medicoRepository.findByConsultorio(c);
                        if (!medicos.isEmpty()) {
                                // Tomamos la especialidad del primer médico asignado
                                String especialidad = medicos.get(0).getEspecialidad();

                                // Si el tipo es GENERAL o nulo, o diferente a la especialidad, lo actualizamos
                                if (c.getTipo() == null || c.getTipo().equals("GENERAL")
                                                || !c.getTipo().equals(especialidad)) {
                                        c.setTipo(especialidad);
                                        consultorioRepository.save(c);
                                        System.out.println("Sincronizado Consultorio " + c.getNumero() + " a Tipo: "
                                                        + especialidad);
                                }
                        }
                }
        }
}