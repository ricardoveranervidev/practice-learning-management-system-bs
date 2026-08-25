package com.lms.bs.controller;

import com.lms.bs.dto.ApiResponse;
import com.lms.bs.dto.EnrollmentDto;
import com.lms.bs.security.UserPrincipal;
import com.lms.bs.service.EnrollmentService;
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
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<EnrollmentDto>>> getMyEnrollments(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<EnrollmentDto> enrollments = enrollmentService.getUserEnrollments(principal.getId());
        String correlationId = MDC.get("correlationId");
        return ResponseEntity.ok(ApiResponse.ok(enrollments, "Inscripciones obtenidas con éxito", correlationId));
    }

    @PostMapping("/{courseId}")
    public ResponseEntity<ApiResponse<EnrollmentDto>> enroll(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserPrincipal principal) {
        EnrollmentDto enrollment = enrollmentService.enroll(principal.getId(), courseId);
        String correlationId = MDC.get("correlationId");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(enrollment, "Inscripción realizada con éxito", correlationId));
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<ApiResponse<Void>> withdraw(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserPrincipal principal) {
        enrollmentService.withdraw(principal.getId(), courseId);
        String correlationId = MDC.get("correlationId");
        return ResponseEntity.ok(ApiResponse.ok(null, "Inscripción cancelada con éxito", correlationId));
    }
}
