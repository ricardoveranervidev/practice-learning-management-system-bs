package com.lms.bs.service.impl;

import com.lms.bs.domain.entity.Course;
import com.lms.bs.domain.entity.EnrollmentStatus;
import com.lms.bs.domain.entity.Task;
import com.lms.bs.dto.CourseDto;
import com.lms.bs.dto.TaskDto;
import com.lms.bs.exception.ResourceNotFoundException;
import com.lms.bs.repository.CourseRepository;
import com.lms.bs.repository.EnrollmentRepository;
import com.lms.bs.repository.TaskRepository;
import com.lms.bs.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private static final Logger log = LoggerFactory.getLogger(CourseServiceImpl.class);

    private final CourseRepository courseRepository;
    private final TaskRepository taskRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CourseDto> getAllCourses(String query, Long currentUserId) {
        log.info("[COURSE-SVC] Obteniendo lista de cursos. Query={}, userId={}", query, currentUserId);
        List<Course> courses;
        if (query != null && !query.trim().isEmpty()) {
            courses = courseRepository.searchCourses(query.trim());
        } else {
            courses = courseRepository.findAll();
        }

        return courses.stream()
                .map(course -> mapToDto(course, currentUserId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CourseDto getCourseById(Long id, Long currentUserId) {
        log.info("[COURSE-SVC] Obteniendo detalle de curso id={}", id);
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con id: " + id));
        return mapToDto(course, currentUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> getTasksByCourseId(Long courseId) {
        log.info("[COURSE-SVC] Obteniendo tareas para curso id={}", courseId);
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Curso no encontrado con id: " + courseId);
        }
        return taskRepository.findByCourseIdOrderByOrderIndexAsc(courseId).stream()
                .map(this::mapTaskToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TaskDto getTaskById(Long courseId, Long taskId) {
        log.info("[COURSE-SVC] Obteniendo tarea id={} del curso id={}", taskId, courseId);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada con id: " + taskId));

        if (!task.getCourse().getId().equals(courseId)) {
            throw new ResourceNotFoundException("La tarea id " + taskId + " no pertenece al curso id " + courseId);
        }

        return mapTaskToDto(task);
    }

    private CourseDto mapToDto(Course course, Long currentUserId) {
        boolean isEnrolled = false;
        if (currentUserId != null) {
            isEnrolled = enrollmentRepository.existsByUserIdAndCourseIdAndStatus(
                    currentUserId, course.getId(), EnrollmentStatus.ACTIVE);
        }

        int totalTasks = (int) taskRepository.countByCourseId(course.getId());

        return CourseDto.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .category(course.getCategory())
                .instructor(course.getInstructor())
                .durationHours(course.getDurationHours())
                .level(course.getLevel())
                .imageUrl(course.getImageUrl())
                .createdAt(course.getCreatedAt())
                .totalTasks(totalTasks)
                .isEnrolled(isEnrolled)
                .build();
    }

    private TaskDto mapTaskToDto(Task task) {
        return TaskDto.builder()
                .id(task.getId())
                .courseId(task.getCourse().getId())
                .courseTitle(task.getCourse().getTitle())
                .title(task.getTitle())
                .description(task.getDescription())
                .orderIndex(task.getOrderIndex())
                .estimatedMinutes(task.getEstimatedMinutes())
                .mandatory(task.getMandatory())
                .build();
    }
}
