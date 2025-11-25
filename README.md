# LifeCenter - Sistema de Gestión Hospitalaria

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.6-green)
![MySQL](https://img.shields.io/badge/MySQL-Database-blue)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-Template_Engine-darkgreen)
![Security](https://img.shields.io/badge/Spring_Security-Protected-red)

**LifeCenter** es una plataforma web integral desarrollada con Spring Boot diseñada para administrar los procesos clínicos y administrativos de un centro médico. Facilita la gestión de citas, expedientes médicos y la administración de recursos hospitalarios mediante una interfaz segura basada en roles.

## 📋 Tabla de Contenidos
- [Características y Lógica de Negocio](#-características-y-lógica-de-negocio)
- [Tecnologías](#-tecnologías)
- [Arquitectura del Proyecto](#-arquitectura-del-proyecto)
- [Instalación y Despliegue](#-instalación-y-despliegue)
- [Seguridad](#-seguridad)

---

## 🧠 Características y Lógica de Negocio

El sistema implementa una lógica de negocio segregada por roles de usuario, garantizando que cada actor tenga acceso únicamente a las funciones pertinentes a su labor.

### 1. Portal del Paciente (`ROLE_PACIENTE`)
Diseñado para la autogestión del usuario final.
* **Registro e Inicio de Sesión:** Acceso seguro mediante credenciales encriptadas.
* **Gestión de Citas:** Solicitar nuevas citas médicas según especialidad y disponibilidad.
* **Historial Clínico:** Visualización del historial médico personal.
* **Dashboard:** Vista resumen de próximas citas y estado actual.

### 2. Portal del Médico (`ROLE_MEDICO`)
Herramientas para la gestión clínica diaria.
* **Agenda Médica:** Visualización de citas asignadas.
* **Atención de Pacientes:** Registro de diagnósticos, recetas y evolución en el historial clínico durante la consulta.
* **Gestión de Pacientes:** Acceso a la lista de pacientes asignados.

### 3. Panel Administrativo (`ROLE_ADMIN`)
Control total sobre la configuración del sistema.
* **Gestión de Recursos:** ABM (Alta, Baja, Modificación) de Consultorios y Especialidades.
* **Gestión de Usuarios:** Administración de cuentas de Médicos y Pacientes.
* **Supervisión:** Vista global de todas las citas y operaciones del centro.

---

## 🛠 Tecnologías

* **Backend Framework:** Spring Boot 3.5.6 (Web, Data JPA, Security).
* **Lenguaje:** Java 17.
* **Motor de Plantillas:** Thymeleaf con integración de Spring Security 6.
* **Base de Datos:** MySQL.
* **Frontend:** Bootstrap 5 (Estilos y Componentes), Bootstrap Icons.
* **Herramientas de Construcción:** Maven (Wrapper incluido).
* **Utilidades:** Lombok (para reducción de código repetitivo).

---

## 📂 Arquitectura del Proyecto

La estructura sigue el patrón MVC (Modelo-Vista-Controlador) estándar de Spring Boot:

```text
src/main/java/com/example/demo
├── config       # Configuración de Seguridad (SecurityConfig) e Inicialización de Datos
├── controller   # Controladores Web (Admin, Medico, Paciente, Auth)
├── model        # Entidades JPA (Usuario, Cita, HistorialClinico, Consultorio, etc.)
├── repository   # Interfaces JpaRepository para acceso a datos
├── service      # Lógica de negocio y servicios
└── LifeCenterApplication.java
```

-----

## 🚀 Instalación y Despliegue

### Requisitos Previos

  * JDK 17 o superior.
  * MySQL Server en ejecución.

### Pasos

1.  **Clonar el repositorio**

    ```bash
    git clone https://github.com/tu-usuario/life-center.git
    cd life-center
    ```

2.  **Configurar la Base de Datos**
    Crea una base de datos vacía en tu servidor MySQL:

    ```sql
    CREATE DATABASE hospital_db;
    ```

3.  **Configurar Credenciales**
    Abre el archivo `src/main/resources/application.properties` y ajusta tu usuario y contraseña:

    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/hospital_db?useSSL=false&serverTimezone=UTC
    spring.datasource.username=TU_USUARIO
    spring.datasource.password=TU_CONTRASEÑA

    # Hibernate creará/actualizará las tablas automáticamente
    spring.jpa.hibernate.ddl-auto=update
    ```

4.  **Ejecutar la Aplicación**
    Usa el wrapper de Maven incluido:

    *En Windows:*
    ```bash
    mvnw.cmd spring-boot:run
    ```

    *En Linux/Mac:*
    ```bash
    ./mvnw spring-boot:run
    ```

5.  **Acceso**
    Navega a: `http://localhost:8080`

-----

## 🔒 Seguridad

La seguridad es gestionada por **Spring Security**. El acceso a las rutas está protegido según el rol del usuario autenticado:

| Ruta | Acceso Requerido | Descripción |
| :--- | :--- | :--- |
| `/`, `/login`, `/registro` | Público | Páginas de acceso y aterrizaje. |
| `/admin/**` | `ROLE_ADMIN` | Gestión administrativa. |
| `/medico/**` | `ROLE_MEDICO` | Portal de atención médica. |
| `/paciente/**` | `ROLE_PACIENTE` | Portal del paciente. |

  * Las contraseñas se almacenan con hash **BCrypt**.
  * El sistema incluye un `DataInitializer` (si está habilitado) para crear usuarios por defecto al inicio.

-----

## 🤝 Contribución

Si deseas contribuir a este proyecto, por favor crea un *fork* del repositorio y envía un *pull request*.

## 📄 Licencia

Distribuido bajo la licencia MIT. Ver `LICENSE` para más información.

