package com.lms.bs.repository;

import com.lms.bs.domain.entity.TaskProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskProgressRepository extends JpaRepository<TaskProgress, Long> {
    Optional<TaskProgress> findByUserIdAndTaskId(Long userId, Long taskId);
    List<TaskProgress> findByUserIdAndTaskCourseId(Long userId, Long courseId);

    @Query("SELECT COUNT(tp) FROM TaskProgress tp WHERE tp.user.id = :userId AND tp.task.course.id = :courseId AND tp.completed = true")
    long countCompletedTasksByUserAndCourse(@Param("userId") Long userId, @Param("courseId") Long courseId);
}
