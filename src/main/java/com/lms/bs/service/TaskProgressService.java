package com.lms.bs.service;

import com.lms.bs.domain.entity.Course;
import com.lms.bs.domain.entity.Task;
import com.lms.bs.domain.entity.TaskProgress;
import com.lms.bs.domain.entity.User;
import com.lms.bs.dto.TaskProgressDto;
import com.lms.bs.exception.ResourceNotFoundException;
import com.lms.bs.repository.CourseRepository;
import com.lms.bs.repository.TaskProgressRepository;
import com.lms.bs.repository.TaskRepository;
import com.lms.bs.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskProgressService {

    private static final Logger log = LoggerFactory.getLogger(TaskProgressService.class);

    private final TaskRepository taskRepository;
    private final TaskProgressRepository taskProgressRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @Transactional(readOnly = true)
    public List<TaskProgressDto> getTasksWithUserProgress(Long userId, Long courseId) {
        log.info("[TASK-PROG-SVC] Obteniendo tareas con progreso para userId={}, courseId={}", userId, courseId);

        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Curso no encontrado con id: " + courseId);
        }

        List<Task> tasks = taskRepository.findByCourseIdOrderByOrderIndexAsc(courseId);
        List<TaskProgress> progresses = taskProgressRepository.findByUserIdAndTaskCourseId(userId, courseId);

        Map<Long, TaskProgress> progressMap = progresses.stream()
                .collect(Collectors.toMap(tp -> tp.getTask().getId(), tp -> tp));

        return tasks.stream().map(task -> {
            TaskProgress tp = progressMap.get(task.getId());
            boolean completed = tp != null && Boolean.TRUE.equals(tp.getCompleted());
            LocalDateTime completedAt = tp != null ? tp.getCompletedAt() : null;

            return TaskProgressDto.builder()
                    .taskId(task.getId())
                    .courseId(task.getCourse().getId())
                    .title(task.getTitle())
                    .description(task.getDescription())
                    .orderIndex(task.getOrderIndex())
                    .estimatedMinutes(task.getEstimatedMinutes())
                    .mandatory(task.getMandatory())
                    .completed(completed)
                    .completedAt(completedAt)
                    .build();
        }).collect(Collectors.toList());
    }

    @Transactional
    public TaskProgressDto markTaskComplete(Long userId, Long taskId) {
        log.info("[TASK-PROG-SVC] Marcando tarea como completada: userId={}, taskId={}", userId, taskId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + userId));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada con id: " + taskId));

        Optional<TaskProgress> existingProgress = taskProgressRepository.findByUserIdAndTaskId(userId, taskId);

        TaskProgress progress;
        if (existingProgress.isPresent()) {
            progress = existingProgress.get();
            progress.setCompleted(true);
            progress.setCompletedAt(LocalDateTime.now());
        } else {
            progress = TaskProgress.builder()
                    .user(user)
                    .task(task)
                    .completed(true)
                    .completedAt(LocalDateTime.now())
                    .build();
        }

        TaskProgress saved = taskProgressRepository.save(progress);
        log.info("[TASK-PROG-SVC] Tarea marcada como completada con éxito para userId={}, taskId={}", userId, taskId);

        return TaskProgressDto.builder()
                .taskId(task.getId())
                .courseId(task.getCourse().getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .orderIndex(task.getOrderIndex())
                .estimatedMinutes(task.getEstimatedMinutes())
                .mandatory(task.getMandatory())
                .completed(saved.getCompleted())
                .completedAt(saved.getCompletedAt())
                .build();
    }

    @Transactional
    public TaskProgressDto unmarkTaskComplete(Long userId, Long taskId) {
        log.info("[TASK-PROG-SVC] Desmarcando tarea (volver a pendiente): userId={}, taskId={}", userId, taskId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + userId));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada con id: " + taskId));

        Optional<TaskProgress> existingProgress = taskProgressRepository.findByUserIdAndTaskId(userId, taskId);

        TaskProgress progress;
        if (existingProgress.isPresent()) {
            progress = existingProgress.get();
            progress.setCompleted(false);
            progress.setCompletedAt(null);
        } else {
            progress = TaskProgress.builder()
                    .user(user)
                    .task(task)
                    .completed(false)
                    .completedAt(null)
                    .build();
        }

        TaskProgress saved = taskProgressRepository.save(progress);
        log.info("[TASK-PROG-SVC] Tarea desmarcada (pendiente) con éxito para userId={}, taskId={}", userId, taskId);

        return TaskProgressDto.builder()
                .taskId(task.getId())
                .courseId(task.getCourse().getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .orderIndex(task.getOrderIndex())
                .estimatedMinutes(task.getEstimatedMinutes())
                .mandatory(task.getMandatory())
                .completed(saved.getCompleted())
                .completedAt(null)
                .build();
    }
}
