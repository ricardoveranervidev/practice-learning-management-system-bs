package com.lms.bs.controller;

import com.lms.bs.dto.ApiResponse;
import com.lms.bs.dto.CourseDto;
import com.lms.bs.dto.TaskDto;
import com.lms.bs.security.UserPrincipal;
import com.lms.bs.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseDto>>> getAllCourses(
            @RequestParam(required = false) String search,
            @AuthenticationPrincipal UserPrincipal principal) {
        Long userId = principal != null ? principal.getId() : null;
        List<CourseDto> courses = courseService.getAllCourses(search, userId);
        String correlationId = MDC.get("correlationId");
        return ResponseEntity.ok(ApiResponse.ok(courses, "Listado de cursos obtenido con éxito", correlationId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseDto>> getCourseById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        Long userId = principal != null ? principal.getId() : null;
        CourseDto course = courseService.getCourseById(id, userId);
        String correlationId = MDC.get("correlationId");
        return ResponseEntity.ok(ApiResponse.ok(course, "Detalle del curso obtenido con éxito", correlationId));
    }

    @GetMapping("/{courseId}/tasks")
    public ResponseEntity<ApiResponse<List<TaskDto>>> getTasksByCourseId(@PathVariable Long courseId) {
        List<TaskDto> tasks = courseService.getTasksByCourseId(courseId);
        String correlationId = MDC.get("correlationId");
        return ResponseEntity.ok(ApiResponse.ok(tasks, "Tareas del curso obtenidas con éxito", correlationId));
    }

    @GetMapping("/{courseId}/tasks/{taskId}")
    public ResponseEntity<ApiResponse<TaskDto>> getTaskById(
            @PathVariable Long courseId,
            @PathVariable Long taskId) {
        TaskDto task = courseService.getTaskById(courseId, taskId);
        String correlationId = MDC.get("correlationId");
        return ResponseEntity.ok(ApiResponse.ok(task, "Detalle de tarea obtenido con éxito", correlationId));
    }
}
