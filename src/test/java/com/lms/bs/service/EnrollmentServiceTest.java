package com.lms.bs.service;

import com.lms.bs.domain.entity.*;
import com.lms.bs.dto.EnrollmentDto;
import com.lms.bs.exception.DuplicateEnrollmentException;
import com.lms.bs.exception.ResourceNotFoundException;
import com.lms.bs.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private TaskProgressRepository taskProgressRepository;

    @InjectMocks
    private EnrollmentService enrollmentService;

    private User testUser;
    private Course testCourse;
    private Enrollment testEnrollment;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("estudiante@minilms.com")
                .fullName("Juan Pérez")
                .role(Role.ROLE_STUDENT)
                .build();

        testCourse = Course.builder()
                .id(10L)
                .title("Spring Boot 3 y Microservicios")
                .description("Curso avanzado de Spring Boot")
                .category("Backend")
                .instructor("Carlos Mendoza")
                .durationHours(40)
                .level(CourseLevel.ADVANCED)
                .build();

        testEnrollment = Enrollment.builder()
                .id(100L)
                .user(testUser)
                .course(testCourse)
                .status(EnrollmentStatus.ACTIVE)
                .enrolledAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Inscripción exitosa cuando el usuario no está inscrito")
    void testEnrollSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(courseRepository.findById(10L)).thenReturn(Optional.of(testCourse));
        when(enrollmentRepository.findByUserIdAndCourseId(1L, 10L)).thenReturn(Optional.empty());
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(testEnrollment);
        when(taskRepository.countByCourseId(10L)).thenReturn(5L);
        when(taskProgressRepository.countCompletedTasksByUserAndCourse(1L, 10L)).thenReturn(0L);

        EnrollmentDto result = enrollmentService.enroll(1L, 10L);

        assertNotNull(result);
        assertEquals(10L, result.getCourseId());
        assertEquals("Spring Boot 3 y Microservicios", result.getCourseTitle());
        assertEquals(EnrollmentStatus.ACTIVE, result.getStatus());
        assertEquals(0.0, result.getProgressPercentage());
        verify(enrollmentRepository, times(1)).save(any(Enrollment.class));
    }

    @Test
    @DisplayName("Inscripción duplicada debe lanzar DuplicateEnrollmentException")
    void testDuplicateEnrollmentThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(courseRepository.findById(10L)).thenReturn(Optional.of(testCourse));
        when(enrollmentRepository.findByUserIdAndCourseId(1L, 10L)).thenReturn(Optional.of(testEnrollment));

        DuplicateEnrollmentException exception = assertThrows(
                DuplicateEnrollmentException.class,
                () -> enrollmentService.enroll(1L, 10L)
        );

        assertTrue(exception.getMessage().contains("Ya estás inscrito"));
        verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }

    @Test
    @DisplayName("Retiro exitoso de un curso inscrito")
    void testWithdrawSuccess() {
        when(enrollmentRepository.findByUserIdAndCourseId(1L, 10L)).thenReturn(Optional.of(testEnrollment));

        enrollmentService.withdraw(1L, 10L);

        assertEquals(EnrollmentStatus.CANCELLED, testEnrollment.getStatus());
        verify(enrollmentRepository, times(1)).save(testEnrollment);
    }

    @Test
    @DisplayName("Retiro de curso inexistente lanza ResourceNotFoundException")
    void testWithdrawNotFoundThrowsException() {
        when(enrollmentRepository.findByUserIdAndCourseId(1L, 99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> enrollmentService.withdraw(1L, 99L));
    }

    @Test
    @DisplayName("Obtener cursos inscritos calcula porcentaje de progreso correctamente")
    void testGetUserEnrollmentsCalculatesProgress() {
        when(enrollmentRepository.findByUserIdAndStatus(1L, EnrollmentStatus.ACTIVE))
                .thenReturn(List.of(testEnrollment));
        when(taskRepository.countByCourseId(10L)).thenReturn(4L);
        when(taskProgressRepository.countCompletedTasksByUserAndCourse(1L, 10L)).thenReturn(2L);

        List<EnrollmentDto> results = enrollmentService.getUserEnrollments(1L);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(50.0, results.get(0).getProgressPercentage());
        assertEquals(2L, results.get(0).getCompletedTasksCount());
        assertEquals(4L, results.get(0).getTotalTasksCount());
    }
}
