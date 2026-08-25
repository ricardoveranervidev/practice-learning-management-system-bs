package com.lms.bs.service;

import com.lms.bs.domain.entity.*;
import com.lms.bs.dto.TaskProgressDto;
import com.lms.bs.exception.ResourceNotFoundException;
import com.lms.bs.repository.CourseRepository;
import com.lms.bs.repository.TaskProgressRepository;
import com.lms.bs.repository.TaskRepository;
import com.lms.bs.repository.UserRepository;
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
class TaskProgressServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private TaskProgressRepository taskProgressRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private TaskProgressService taskProgressService;

    private User testUser;
    private Course testCourse;
    private Task testTask;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("estudiante@minilms.com")
                .fullName("Juan Pérez")
                .build();

        testCourse = Course.builder()
                .id(10L)
                .title("Spring Boot 3")
                .build();

        testTask = Task.builder()
                .id(50L)
                .course(testCourse)
                .title("Configuración de Proyecto")
                .description("Inicializar dependencias")
                .orderIndex(1)
                .estimatedMinutes(30)
                .mandatory(true)
                .build();
    }

    @Test
    @DisplayName("Marcar tarea como completada crea o actualiza TaskProgress a true")
    void testCompleteTask() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(taskRepository.findById(50L)).thenReturn(Optional.of(testTask));
        when(taskProgressRepository.findByUserIdAndTaskId(1L, 50L)).thenReturn(Optional.empty());

        TaskProgress savedProgress = TaskProgress.builder()
                .id(1L)
                .user(testUser)
                .task(testTask)
                .completed(true)
                .completedAt(LocalDateTime.now())
                .build();

        when(taskProgressRepository.save(any(TaskProgress.class))).thenReturn(savedProgress);

        TaskProgressDto result = taskProgressService.markTaskComplete(1L, 50L);

        assertNotNull(result);
        assertEquals(50L, result.getTaskId());
        assertTrue(result.getCompleted());
        assertNotNull(result.getCompletedAt());
        verify(taskProgressRepository, times(1)).save(any(TaskProgress.class));
    }

    @Test
    @DisplayName("Desmarcar tarea (volver a pendiente) actualiza TaskProgress a false")
    void testUncompleteTask() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(taskRepository.findById(50L)).thenReturn(Optional.of(testTask));

        TaskProgress existingProgress = TaskProgress.builder()
                .id(1L)
                .user(testUser)
                .task(testTask)
                .completed(true)
                .completedAt(LocalDateTime.now())
                .build();

        when(taskProgressRepository.findByUserIdAndTaskId(1L, 50L)).thenReturn(Optional.of(existingProgress));

        TaskProgress savedProgress = TaskProgress.builder()
                .id(1L)
                .user(testUser)
                .task(testTask)
                .completed(false)
                .completedAt(null)
                .build();

        when(taskProgressRepository.save(any(TaskProgress.class))).thenReturn(savedProgress);

        TaskProgressDto result = taskProgressService.unmarkTaskComplete(1L, 50L);

        assertNotNull(result);
        assertEquals(50L, result.getTaskId());
        assertFalse(result.getCompleted());
        assertNull(result.getCompletedAt());
        verify(taskProgressRepository, times(1)).save(existingProgress);
    }

    @Test
    @DisplayName("Completar tarea inexistente lanza ResourceNotFoundException")
    void testCompleteTaskNotFoundThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskProgressService.markTaskComplete(1L, 999L));
    }

    @Test
    @DisplayName("Obtener tareas con progreso muestra estado específico de cada usuario")
    void testGetTasksWithUserProgress() {
        when(courseRepository.existsById(10L)).thenReturn(true);
        when(taskRepository.findByCourseIdOrderByOrderIndexAsc(10L)).thenReturn(List.of(testTask));

        TaskProgress tp = TaskProgress.builder()
                .id(1L)
                .user(testUser)
                .task(testTask)
                .completed(true)
                .completedAt(LocalDateTime.now())
                .build();

        when(taskProgressRepository.findByUserIdAndTaskCourseId(1L, 10L)).thenReturn(List.of(tp));

        List<TaskProgressDto> list = taskProgressService.getTasksWithUserProgress(1L, 10L);

        assertEquals(1, list.size());
        assertTrue(list.get(0).getCompleted());
        assertEquals("Configuración de Proyecto", list.get(0).getTitle());
    }
}
