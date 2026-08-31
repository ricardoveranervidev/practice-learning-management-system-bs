package com.lms.bs.controller;

import com.lms.bs.dto.ApiResponse;
import com.lms.bs.dto.TaskProgressDto;
import com.lms.bs.security.UserPrincipal;
import com.lms.bs.service.TaskProgressService;
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
@RequestMapping("/api/v1/me/tasks")
@RequiredArgsConstructor
@Tag(name = "Progreso de Tareas", description = "Endpoints para el seguimiento individual y checklist de tareas por estudiante")
public class TaskProgressController {

    private final TaskProgressService taskProgressService;

    @GetMapping
    @Operation(
        summary = "Consultar progreso de tareas por curso",
        description = "Obtiene la lista de tareas de un curso junto con el estado individual de avance (completada o pendiente) del estudiante autenticado."
    )
    public ResponseEntity<ApiResponse<List<TaskProgressDto>>> getMyTasksForCourse(
            @Parameter(description = "ID del curso a consultar", example = "1")
            @RequestParam Long courseId,
            @AuthenticationPrincipal UserPrincipal principal) {
        List<TaskProgressDto> tasks = taskProgressService.getTasksWithUserProgress(principal.getId(), courseId);
        String correlationId = MDC.get("correlationId");
        return ResponseEntity.ok(ApiResponse.ok(tasks, "Progreso de tareas obtenido con éxito", correlationId));
    }

    @PostMapping("/{taskId}/complete")
    @Operation(
        summary = "Marcar tarea como completada",
        description = "Registra una tarea del curso como completada para el estudiante autenticado, guardando la fecha y hora de finalización."
    )
    public ResponseEntity<ApiResponse<TaskProgressDto>> completeTask(
            @Parameter(description = "ID único de la tarea a completar", example = "1")
            @PathVariable Long taskId,
            @AuthenticationPrincipal UserPrincipal principal) {
        TaskProgressDto progress = taskProgressService.markTaskComplete(principal.getId(), taskId);
        String correlationId = MDC.get("correlationId");
        return ResponseEntity.ok(ApiResponse.ok(progress, "Tarea marcada como completada con éxito", correlationId));
    }

    @DeleteMapping("/{taskId}/complete")
    @Operation(
        summary = "Desmarcar tarea (volver a pendiente)",
        description = "Revierte el estado de una tarea completada a pendiente para el estudiante autenticado."
    )
    public ResponseEntity<ApiResponse<TaskProgressDto>> uncompleteTask(
            @Parameter(description = "ID único de la tarea a desmarcar", example = "1")
            @PathVariable Long taskId,
            @AuthenticationPrincipal UserPrincipal principal) {
        TaskProgressDto progress = taskProgressService.unmarkTaskComplete(principal.getId(), taskId);
        String correlationId = MDC.get("correlationId");
        return ResponseEntity.ok(ApiResponse.ok(progress, "Tarea desmarcada con éxito (volver a pendiente)", correlationId));
    }
}
