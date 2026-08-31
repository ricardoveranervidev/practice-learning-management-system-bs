package com.lms.bs.controller;

import com.lms.bs.dto.ApiResponse;
import com.lms.bs.dto.AuthRequest;
import com.lms.bs.dto.AuthResponse;
import com.lms.bs.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints para inicio de sesión y gestión de credenciales JWT")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(
        summary = "Iniciar sesión",
        description = "Valida las credenciales del usuario (email y contraseña) y retorna un token JWT válido junto con la información del usuario autenticado."
    )
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        String correlationId = MDC.get("correlationId");
        return ResponseEntity.ok(ApiResponse.ok(response, "Inicio de sesión exitoso", correlationId));
    }
}
