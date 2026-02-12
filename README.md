# LifeCenter - Sistema de Gestión Hospitalaria

**LifeCenter** es una plataforma web integral diseñada para modernizar y optimizar la gestión de centros médicos. Facilita la interacción entre pacientes, médicos y administradores, proporcionando herramientas eficientes para la reserva de citas, gestión de historiales clínicos y administración de recursos hospitalarios.

## 🚀 Características Principales

### 🌐 Portal Público
- **Landing Page Moderna**: Diseño atractivo y responsivo con información sobre servicios y testimonios.
- **Catálogo de Médicos**: Visualización de staff médico con filtros por especialidad.
- **Especialidades**: Información detallada sobre las áreas médicas atendidas.
- **Contacto**: Formulario de contacto y ubicación.

### 🔐 Módulo de Seguridad y Autenticación
- **Login Unificado**: Acceso seguro para todos los roles (Admin, Médico, Paciente).
- **Registro de Pacientes**: Formulario de auto-registro con validaciones de DNI y datos personales.
- **Recuperación de Contraseña**: Flujo seguro para restablecer credenciales.
- **Roles y Permisos**: Control de acceso basado en roles (`ROLE_ADMIN`, `ROLE_MEDICO`, `ROLE_PACIENTE`) utilizando Spring Security.

### 👤 Módulo de Paciente
- **Agendamiento de Citas**: Interfaz intuitiva para reservar citas médicas seleccionando especialidad, médico y horario.
- **Historial Clínico**: Acceso a su propio historial de atenciones y diagnósticos.
- **Dashboard Personal**: Resumen de próximas citas y estado de salud.

### 👨‍⚕️ Módulo de Médico
- **Gestión de Agenda**: Visualización de citas programadas (diarias, semanales).
- **Atención de Pacientes**: Registro de diagnósticos, recetas y notas de evolución en la historia clínica.
- **Perfil Profesional**: Gestión de información pública (foto, especialidad).

### 🛠️ Módulo de Administrador
- **Gestión de Usuarios**: Administración centralizada de cuentas.
- **Gestión de Médicos**: Alta, baja y modificación de personal médico. *Nota: Al crear un médico, el sistema genera automáticamente su usuario de acceso.*
- **Gestión de Consultorios**: Administración de espacios físicos y asignación de recursos.
- **Gestión de Especialidades**: Configuración del catálogo de servicios médicos.
- **Reportes**: Visualización de métricas clave (citas por día, ocupación, etc.).

## 🧠 Lógica de Negocio Destacada

1.  **Integración Usuario-Rol**:
    - Cada actor del sistema (Médico, Paciente) está vinculado a una entidad `Usuario` que maneja las credenciales.
    - El DNI se utiliza como identificador único y nombre de usuario por defecto.

2.  **Validaciones de Negocio**:
    - **DNI**: Validación estricta de 8 dígitos numéricos en formularios de registro.
    - **Disponibilidad**: El sistema impide agendar citas en horarios ocupados o con médicos no disponibles.
    - **Integridad**: No se pueden eliminar registros que tengan dependencias activas (ej. médicos con citas futuras).

3.  **Automatización**:
    - Generación automática de credenciales para médicos nuevos (Usuario: DNI, Password: Generada).
    - Asignación automática de roles según el tipo de registro.

## 💻 Stack Tecnológico

El proyecto está construido utilizando tecnologías robustas y modernas del ecosistema Java:

*   **Backend**:
    *   **Java 17+**: Lenguaje de programación principal.
    *   **Spring Boot 3.x**: Framework para el desarrollo de aplicaciones web.
    *   **Spring Data JPA**: Capa de persistencia y ORM (Hibernate).
    *   **Spring Security**: Gestión de autenticación y autorización.
    *   **MySQL**: Base de datos relacional.

