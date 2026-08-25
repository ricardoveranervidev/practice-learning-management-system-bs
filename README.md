# 🏛️ Backend BS (Business Service - Microservicio de Dominio)

El **Backend BS (Business Service)** es el microservicio central de dominio del sistema **Mini LMS**. Encapsula toda la lógica de negocio, el modelo relacional de datos, las reglas transaccionales, la persistencia JPA y la seguridad basada en tokens JWT.

---

## 📋 Responsabilidades de la Capa

1. **Gestión de Entidades y Persistencia:** Administra las tablas del modelo relacional (`User`, `Course`, `Enrollment`, `Task`, `TaskProgress`) sobre una base de datos en memoria **H2**.
2. **Aplicación de Reglas de Negocio:**
   - **Prevención de Inscripciones Duplicadas:** Controla que un usuario no pueda registrarse más de una vez en el mismo curso (`DuplicateEnrollmentException` -> HTTP 409 Conflict), reforzado con restricciones de unicidad `@UniqueConstraint(columnNames = {"user_id", "course_id"})`.
   - **Progreso de Tareas Aislado por Usuario:** Cada estudiante registra su avance independiente (`TaskProgress`) al marcar o desmarcar tareas, sin alterar el catálogo de tareas global del curso.
   - **Cálculo Dinámico de Métricas:** Calcula el porcentaje de completitud y tareas finalizadas por curso en tiempo real.
3. **Autenticación Stateless & Seguridad:**
   - Generación y validación de tokens **JWT (HMAC-SHA256)** con expiración configurable.
   - Cifrado seguro de contraseñas con **BCrypt**.
   - Control de acceso por roles (`ROLE_STUDENT`, `ROLE_ADMIN`) y protección de rutas bajo `/me/**`.
4. **Trazabilidad Distribuida de Logs:**
   - Intercepta el encabezado `X-Correlation-ID` mediante `MdcLoggingFilter`.
   - Inyecta el identificador en el `MDC` de SLF4J para que cada línea de log en consola y archivo contenga `[%X{correlationId}]`.
   - Retorna la cabecera `X-Correlation-ID` en la respuesta HTTP.
5. **Seed de Datos Automatizado (`DataSeeder`):** Precarga 3 usuarios y 8 cursos con temáticas tecnológicas reales y múltiples tareas por curso al iniciar la aplicación.

---

## 🛠️ Stack Tecnológico

| Componente | Tecnología |
|---|---|
| **Lenguaje** | Java 17+ |
| **Framework** | Spring Boot 3.3.2 |
| **Persistencia** | Spring Data JPA / Hibernate |
| **Base de Datos** | H2 Database (In-Memory) |
| **Seguridad** | Spring Security 6 + JJWT (`0.11.5`) |
| **Documentación** | Springdoc OpenAPI 3 / Swagger UI (`2.6.0`) |
| **Testing** | JUnit 5 + Mockito + MockMvc |
| **Herramienta de Construcción** | Gradle |

---

## 📁 Estructura del Proyecto

```text
backend-bs/
├── build.gradle                          # Dependencias y configuración del build
├── settings.gradle                       # Nombre del proyecto raíz
├── Dockerfile                            # Imagen Docker multi-stage (eclipse-temurin:17-jre)
└── src/
    ├── main/
    │   ├── resources/
    │   │   └── application.yml           # Puerto 8081, config H2, JWT y logging MDC
    │   └── java/com/lms/bs/
    │       ├── LmsBsApplication.java # Clase principal Spring Boot
    │       ├── config/                   # Configuración MVC, OpenAPI y MdcLoggingFilter
    │       ├── domain/entity/            # Entidades JPA (User, Course, Enrollment, Task, TaskProgress)
    │       ├── repository/               # Repositorios Spring Data JPA
    │       ├── service/                  # Lógica de negocio (Auth, Course, Enrollment, TaskProgress)
    │       ├── controller/               # Controladores REST (/auth, /courses, /me)
    │       ├── dto/                      # Data Transfer Objects y ApiResponse genérico
    │       ├── exception/                # GlobalExceptionHandler y excepciones personalizadas
    │       ├── security/                 # Filtro JWT, TokenProvider, UserPrincipal y SecurityConfig
    │       └── seed/                     # DataSeeder con precarga de 8 cursos y 3 usuarios
    └── test/java/com/lms/bs/
        ├── service/                      # Tests unitarios JUnit 5 (Enrollment, TaskProgress, Auth)
        └── controller/                   # Tests de integración de seguridad MockMvc
```

---

## 🔌 Catálogo de Endpoints REST (Puerto 8081)

### 1. Autenticación Pública
- `POST /auth/login` o `POST /api/v1/auth/login`: Autentica credenciales y emite token JWT.

### 2. Catálogo de Cursos (Público)
- `GET /courses` o `GET /api/v1/courses`: Listado de cursos (permite parámetro opcional `?search=...`).
- `GET /courses/{id}` o `GET /api/v1/courses/{id}`: Detalle de un curso específico.
- `GET /courses/{courseId}/tasks`: Tareas asociadas a un curso.
- `GET /courses/{courseId}/tasks/{taskId}`: Detalle de una tarea.

### 3. Inscripciones del Usuario (Requiere Bearer Token JWT)
- `GET /me/enrollments` o `GET /api/v1/me/enrollments`: Cursos donde el usuario actual está inscrito.
- `POST /me/enrollments/{courseId}`: Inscribirse a un curso (retorna 409 si ya está inscrito).
- `DELETE /me/enrollments/{courseId}`: Retirarse de un curso.

### 4. Progreso de Tareas del Usuario (Requiere Bearer Token JWT)
- `GET /me/tasks?courseId={id}`: Tareas del curso con el estado completado/pendiente del usuario actual.
- `POST /me/tasks/{taskId}/complete`: Marcar tarea como completada.
- `DELETE /me/tasks/{taskId}/complete`: Desmarcar tarea (volver a pendiente).

---

## 👥 Credenciales Seed Disponibles

| Usuario / Correo | Contraseña | Rol | Nombre Completo |
|---|---|---|---|
| `estudiante@minilms.com` | `Password123!` | `ROLE_STUDENT` | Juan Pérez |
| `maria@minilms.com` | `Password123!` | `ROLE_STUDENT` | María García |
| `admin@minilms.com` | `Admin123!` | `ROLE_ADMIN` | Administrador Sistema |

---

## 🧪 Ejecución de Pruebas Unitarias

Para ejecutar la suite completa de tests de la capa de servicio y seguridad:

```bash
gradle test
```

### Casos de prueba verificados:
- **`EnrollmentServiceTest`**: Inscripción exitosa, duplicada (espera `DuplicateEnrollmentException`), retiro y cálculo de porcentaje.
- **`TaskProgressServiceTest`**: Marcar tarea completada, desmarcar a pendiente y aislamiento entre usuarios.
- **`AuthServiceTest`**: Login con credenciales válidas y rechazo con credenciales erróneas.
- **`SecurityMeEndpointIntegrationTest`**: Comprobación de que `/me/**` sin token retorna `401 Unauthorized`.

---

## 🚀 Ejecución en Local

```bash
# Iniciar el servicio en el puerto 8081
gradle bootRun
```

- **Swagger UI:** [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
- **Consola H2:** [http://localhost:8081/h2-console](http://localhost:8081/h2-console)
  - JDBC URL: `jdbc:h2:mem:lms_bs_db`
  - User: `sa`
  - Password: *(vacío)*
