package com.lms.bs.service;

import com.lms.bs.domain.entity.Role;
import com.lms.bs.domain.entity.User;
import com.lms.bs.dto.AuthRequest;
import com.lms.bs.dto.AuthResponse;
import com.lms.bs.exception.UnauthorizedException;
import com.lms.bs.repository.UserRepository;
import com.lms.bs.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("estudiante@minilms.com")
                .password("encodedPassword123")
                .fullName("Juan Pérez")
                .role(Role.ROLE_STUDENT)
                .build();
    }

    @Test
    @DisplayName("Login exitoso con credenciales correctas retorna token JWT")
    void testLoginSuccess() {
        AuthRequest request = new AuthRequest("estudiante@minilms.com", "Password123!");

        when(userRepository.findByUsername("estudiante@minilms.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Password123!", "encodedPassword123")).thenReturn(true);
        when(tokenProvider.generateToken(1L, "estudiante@minilms.com", "Juan Pérez", "ROLE_STUDENT"))
                .thenReturn("mocked-jwt-token");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getToken());
        assertEquals("estudiante@minilms.com", response.getUsername());
        assertEquals("Juan Pérez", response.getFullName());
    }

    @Test
    @DisplayName("Login con contraseña incorrecta lanza UnauthorizedException")
    void testLoginInvalidPasswordThrowsException() {
        AuthRequest request = new AuthRequest("estudiante@minilms.com", "WrongPassword");

        when(userRepository.findByUsername("estudiante@minilms.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("WrongPassword", "encodedPassword123")).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> authService.login(request));
    }

    @Test
    @DisplayName("Login con usuario no existente lanza UnauthorizedException")
    void testLoginUserNotFoundThrowsException() {
        AuthRequest request = new AuthRequest("desconocido@minilms.com", "Password123!");

        when(userRepository.findByUsername("desconocido@minilms.com")).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class, () -> authService.login(request));
    }
}