*   **Frontend**:
    *   **Thymeleaf**: Motor de plantillas para renderizado del lado del servidor.
    *   **Bootstrap 5**: Framework CSS para diseño responsivo y componentes UI.
    *   **JavaScript (Vanilla)**: Lógica del lado del cliente y validaciones (ej. `registro.js`).
    *   **HTML5 & CSS3**: Estructura y estilos personalizados (`lifecenter-theme.css`).

## 📂 Estructura del Proyecto

```
src/main/
├── java/com/example/demo/
│   ├── config/          # Configuraciones (Security, MVC, DataInit)
│   ├── controller/      # Controladores MVC (Vistas y API)
│   ├── entity/          # Entidades JPA (Modelo de datos)
│   ├── repository/      # Interfaces de acceso a datos (DAO)
│   ├── service/         # Lógica de negocio
│   └── LifeCenterApplication.java
└── resources/
    ├── static/          # Recursos estáticos (CSS, JS, Imágenes)
    │   ├── css/
    │   ├── img/
    │   └── js/
    └── templates/       # Vistas Thymeleaf (.html)
        ├── admin/       # Vistas protegidas de administrador
        ├── fragments/   # Componentes reutilizables (Navbar, Footer)
        ├── portal/      # Vistas de dashboards (Médico, Paciente)
        └── ...          # Vistas públicas
```

## 🚀 Instalación y Ejecución

1.  **Clonar el repositorio**:
    ```bash
    git clone https://github.com/JeremiAlex04/Life-Center.git
    cd Life-Center
    ```

2.  **Configurar Base de Datos**:
    - Asegúrese de tener MySQL ejecutándose.
    - Configure las credenciales en `src/main/resources/application.properties`.

3.  **Ejecutar la aplicación**:
    ```bash
    ./mvnw spring-boot:run
    ```
    O ejecute la clase `LifeCenterApplication.java` desde su IDE favorito.

4.  **Acceso**:
    - Abra su navegador en `http://localhost:8080`.

## 🔑 Credenciales de Acceso (Datos de Prueba)

El sistema se inicializa con datos de prueba (`DataInitializer.java`) para facilitar las pruebas.

### 🛡️ Administrador
- **Usuario**: `admin`
- **Contraseña**: `admin123`

### 👨‍⚕️ Médicos (Ejemplos)
El formato de contraseña por defecto para médicos es: `[InicialNombre][InicialApellido]2010` (todo en minúsculas).

| Nombre | Especialidad | Usuario (DNI) | Contraseña |
| :--- | :--- | :--- | :--- |
| Andrés Salazar | Cardiología | `48219357` | `as2010` |
| Valeria Cornejo | Pediatría | `70358149` | `vc2010` |
| Ricardo Vargas | Dermatología | `12974853` | `rv2010` |
| Gabriela Rivas | Medicina Interna | `86432091` | `gr2010` |

### 👤 Pacientes (Ejemplos)
El formato de contraseña por defecto para pacientes es: `[InicialNombre][InicialApellido][AñoNacimiento]`.

| Nombre | Año Nac. | Usuario (DNI) | Contraseña |
| :--- | :--- | :--- | :--- |
| Carlos Perez | 1990 | `71234567` | `cp1990` |
| Ana Gomez | 1985 | `72345678` | `ag1985` |
| Luis Rodriguez | 1992 | `73456789` | `lr1992` |

> **Nota**: Los nuevos usuarios registrados a través del formulario público crean su propia contraseña.

## 📖 Explicación del Código y Arquitectura

El proyecto sigue una arquitectura en capas clásica de Spring Boot (MVC + Service + Repository).

### 1. Capa de Configuración (`com.example.demo.config`)
- **`SecurityConfiguration.java`**: Define las reglas de seguridad. Configura qué rutas son públicas (`/`, `/login`, `/registro`, `/medicos`) y cuáles requieren autenticación. Asigna permisos por rol (ej. solo ADMIN puede ver `/admin/**`).
- **`DataInitializer.java`**: Clase que se ejecuta al iniciar la aplicación. Se encarga de poblar la base de datos con usuarios, médicos, especialidades y consultorios iniciales si la base de datos está vacía.

