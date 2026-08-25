package com.lms.bs.service;

import com.lms.bs.domain.entity.User;
import com.lms.bs.dto.AuthRequest;
import com.lms.bs.dto.AuthResponse;
import com.lms.bs.exception.UnauthorizedException;
import com.lms.bs.repository.UserRepository;
import com.lms.bs.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Transactional(readOnly = true)
    public AuthResponse login(AuthRequest request) {
        log.info("[AUTH-SVC] Intento de login para usuario: {}", request.getUsername());

        User user = userRepository.findByUsername(request.getUsername().trim().toLowerCase())
                .orElseThrow(() -> {
                    log.warn("[AUTH-SVC] Usuario no encontrado: {}", request.getUsername());
                    return new UnauthorizedException("Credenciales inválidas. Verifica tu usuario y contraseña.");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("[AUTH-SVC] Contraseña incorrecta para usuario: {}", request.getUsername());
            throw new UnauthorizedException("Credenciales inválidas. Verifica tu usuario y contraseña.");
        }

        String token = tokenProvider.generateToken(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getRole().name()
        );

        log.info("[AUTH-SVC] Login exitoso para userId={}, username={}", user.getId(), user.getUsername());

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .build();
    }
}
