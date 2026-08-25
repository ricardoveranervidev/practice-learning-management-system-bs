package com.lms.bs.seed;

import com.lms.bs.domain.entity.*;
import com.lms.bs.repository.CourseRepository;
import com.lms.bs.repository.EnrollmentRepository;
import com.lms.bs.repository.TaskProgressRepository;
import com.lms.bs.repository.TaskRepository;
import com.lms.bs.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final TaskRepository taskRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final TaskProgressRepository taskProgressRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("[SEED] Iniciando precarga de datos (Seed)...");

        if (userRepository.count() > 0) {
            log.info("[SEED] Base de datos ya cuenta con datos. Omitiendo seed.");
            return;
        }

        // 1. Precargar Usuarios
        User user1 = User.builder()
                .username("estudiante@minilms.com")
                .password(passwordEncoder.encode("Password123!"))
                .fullName("Juan Pérez")
                .role(Role.ROLE_STUDENT)
                .createdAt(LocalDateTime.now().minusDays(10))
                .build();

        User user2 = User.builder()
                .username("maria@minilms.com")
                .password(passwordEncoder.encode("Password123!"))
                .fullName("María García")
                .role(Role.ROLE_STUDENT)
                .createdAt(LocalDateTime.now().minusDays(8))
                .build();

        User admin = User.builder()
                .username("admin@minilms.com")
                .password(passwordEncoder.encode("Admin123!"))
                .fullName("Administrador Sistema")
                .role(Role.ROLE_ADMIN)
                .createdAt(LocalDateTime.now().minusDays(30))
                .build();

        userRepository.saveAll(List.of(user1, user2, admin));
        log.info("[SEED] 3 usuarios creados: estudiante@minilms.com, maria@minilms.com, admin@minilms.com");

        // 2. Precargar 8 Cursos con Tareas
        List<Course> courses = new ArrayList<>();

        courses.add(createCourse(
                "Spring Boot 3 y Microservicios Empresariales",
                "Domina el desarrollo de microservicios robustos con Spring Boot 3, Spring Security, JPA, Spring Cloud y arquitecturas limpias desacopladas.",
                "Backend",
                "Ing. Carlos Mendoza",
                40,
                CourseLevel.ADVANCED,
                "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=800",
                List.of(
                        createTaskData("Configuración del proyecto y dependencias en Gradle", "Inicializar Spring Boot 3 con Spring Web y Data JPA", 1, 45, true),
                        createTaskData("Modelado del dominio y entidades JPA con H2", "Crear las entidades relacionales y repositorios Spring Data", 2, 60, true),
                        createTaskData("Implementación de seguridad JWT y Filtros MDC", "Configurar Spring Security 6 y trazabilidad de logs con correlation ID", 3, 90, true),
                        createTaskData("Construcción de API REST y manejo de excepciones", "Desarrollar controladores y @ControllerAdvice para respuestas uniformes", 4, 75, true),
                        createTaskData("Pruebas unitarias con JUnit 5 y Mockito", "Implementar cobertura de pruebas en capa de servicios", 5, 60, false)
                )
        ));

        courses.add(createCourse(
                "Angular 18/19: Arquitectura Frontend Moderna y Standalone",
                "Aprende a construir aplicaciones web SPA de alto rendimiento con Angular, Signals, Standalone Components y control de estado reactivo.",
                "Frontend",
                "Lic. Sofía Ramírez",
                35,
                CourseLevel.INTERMEDIATE,
                "https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=800",
                List.of(
                        createTaskData("Configuración del proyecto Angular y Standalone Components", "Crear estructura modular limpia sin NgModules obsoletos", 1, 40, true),
                        createTaskData("Implementación de Servicios y Signals reactivos", "Gestionar estado de autenticación y cursos de forma reactiva", 2, 60, true),
                        createTaskData("HTTP Interceptors para JWT y Trace ID", "Inyectar token y propagar X-Correlation-ID en todas las peticiones", 3, 50, true),
                        createTaskData("Diseño de componentes UI responsivos", "Construir vistas para catálogo, detalle y tablero de progreso", 4, 80, true)
                )
        ));

        courses.add(createCourse(
                "Patrones de Arquitectura Backend: BFF y Microservicios",
                "Comprende a fondo el patrón Backend For Frontend (BFF), orquestación de servicios, agregación de respuestas y trazabilidad distribuida.",
                "Arquitectura",
                "MSc. Roberto Silva",
                25,
                CourseLevel.ADVANCED,
                "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=800",
                List.of(
                        createTaskData("Fundamentos y ventajas del patrón BFF", "Análisis comparativo de monolitos vs microservicios vs BFF", 1, 30, true),
                        createTaskData("Diseño de contratos OpenAPI y Swagger", "Definir endpoints y documentación interactiva del BFF", 2, 45, true),
                        createTaskData("Comunicación cliente RestClient y propagación de headers", "Configurar cliente tipado para llamar al servicio de negocio BS", 3, 60, true),
                        createTaskData("Manejo de resiliencia y timeouts", "Garantizar estabilidad ante caídas de servicios downstream", 4, 50, false)
                )
        ));

        courses.add(createCourse(
                "Docker y Contenerización para Desarrolladores Fullstack",
                "Aprende a empaquetar tus aplicaciones Spring Boot y Angular en contenedores Docker y orquestarlas con Docker Compose.",
                "DevOps",
                "Ing. Fernando Torres",
                30,
                CourseLevel.INTERMEDIATE,
                "https://images.unsplash.com/photo-1605745341112-85968b19335b?w=800",
                List.of(
                        createTaskData("Creación de Dockerfile multi-stage para Spring Boot", "Optimizar tamaño de imágenes Java usando capas", 1, 45, true),
                        createTaskData("Creación de Dockerfile para Angular con Nginx", "Configurar servidor Nginx para servir la SPA y manejar rutas", 2, 45, true),
                        createTaskData("Orquestación multi-contenedor con docker-compose", "Levantar BS, BFF y Frontend en una sola red virtual", 3, 60, true)
                )
        ));

        courses.add(createCourse(
                "Bases de Datos Relacionales y Optimización JPA / Hibernate",
                "Estrategias avanzadas de diseño de esquemas, consultas JPQL eficientes, índices y solución de problemas N+1.",
                "Data",
                "Dra. Elena Castro",
                20,
                CourseLevel.INTERMEDIATE,
                "https://images.unsplash.com/photo-1544383835-bda2bc66a55d?w=800",
                List.of(
                        createTaskData("Diseño de esquemas y restricciones de integridad", "Modelar llaves compuestas y restricciones únicas", 1, 40, true),
                        createTaskData("Estrategias de Fetching: Lazy vs Eager", "Evitar el problema de N+1 queries en JPA", 2, 50, true),
                        createTaskData("Transaccionalidad y niveles de aislamiento", "Uso correcto de @Transactional y manejo de concurrencia", 3, 45, true)
                )
        ));

        courses.add(createCourse(
                "Seguridad Web y Autenticación JWT en Java",
                "Implementación profesional de seguridad web, tokens JWT, criptografía de contraseñas con BCrypt y control de acceso RBAC.",
                "Seguridad",
                "Ing. Andrés Morales",
                18,
                CourseLevel.ADVANCED,
                "https://images.unsplash.com/photo-1563986768609-322da13575f3?w=800",
                List.of(
                        createTaskData("Fundamentos de JWT: Header, Payload y Firma", "Estructura de tokens y algoritmos HMAC SHA-256", 1, 30, true),
                        createTaskData("Filtro de autenticación stateless en Spring Security", "Interceptar peticiones y validar tokens Bearer", 2, 60, true),
                        createTaskData("Control de acceso basado en roles (@PreAuthorize)", "Restricción de endpoints según perfil de usuario", 3, 40, true)
                )
        ));

        courses.add(createCourse(
                "Fundamentos de Java 21 y Programación Funcional",
                "Aprende las características modernas de Java: Records, Pattern Matching, Streams, Lambdas y Virtual Threads.",
                "Backend",
                "Lic. Diego Navarro",
                22,
                CourseLevel.BEGINNER,
                "https://images.unsplash.com/photo-1537884944318-390069bb8665?w=800",
                List.of(
                        createTaskData("Sintaxis moderna de Java y Records", "Definición inmutable de modelos de datos", 1, 35, true),
                        createTaskData("Uso de Streams y Lambdas para procesamiento", "Transformación y filtrado declarativo de colecciones", 2, 50, true),
                        createTaskData("Manejo de Optional y programación defensiva", "Evitar NullPointerExceptions en código productivo", 3, 40, true)
                )
        ));

        courses.add(createCourse(
                "Testing Automatizado en Backend con JUnit 5 y Mockito",
                "Guía práctica para escribir pruebas unitarias y de integración efectivas, mocks, aserciones y pruebas de controladores.",
                "QA",
                "Ing. Valeria Rojas",
                16,
                CourseLevel.BEGINNER,
                "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=800",
                List.of(
                        createTaskData("Estructura de un test unitario: Given-When-Then", "Escribir pruebas limpias y legibles con JUnit 5", 1, 30, true),
                        createTaskData("Mocking de dependencias con Mockito (@Mock, @InjectMocks)", "Aislar la capa de servicio de repositorios", 2, 50, true),
                        createTaskData("Pruebas de endpoints con MockMvc", "Validar códigos de estado HTTP y estructura JSON de respuestas", 3, 45, true)
                )
        ));

        for (Course c : courses) {
            Course savedCourse = courseRepository.save(c);
            for (Task t : c.getTasks()) {
                t.setCourse(savedCourse);
                taskRepository.save(t);
            }
        }
        log.info("[SEED] 8 cursos y sus tareas precargados con éxito.");

        // 3. Crear inscripciones de prueba para usuario 1
        Course springCourse = courses.get(0);
        Course angularCourse = courses.get(1);

        Enrollment enr1 = Enrollment.builder()
                .user(user1)
                .course(springCourse)
                .status(EnrollmentStatus.ACTIVE)
                .enrolledAt(LocalDateTime.now().minusDays(3))
                .build();

        Enrollment enr2 = Enrollment.builder()
                .user(user1)
                .course(angularCourse)
                .status(EnrollmentStatus.ACTIVE)
                .enrolledAt(LocalDateTime.now().minusDays(2))
                .build();

        enrollmentRepository.saveAll(List.of(enr1, enr2));

        // 4. Marcar algunas tareas como completadas para usuario 1
        List<Task> springTasks = taskRepository.findByCourseIdOrderByOrderIndexAsc(springCourse.getId());
        if (!springTasks.isEmpty()) {
            TaskProgress tp1 = TaskProgress.builder()
                    .user(user1)
                    .task(springTasks.get(0))
                    .completed(true)
                    .completedAt(LocalDateTime.now().minusDays(2))
                    .build();
            TaskProgress tp2 = TaskProgress.builder()
                    .user(user1)
                    .task(springTasks.get(1))
                    .completed(true)
                    .completedAt(LocalDateTime.now().minusDays(1))
                    .build();
            taskProgressRepository.saveAll(List.of(tp1, tp2));
        }

        log.info("[SEED] Precarga de datos completada satisfactoriamente.");
    }

    private Course createCourse(String title, String desc, String category, String instructor,
                                int duration, CourseLevel level, String img, List<TaskData> taskDataList) {
        Course course = Course.builder()
                .title(title)
                .description(desc)
                .category(category)
                .instructor(instructor)
                .durationHours(duration)
                .level(level)
                .imageUrl(img)
                .createdAt(LocalDateTime.now().minusDays(15))
                .build();

        List<Task> tasks = new ArrayList<>();
        for (TaskData td : taskDataList) {
            Task task = Task.builder()
                    .course(course)
                    .title(td.title)
                    .description(td.description)
                    .orderIndex(td.orderIndex)
                    .estimatedMinutes(td.estimatedMinutes)
                    .mandatory(td.mandatory)
                    .build();
            tasks.add(task);
        }
        course.setTasks(tasks);
        return course;
    }

    private TaskData createTaskData(String title, String desc, int order, int minutes, boolean mandatory) {
        return new TaskData(title, desc, order, minutes, mandatory);
    }

    private static class TaskData {
        String title;
        String description;
        int orderIndex;
        int estimatedMinutes;
        boolean mandatory;

        TaskData(String title, String description, int orderIndex, int estimatedMinutes, boolean mandatory) {
            this.title = title;
            this.description = description;
            this.orderIndex = orderIndex;
            this.estimatedMinutes = estimatedMinutes;
            this.mandatory = mandatory;
        }
    }
}
