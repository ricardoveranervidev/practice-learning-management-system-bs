package com.lms.bs.controller;

import com.lms.bs.dto.ApiResponse;
import com.lms.bs.dto.CourseDto;
import com.lms.bs.dto.TaskDto;
import com.lms.bs.security.UserPrincipal;
import com.lms.bs.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
@Tag(name = "Cursos", description = "Endpoints para consultar el catálogo de cursos y sus tareas asociadas")
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    @Operation(
        summary = "Listar catálogo de cursos",
        description = "Obtiene la lista de todos los cursos disponibles en el sistema con soporte para filtrado por texto (título, descripción o categoría) y estado de inscripción si el usuario está autenticado."
    )
    public ResponseEntity<ApiResponse<List<CourseDto>>> getAllCourses(
            @Parameter(description = "Texto para buscar cursos por título, descripción o categoría")
            @RequestParam(required = false) String search,
            @AuthenticationPrincipal UserPrincipal principal) {
        Long userId = principal != null ? principal.getId() : null;
        List<CourseDto> courses = courseService.getAllCourses(search, userId);
        String correlationId = MDC.get("correlationId");
        return ResponseEntity.ok(ApiResponse.ok(courses, "Listado de cursos obtenido con éxito", correlationId));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener detalle de un curso",
        description = "Retorna la información completa de un curso específico por su identificador (ID), incluyendo su temario y estado de inscripción."
    )
    public ResponseEntity<ApiResponse<CourseDto>> getCourseById(
            @Parameter(description = "ID único del curso", example = "1")
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        Long userId = principal != null ? principal.getId() : null;
        CourseDto course = courseService.getCourseById(id, userId);
        String correlationId = MDC.get("correlationId");
        return ResponseEntity.ok(ApiResponse.ok(course, "Detalle del curso obtenido con éxito", correlationId));
    }

    @GetMapping("/{courseId}/tasks")
    @Operation(
        summary = "Listar tareas de un curso",
        description = "Obtiene todas las lecciones y actividades académicas asociadas a un curso determinado, ordenadas por su posición en el temario."
    )
    public ResponseEntity<ApiResponse<List<TaskDto>>> getTasksByCourseId(
            @Parameter(description = "ID único del curso", example = "1")
            @PathVariable Long courseId) {
        List<TaskDto> tasks = courseService.getTasksByCourseId(courseId);
        String correlationId = MDC.get("correlationId");
        return ResponseEntity.ok(ApiResponse.ok(tasks, "Tareas del curso obtenidas con éxito", correlationId));
    }

    @GetMapping("/{courseId}/tasks/{taskId}")
    @Operation(
        summary = "Obtener detalle de una tarea",
        description = "Retorna la información detallada de una tarea específica perteneciente a un curso."
    )
    public ResponseEntity<ApiResponse<TaskDto>> getTaskById(
            @Parameter(description = "ID único del curso", example = "1")
            @PathVariable Long courseId,
            @Parameter(description = "ID único de la tarea", example = "1")
            @PathVariable Long taskId) {
        TaskDto task = courseService.getTaskById(courseId, taskId);
        String correlationId = MDC.get("correlationId");
        return ResponseEntity.ok(ApiResponse.ok(task, "Detalle de tarea obtenido con éxito", correlationId));
    }
}
