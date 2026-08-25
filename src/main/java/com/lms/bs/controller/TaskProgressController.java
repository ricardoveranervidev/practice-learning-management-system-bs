package com.lms.bs.controller;

import com.lms.bs.dto.ApiResponse;
import com.lms.bs.dto.TaskProgressDto;
import com.lms.bs.security.UserPrincipal;
import com.lms.bs.service.TaskProgressService;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/me/tasks")
@RequiredArgsConstructor
public class TaskProgressController {

    private final TaskProgressService taskProgressService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TaskProgressDto>>> getMyTasksForCourse(
            @RequestParam Long courseId,
            @AuthenticationPrincipal UserPrincipal principal) {
        List<TaskProgressDto> tasks = taskProgressService.getTasksWithUserProgress(principal.getId(), courseId);
        String correlationId = MDC.get("correlationId");
        return ResponseEntity.ok(ApiResponse.ok(tasks, "Progreso de tareas obtenido con éxito", correlationId));
    }

    @PostMapping("/{taskId}/complete")
    public ResponseEntity<ApiResponse<TaskProgressDto>> completeTask(
            @PathVariable Long taskId,
            @AuthenticationPrincipal UserPrincipal principal) {
        TaskProgressDto progress = taskProgressService.markTaskComplete(principal.getId(), taskId);
        String correlationId = MDC.get("correlationId");
        return ResponseEntity.ok(ApiResponse.ok(progress, "Tarea marcada como completada con éxito", correlationId));
    }

    @DeleteMapping("/{taskId}/complete")
    public ResponseEntity<ApiResponse<TaskProgressDto>> uncompleteTask(
            @PathVariable Long taskId,
            @AuthenticationPrincipal UserPrincipal principal) {
        TaskProgressDto progress = taskProgressService.unmarkTaskComplete(principal.getId(), taskId);
        String correlationId = MDC.get("correlationId");
        return ResponseEntity.ok(ApiResponse.ok(progress, "Tarea desmarcada con éxito (volver a pendiente)", correlationId));
    }
}
