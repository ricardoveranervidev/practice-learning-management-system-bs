package com.lms.bs.controller;

import com.lms.bs.dto.ApiResponse;
import com.lms.bs.dto.EnrollmentDto;
import com.lms.bs.security.UserPrincipal;
import com.lms.bs.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/me/enrollments")
@RequiredArgsConstructor
@Tag(name = "Inscripciones", description = "Endpoints para la gestión de inscripciones de cursos del estudiante autenticado")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @GetMapping
    @Operation(
        summary = "Listar mis cursos inscritos",
        description = "Retorna el historial completo de inscripciones activas del estudiante autenticado con métricas de progreso de cada curso."
    )
    public ResponseEntity<ApiResponse<List<EnrollmentDto>>> getMyEnrollments(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<EnrollmentDto> enrollments = enrollmentService.getUserEnrollments(principal.getId());
        String correlationId = MDC.get("correlationId");
        return ResponseEntity.ok(ApiResponse.ok(enrollments, "Inscripciones obtenidas con éxito", correlationId));
    }

    @PostMapping("/{courseId}")
    @Operation(
        summary = "Inscribirse a un curso",
        description = "Inscribe al estudiante autenticado en el curso especificado. Valida reglas de negocio para evitar inscripciones duplicadas (retorna 409 Conflict si ya está inscrito)."
    )
    public ResponseEntity<ApiResponse<EnrollmentDto>> enroll(
            @Parameter(description = "ID único del curso al cual inscribirse", example = "1")
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserPrincipal principal) {
        EnrollmentDto enrollment = enrollmentService.enroll(principal.getId(), courseId);
        String correlationId = MDC.get("correlationId");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(enrollment, "Inscripción realizada con éxito", correlationId));
    }

    @DeleteMapping("/{courseId}")
    @Operation(
        summary = "Retirarse de un curso",
        description = "Cancela la inscripción del estudiante autenticado en el curso especificado."
    )
    public ResponseEntity<ApiResponse<Void>> withdraw(
            @Parameter(description = "ID único del curso del cual retirarse", example = "1")
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserPrincipal principal) {
        enrollmentService.withdraw(principal.getId(), courseId);
        String correlationId = MDC.get("correlationId");
        return ResponseEntity.ok(ApiResponse.ok(null, "Inscripción cancelada con éxito", correlationId));
    }
}
