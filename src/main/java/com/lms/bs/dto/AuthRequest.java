package com.lms.bs.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthRequest {

    @NotBlank(message = "El email/username es requerido")
    private String username;

    @NotBlank(message = "La contraseña es requerida")
    private String password;
}