### 2. Capa de Modelo / Entidades (`com.example.demo.model`)
Representan las tablas de la base de datos.
- **`Usuario`**: Entidad central para la autenticación. Contiene `username`, `password` y `rol`.
- **`Medico`**: Almacena datos profesionales (CMP, especialidad). Tiene una relación `OneToOne` con `Usuario`.
- **`Paciente`**: Almacena datos personales e historial. Tiene una relación `OneToOne` con `Usuario`.
- **`Cita`**: Representa el agendamiento. Relaciona `Medico`, `Paciente` y `Consultorio`.
- **`Consultorio`**: Espacios físicos donde se realizan las atenciones.

### 3. Capa de Repositorio (`com.example.demo.repository`)
Interfaces que extienden `JpaRepository`. Permiten realizar operaciones CRUD y consultas personalizadas a la base de datos sin escribir SQL manual (ej. `findByDni`, `findByEspecialidad`).

### 4. Capa de Servicio (`com.example.demo.service`)
Contiene la lógica de negocio pura.
- **`UsuarioService`**: Maneja la creación de usuarios y encriptación de contraseñas.
- **`CitaService`**: Lógica compleja para agendar citas, validar disponibilidad de horarios y médicos, y evitar conflictos.
- **`MedicoService`**: Gestión de la información de los médicos.

### 5. Capa de Controladores (`com.example.demo.controller`)
Maneja las peticiones HTTP y decide qué vista mostrar.
- **`AuthController`**: Gestiona el Login y Registro de pacientes.
- **`AdminController`**: Controla el dashboard del administrador y los mantenimientos (CRUDs).
- **`PortalPacienteController`**: Maneja las vistas del paciente (Mis Citas, Historia Clínica).
- **`PortalMedicoController`**: Maneja las vistas del médico (Agenda, Atención de Citas).
- **`HomeController`**: Sirve las páginas públicas (`index`, `medicos`, `contacto`).

### 6. Frontend (Thymeleaf + JS)
- **Fragmentos (`templates/fragments/`)**: Piezas de código reutilizables como el `navbar.html` y `footer.html`. El navbar contiene lógica condicional para mostrar diferentes menús según si el usuario es Admin, Médico o Paciente.
- **JavaScript (`static/js/`)**:
    - `registro.js`: Contiene validaciones del lado del cliente, como asegurar que el DNI tenga máximo 8 dígitos.
    - `login.js`: Efectos visuales para la página de login.

---
&copy; 2025 LifeCenter. Desarrollado por Jeremi Alexander.

## Deploy en Render (GitHub)

### 1. Preparar y subir a GitHub

```bash
git add .
git commit -m "Prepare project for Render deployment"
git branch -M main
git remote add origin https://github.com/TU_USUARIO/TU_REPO.git
git push -u origin main
```

### 2. Crear base de datos MySQL en Render

- Crea un servicio MySQL en Render.
- Guarda estos datos: host, port, database, username y password.

### 3. Desplegar con Blueprint (`render.yaml`)

- En Render: `New` -> `Blueprint`.
- Conecta tu repositorio.
- Render detectara `render.yaml` y creara el servicio web.

### 4. Configurar variables de entorno del Web Service

Configura en Render:

- `SPRING_DATASOURCE_URL=jdbc:mysql://<MYSQL_HOST>:<MYSQL_PORT>/<MYSQL_DB>?useSSL=false&serverTimezone=UTC`
- `SPRING_DATASOURCE_USERNAME=<MYSQL_USER>`
- `SPRING_DATASOURCE_PASSWORD=<MYSQL_PASSWORD>`
- `SPRING_JPA_HIBERNATE_DDL_AUTO=update`
- `SPRING_JPA_SHOW_SQL=false`
- `LIFECENTER_ADMIN_PASSWORD=<PASSWORD_ADMIN_SEGURO>`

### 5. Verificar despliegue

- Revisa logs de build y start.
- Abre la URL publica del servicio.
- Si falla conexion a DB, valida host/puerto/credenciales.
