package com.lms.bs.service.impl;

import com.lms.bs.domain.entity.Course;
import com.lms.bs.domain.entity.Enrollment;
import com.lms.bs.domain.entity.EnrollmentStatus;
import com.lms.bs.domain.entity.User;
import com.lms.bs.dto.EnrollmentDto;
import com.lms.bs.exception.DuplicateEnrollmentException;
import com.lms.bs.exception.ResourceNotFoundException;
import com.lms.bs.repository.CourseRepository;
import com.lms.bs.repository.EnrollmentRepository;
import com.lms.bs.repository.TaskProgressRepository;
import com.lms.bs.repository.TaskRepository;
import com.lms.bs.repository.UserRepository;
import com.lms.bs.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private static final Logger log = LoggerFactory.getLogger(EnrollmentServiceImpl.class);

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final TaskRepository taskRepository;
    private final TaskProgressRepository taskProgressRepository;

    @Override
    @Transactional
    public EnrollmentDto enroll(Long userId, Long courseId) {
        log.info("[ENROLL-SVC] Iniciando inscripción para userId={}, courseId={}", userId, courseId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + userId));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con id: " + courseId));

        Optional<Enrollment> existingEnrollment = enrollmentRepository.findByUserIdAndCourseId(userId, courseId);
        if (existingEnrollment.isPresent()) {
            Enrollment enrollment = existingEnrollment.get();
            if (enrollment.getStatus() == EnrollmentStatus.ACTIVE) {
                log.warn("[ENROLL-SVC] Intento de inscripción duplicada: userId={}, courseId={}", userId, courseId);
                throw new DuplicateEnrollmentException("Ya estás inscrito en el curso: " + course.getTitle());
            } else {
                // Reactivar inscripción previamente cancelada
                enrollment.setStatus(EnrollmentStatus.ACTIVE);
                enrollment.setEnrolledAt(LocalDateTime.now());
                Enrollment saved = enrollmentRepository.save(enrollment);
                log.info("[ENROLL-SVC] Inscripción reactivada con éxito: id={}", saved.getId());
                return mapToDto(saved);
            }
        }

        Enrollment enrollment = Enrollment.builder()
                .user(user)
                .course(course)
                .status(EnrollmentStatus.ACTIVE)
                .enrolledAt(LocalDateTime.now())
                .build();

        Enrollment saved = enrollmentRepository.save(enrollment);
        log.info("[ENROLL-SVC] Inscripción creada con éxito: id={}", saved.getId());

        return mapToDto(saved);
    }

    @Override
    @Transactional
    public void withdraw(Long userId, Long courseId) {
        log.info("[ENROLL-SVC] Retirando usuario userId={} del curso courseId={}", userId, courseId);

        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("No estás inscrito en el curso con id: " + courseId));

        if (enrollment.getStatus() == EnrollmentStatus.CANCELLED) {
            log.warn("[ENROLL-SVC] El usuario ya se encontraba retirado del curso id={}", courseId);
            return;
        }

        enrollment.setStatus(EnrollmentStatus.CANCELLED);
        enrollmentRepository.save(enrollment);
        log.info("[ENROLL-SVC] Retiro completado con éxito para userId={}, courseId={}", userId, courseId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentDto> getUserEnrollments(Long userId) {
        log.info("[ENROLL-SVC] Obteniendo inscripciones activas para userId={}", userId);
        List<Enrollment> enrollments = enrollmentRepository.findByUserIdAndStatus(userId, EnrollmentStatus.ACTIVE);

        return enrollments.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private EnrollmentDto mapToDto(Enrollment enrollment) {
        Course course = enrollment.getCourse();
        Long userId = enrollment.getUser().getId();
        long totalTasks = taskRepository.countByCourseId(course.getId());
        long completedTasks = taskProgressRepository.countCompletedTasksByUserAndCourse(userId, course.getId());

        double progress = totalTasks > 0 ? ((double) completedTasks / totalTasks) * 100.0 : 0.0;
        progress = Math.round(progress * 10.0) / 10.0;

        return EnrollmentDto.builder()
                .id(enrollment.getId())
                .courseId(course.getId())
                .courseTitle(course.getTitle())
                .courseDescription(course.getDescription())
                .category(course.getCategory())
                .instructor(course.getInstructor())
                .imageUrl(course.getImageUrl())
                .enrolledAt(enrollment.getEnrolledAt())
                .status(enrollment.getStatus())
                .completedTasksCount(completedTasks)
                .totalTasksCount(totalTasks)
                .progressPercentage(progress)
                .build();
    }
}
